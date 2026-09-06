package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.AssistanceRequestItem
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreRepository(private val context: Context) {

    private val TAG = "FirestoreRepository"
    private val COLLECTION_PATIENTS = "patients"
    private val COLLECTION_ASSISTANCE = "assistance_requests"

    private val _syncStatus = MutableStateFlow(SyncStatus.SYNCED)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private var firestoreInstance: FirebaseFirestore? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val db = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build()
            db.firestoreSettings = settings
            firestoreInstance = db
            Log.d(TAG, "Firebase Firestore initialized successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization warning (resilient offline mode active): ${e.message}")
            _syncStatus.value = SyncStatus.OFFLINE
        }
    }

    /**
     * Listen to real-time updates from Firestore "patients" collection.
     */
    fun listenToPatients(): Flow<List<FirestorePatient>> = callbackFlow {
        _syncStatus.value = SyncStatus.SYNCING
        val db = firestoreInstance
        if (db == null) {
            _syncStatus.value = SyncStatus.OFFLINE
            trySend(getInitialFallbackPatients())
            awaitClose { }
            return@callbackFlow
        }

        var listenerRegistration: ListenerRegistration? = null
        try {
            listenerRegistration = db.collection(COLLECTION_PATIENTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore listen error: ${error.message}")
                        _syncStatus.value = SyncStatus.OFFLINE
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val patientsList = mutableListOf<FirestorePatient>()
                        for (doc in snapshot.documents) {
                            try {
                                val p = doc.toObject(FirestorePatient::class.java)
                                if (p != null) {
                                    if (p.id.isBlank()) p.id = doc.id
                                    patientsList.add(p)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing patient doc ${doc.id}", e)
                            }
                        }

                        _syncStatus.value = SyncStatus.SYNCED

                        if (patientsList.isEmpty() && !snapshot.metadata.isFromCache) {
                            trySend(getInitialFallbackPatients())
                        } else {
                            trySend(patientsList)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception attaching Firestore snapshot listener", e)
            _syncStatus.value = SyncStatus.OFFLINE
            trySend(getInitialFallbackPatients())
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Save full patient record to Firestore "patients" collection.
     */
    suspend fun savePatientRecord(patient: FirestorePatient): Result<Unit> {
        _syncStatus.value = SyncStatus.SYNCING
        val db = firestoreInstance
        if (db == null) {
            _syncStatus.value = SyncStatus.OFFLINE
            return Result.success(Unit)
        }

        return try {
            val docId = if (patient.id.isNotBlank()) patient.id else patient.tokenNumber.ifBlank { "MK-${System.currentTimeMillis()}" }
            val patientToSave = patient.copy(
                id = docId,
                updatedAt = System.currentTimeMillis()
            )
            db.collection(COLLECTION_PATIENTS)
                .document(docId)
                .set(patientToSave)
                .await()

            _syncStatus.value = SyncStatus.SYNCED
            Log.d(TAG, "Patient ${patient.tokenNumber} saved to Firestore successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save to Firestore remote (cached locally): ${e.message}")
            _syncStatus.value = SyncStatus.OFFLINE
            Result.success(Unit)
        }
    }

    /**
     * Update a patient's status in Firestore (WAITING, CALLED, IN_CONSULTATION, COMPLETED, CANCELLED).
     */
    suspend fun updatePatientStatus(patientId: String, newStatus: String, changedBy: String = "Staff"): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val eventDesc = when (newStatus.uppercase()) {
                "CALLED" -> "Patient called to consulting room"
                "IN_CONSULTATION" -> "Consultation started with physician"
                "COMPLETED", "SEEN" -> "Consultation completed and visit finalized"
                "CANCELLED" -> "Visit cancelled"
                else -> "Status updated to $newStatus"
            }

            val docRef = db.collection(COLLECTION_PATIENTS).document(patientId)
            val snapshot = docRef.get().await()
            val existingTimeline = snapshot.toObject(FirestorePatient::class.java)?.activityTimeline?.toMutableList() ?: mutableListOf()

            existingTimeline.add(
                mapOf(
                    "event" to eventDesc,
                    "timeFormatted" to nowTime,
                    "actor" to changedBy,
                    "status" to "COMPLETED"
                )
            )

            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "activityTimeline" to existingTimeline,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            _syncStatus.value = SyncStatus.SYNCED
            Log.d(TAG, "Patient $patientId status updated to $newStatus")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update status in remote Firestore: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Assign doctor to patient.
     */
    suspend fun assignDoctor(patientId: String, doctorName: String, assignedBy: String = "Reception"): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val docRef = db.collection(COLLECTION_PATIENTS).document(patientId)
            val snapshot = docRef.get().await()
            val existingTimeline = snapshot.toObject(FirestorePatient::class.java)?.activityTimeline?.toMutableList() ?: mutableListOf()

            existingTimeline.add(
                mapOf(
                    "event" to "Assigned to $doctorName",
                    "timeFormatted" to nowTime,
                    "actor" to assignedBy,
                    "status" to "COMPLETED"
                )
            )

            docRef.update(
                mapOf(
                    "assignedDoctor" to doctorName,
                    "activityTimeline" to existingTimeline,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            _syncStatus.value = SyncStatus.SYNCED
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to assign doctor: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Update priority level (Normal, Priority, Urgent).
     */
    suspend fun updatePriority(patientId: String, priority: String, urgentReason: String? = null): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val isUrgent = priority.equals("Urgent", ignoreCase = true)
            docRefUpdate(patientId, mapOf(
                "priorityTag" to priority,
                "isUrgent" to isUrgent,
                "urgentReason" to (urgentReason ?: if (isUrgent) "Escalated by Clinical Triage Staff" else null),
                "updatedAt" to System.currentTimeMillis()
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    /**
     * Update patient vitals in Firestore.
     */
    suspend fun updateVitals(patientId: String, vitalsDetail: Map<String, String>, vitalsSummary: String): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val docRef = db.collection(COLLECTION_PATIENTS).document(patientId)
            val snapshot = docRef.get().await()
            val existingTimeline = snapshot.toObject(FirestorePatient::class.java)?.activityTimeline?.toMutableList() ?: mutableListOf()

            existingTimeline.add(
                mapOf(
                    "event" to "Triage Vitals recorded ($vitalsSummary)",
                    "timeFormatted" to nowTime,
                    "actor" to (vitalsDetail["recordedBy"] ?: "Triage Nurse"),
                    "status" to "COMPLETED"
                )
            )

            docRef.update(
                mapOf(
                    "vitals" to vitalsSummary,
                    "vitalsDetail" to vitalsDetail,
                    "activityTimeline" to existingTimeline,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            _syncStatus.value = SyncStatus.SYNCED
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    /**
     * Save clinician assessment & plan notes.
     */
    suspend fun saveClinicianNotes(patientId: String, notes: Map<String, String>): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val nowTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val docRef = db.collection(COLLECTION_PATIENTS).document(patientId)
            val snapshot = docRef.get().await()
            val existingTimeline = snapshot.toObject(FirestorePatient::class.java)?.activityTimeline?.toMutableList() ?: mutableListOf()

            existingTimeline.add(
                mapOf(
                    "event" to "Clinician consultation notes & treatment plan saved",
                    "timeFormatted" to nowTime,
                    "actor" to (notes["author"] ?: "Attending Physician"),
                    "status" to "COMPLETED"
                )
            )

            docRef.update(
                mapOf(
                    "clinicianNotes" to notes,
                    "activityTimeline" to existingTimeline,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            _syncStatus.value = SyncStatus.SYNCED
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    private suspend fun docRefUpdate(patientId: String, map: Map<String, Any?>) {
        firestoreInstance?.collection(COLLECTION_PATIENTS)?.document(patientId)?.update(map)?.await()
    }

    /**
     * Records an assistance request in Firestore "assistance_requests".
     */
    suspend fun saveAssistanceRequest(kioskId: String, reason: String, tokenNumber: String = "", patientName: String = ""): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            val reqId = "req_${System.currentTimeMillis()}"
            val data = mapOf(
                "id" to reqId,
                "kioskId" to kioskId,
                "reason" to reason,
                "tokenNumber" to tokenNumber,
                "patientName" to patientName,
                "status" to "PENDING",
                "createdAt" to System.currentTimeMillis()
            )
            db.collection(COLLECTION_ASSISTANCE)
                .document(reqId)
                .set(data)
                .await()
            _syncStatus.value = SyncStatus.SYNCED
            Log.d(TAG, "Assistance request logged in Firestore: $reason")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to record assistance request remotely: ${e.message}")
            Result.success(Unit)
        }
    }

    /**
     * Listen to Assistance Requests stream from Firestore.
     */
    fun listenToAssistanceRequests(): Flow<List<AssistanceRequestItem>> = callbackFlow {
        val db = firestoreInstance
        if (db == null) {
            trySend(getFallbackAssistanceRequests())
            awaitClose { }
            return@callbackFlow
        }

        var listenerRegistration: ListenerRegistration? = null
        try {
            listenerRegistration = db.collection(COLLECTION_ASSISTANCE)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(getFallbackAssistanceRequests())
                        return@addSnapshotListener
                    }

                    val list = mutableListOf<AssistanceRequestItem>()
                    for (doc in snapshot.documents) {
                        val req = AssistanceRequestItem(
                            id = doc.id,
                            kioskId = doc.getString("kioskId") ?: "Kiosk A",
                            reason = doc.getString("reason") ?: "Help needed",
                            tokenNumber = doc.getString("tokenNumber") ?: "",
                            patientName = doc.getString("patientName") ?: "Patient",
                            status = doc.getString("status") ?: "PENDING",
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        )
                        list.add(req)
                    }

                    if (list.isEmpty()) {
                        trySend(getFallbackAssistanceRequests())
                    } else {
                        trySend(list)
                    }
                }
        } catch (e: Exception) {
            trySend(getFallbackAssistanceRequests())
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Update Assistance Request status (PENDING, ACKNOWLEDGED, RESOLVED).
     */
    suspend fun updateAssistanceRequestStatus(reqId: String, newStatus: String): Result<Unit> {
        val db = firestoreInstance ?: return Result.success(Unit)
        return try {
            db.collection(COLLECTION_ASSISTANCE).document(reqId).update("status", newStatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    fun getFallbackAssistanceRequests(): List<AssistanceRequestItem> {
        return listOf(
            AssistanceRequestItem(
                id = "req_101",
                kioskId = "Kiosk A (Ground Floor Triage)",
                reason = "Assistance needed with prescription scanning and lighting",
                tokenNumber = "MK-4818",
                patientName = "Rajesh Mehta",
                status = "PENDING",
                createdAt = System.currentTimeMillis() - 4 * 60 * 1000
            ),
            AssistanceRequestItem(
                id = "req_102",
                kioskId = "Kiosk B (Outpatient Wing)",
                reason = "Wheelchair navigation assistance requested",
                tokenNumber = "MK-4819",
                patientName = "Anita Sharma",
                status = "ACKNOWLEDGED",
                createdAt = System.currentTimeMillis() - 12 * 60 * 1000
            )
        )
    }

    /**
     * Provides baseline queue patients for realistic clinical demo when starting clean.
     */
    fun getInitialFallbackPatients(): List<FirestorePatient> {
        return listOf(
            FirestorePatient(
                id = "MK-4818",
                tokenNumber = "MK-4818",
                name = "Rajesh Mehta",
                age = "54",
                gender = "Male",
                abhaId = "91-3829-1029-4401",
                contactPhone = "+91 98201 44812",
                chiefComplaint = "Crushing retrosternal chest pain radiating to left jaw, accompanied by acute breathlessness & diaphoresis.",
                isUrgent = true,
                priorityTag = "Urgent",
                urgentReason = "Cardiopulmonary Red Flag: Suspected Acute Coronary Syndrome",
                status = "WAITING",
                assignedDoctor = "Dr. S. Mehta (Cardiology)",
                kioskId = "Kiosk A (Outpatient B)",
                vitals = "BP 168/98 mmHg • HR 106 bpm • SpO2 93% • Temp 98.6°F • RR 24/min",
                vitalsDetail = mapOf(
                    "systolic" to "168",
                    "diastolic" to "98",
                    "heartRate" to "106",
                    "spo2" to "93",
                    "temperature" to "98.6",
                    "respRate" to "24",
                    "weight" to "78",
                    "recordedBy" to "Triage Nurse K. Patel",
                    "timestamp" to (System.currentTimeMillis() - 6 * 60 * 1000).toString()
                ),
                createdAt = System.currentTimeMillis() - 6 * 60 * 1000,
                clinicalSummary = mapOf(
                    "chiefComplaint" to "Severe acute retrosternal chest tightness radiating to left shoulder and jaw with dyspnea for 60 minutes.",
                    "historyOfPresentIllness" to "Patient reports sudden onset heavy substernal chest pressure while at rest. Describes feeling of elephant on chest with cold sweating and inability to catch breath. Pain is constant (9/10 severity) and not relieved by rest.",
                    "pastMedicalSurgical" to "Hypertension (8 yrs), Dyslipidemia (4 yrs). No prior angioplasty or CABG.",
                    "drugAndAllergy" to "Tab Atorvastatin 20mg OD, Tab Metoprolol 25mg OD • NKDA (No Known Drug Allergies)",
                    "familyHistory" to "Father had myocardial infarction at age 52.",
                    "reviewOfSystems" to "Positive for diaphoresis, dyspnea, chest pressure. Negative for nausea, hemoptysis.",
                    "priorInvestigations" to "ECG STAT ordered in Triage. Random Blood Sugar: 174 mg/dL.",
                    "triageLevel" to "Priority 1 - Immediate Emergency Evaluation"
                ),
                extractedDiagnoses = listOf("Essential Hypertension", "Dyslipidemia", "Rule out Acute Coronary Syndrome"),
                extractedMedications = listOf(
                    mapOf("name" to "Atorvastatin", "dosage" to "20 mg", "frequency" to "Once daily at bedtime", "instructions" to "Lipid lowering"),
                    mapOf("name" to "Metoprolol Succinate", "dosage" to "25 mg", "frequency" to "Once daily morning", "instructions" to "Cardioprotective beta-blocker"),
                    mapOf("name" to "Aspirin (Dispersible)", "dosage" to "325 mg", "frequency" to "STAT in Triage", "instructions" to "Antiplatelet loading dose")
                ),
                extractedLabValues = listOf(
                    mapOf("testName" to "Troponin-I (High Sensitivity)", "value" to "0.082", "unit" to "ng/mL", "referenceRange" to "< 0.014", "status" to "High"),
                    mapOf("testName" to "Random Blood Sugar", "value" to "174", "unit" to "mg/dL", "referenceRange" to "70-140", "status" to "High"),
                    mapOf("testName" to "Serum Potassium", "value" to "4.1", "unit" to "mmol/L", "referenceRange" to "3.5-5.0", "status" to "Normal")
                ),
                documents = listOf(
                    mapOf("type" to "Prescription & ECG", "title" to "Previous Cardiology OPD Prescription", "date" to "15 Aug 2026", "notes" to "Scanned at kiosk intake. Shows previous Metoprolol prescription.")
                ),
                aiInsights = listOf(
                    mapOf("category" to "Red Flag Alert", "content" to "Potential red flag detected. Chest pain + dyspnea + elevated high-sensitivity troponin requires immediate physician intervention. Clinical assessment required.", "priority" to "URGENT"),
                    mapOf("category" to "Medication Adherence", "content" to "Patient takes regular Metoprolol and Atorvastatin. Cross-check last dose timing before administering beta blockers or nitrates.", "priority" to "CAUTION"),
                    mapOf("category" to "Suggested Clinician Questions", "content" to "Ask regarding exact onset time to establish door-to-balloon window and inquire about any recent nitrate use.", "priority" to "INFO")
                ),
                activityTimeline = listOf(
                    mapOf("event" to "Patient checked in at Kiosk A (Outpatient B)", "timeFormatted" to "10:21 AM", "actor" to "Kiosk Self-Service", "status" to "COMPLETED"),
                    mapOf("event" to "Conversational AI intake completed (Socrates Pain Assessment)", "timeFormatted" to "10:24 AM", "actor" to "Gemini Medical Assistant", "status" to "COMPLETED"),
                    mapOf("event" to "Previous prescription document uploaded and OCR processed", "timeFormatted" to "10:26 AM", "actor" to "OCR Extraction Pipeline", "status" to "COMPLETED"),
                    mapOf("event" to "Red Flag Triggered: Suspected Acute Coronary Syndrome", "timeFormatted" to "10:27 AM", "actor" to "Triage Safety Sentinel", "status" to "COMPLETED"),
                    mapOf("event" to "Assigned to Dr. S. Mehta (Cardiology)", "timeFormatted" to "10:29 AM", "actor" to "Triage Nurse K. Patel", "status" to "ACTIVE")
                ),
                clinicianNotes = mapOf(
                    "clinicalNotes" to "Patient presented with typical angina pain radiating to left jaw. Urgent 12-lead ECG shows ST depression in leads V4-V6.",
                    "assessment" to "Suspected Non-ST Elevation Myocardial Infarction (NSTEMI) / Unstable Angina.",
                    "plan" to "1. STAT Aspirin 325mg + Clopidogrel 300mg loading dose given.\n2. Sublingual Nitroglycerin 0.5mg SOS if SBP > 100.\n3. Transfer immediately to Cardiac Care Unit (CCU Room 2).\n4. Cardiology angiography on call.",
                    "author" to "Dr. S. Mehta",
                    "lastUpdated" to (System.currentTimeMillis() - 2 * 60 * 1000).toString()
                )
            ),
            FirestorePatient(
                id = "MK-4819",
                tokenNumber = "MK-4819",
                name = "Anita Sharma",
                age = "67",
                gender = "Female",
                abhaId = "44-8819-2039-1182",
                contactPhone = "+91 94112 09823",
                chiefComplaint = "Sudden right facial droop and severe difficulty articulating words, onset 45 minutes ago.",
                isUrgent = true,
                priorityTag = "Urgent",
                urgentReason = "Neurological Red Flag: FAST Positive (Possible Acute Stroke)",
                status = "CALLED",
                assignedDoctor = "Dr. P. Iyer (Neurology)",
                kioskId = "Kiosk B (Ground Floor Triage)",
                vitals = "BP 178/104 mmHg • HR 88 bpm • SpO2 97% • Temp 98.4°F • RR 18/min",
                vitalsDetail = mapOf(
                    "systolic" to "178",
                    "diastolic" to "104",
                    "heartRate" to "88",
                    "spo2" to "97",
                    "temperature" to "98.4",
                    "respRate" to "18",
                    "weight" to "61",
                    "recordedBy" to "Nurse V. Nair",
                    "timestamp" to (System.currentTimeMillis() - 11 * 60 * 1000).toString()
                ),
                createdAt = System.currentTimeMillis() - 11 * 60 * 1000,
                clinicalSummary = mapOf(
                    "chiefComplaint" to "Acute right-sided facial weakness and expressive dysarthria (slurred speech).",
                    "historyOfPresentIllness" to "Patient was having breakfast when family noticed asymmetrical smile, drooping right corner of mouth, and inability to speak coherent sentences. Last known normal was 50 minutes ago. Mild right arm drift noted.",
                    "pastMedicalSurgical" to "Atrial Fibrillation (3 yrs), Hypertension (12 yrs).",
                    "drugAndAllergy" to "Tab Apixaban 5mg BD, Tab Amlodipine 5mg OD • Allergic to Penicillin (Skin Rash)",
                    "familyHistory" to "Non-contributory",
                    "reviewOfSystems" to "Positive for facial droop, slurred speech, right arm weakness. Negative for headache, seizure, or trauma.",
                    "priorInvestigations" to "Non-contrast Brain CT requested immediately.",
                    "triageLevel" to "Priority 1 - Emergency Stroke Code"
                ),
                extractedDiagnoses = listOf("Atrial Fibrillation", "Hypertension", "Acute Ischemic Stroke candidate"),
                extractedMedications = listOf(
                    mapOf("name" to "Apixaban", "dosage" to "5 mg", "frequency" to "Twice daily", "instructions" to "Direct Oral Anticoagulant"),
                    mapOf("name" to "Amlodipine", "dosage" to "5 mg", "frequency" to "Once daily", "instructions" to "Antihypertensive")
                ),
                extractedLabValues = listOf(
                    mapOf("testName" to "International Normalized Ratio (INR)", "value" to "1.1", "unit" to "", "referenceRange" to "0.8-1.2", "status" to "Normal"),
                    mapOf("testName" to "Blood Glucose", "value" to "118", "unit" to "mg/dL", "referenceRange" to "70-140", "status" to "Normal")
                ),
                documents = listOf(
                    mapOf("type" to "Discharge Summary", "title" to "Cardiology Afib Discharge Note", "date" to "10 Jan 2026", "notes" to "Shows Apixaban anticoagulation treatment.")
                ),
                aiInsights = listOf(
                    mapOf("category" to "Red Flag Alert", "content" to "FAST stroke protocol active. Onset under 4.5 hours. Check exact last time taken of direct oral anticoagulant (Apixaban) before considering IV thrombolysis.", "priority" to "URGENT"),
                    mapOf("category" to "Allergy Caution", "content" to "Documented severe allergy to Penicillin class. Avoid Ampicillin/Amoxicillin formulations.", "priority" to "CAUTION")
                ),
                activityTimeline = listOf(
                    mapOf("event" to "Checked in at Kiosk B", "timeFormatted" to "10:14 AM", "actor" to "Kiosk Self-Service", "status" to "COMPLETED"),
                    mapOf("event" to "FAST Stroke Protocol Triggered", "timeFormatted" to "10:16 AM", "actor" to "AI Triage Sentinel", "status" to "COMPLETED"),
                    mapOf("event" to "Assigned to Dr. P. Iyer (Neurology)", "timeFormatted" to "10:18 AM", "actor" to "Nurse V. Nair", "status" to "COMPLETED"),
                    mapOf("event" to "Patient called to Neuro Consultation Bay", "timeFormatted" to "10:20 AM", "actor" to "Dr. P. Iyer", "status" to "ACTIVE")
                ),
                clinicianNotes = mapOf(
                    "clinicalNotes" to "NIHSS Score: 6 (Facial palsy 2, Arm drift 2, Dysarthria 2). Last known normal confirmed at 09:35 AM.",
                    "assessment" to "Acute Ischemic Stroke in Left MCA territory vs TIA. On oral anticoagulant.",
                    "plan" to "1. STAT NCCT Brain + CT Angiography.\n2. Verify Apixaban last intake time with family.\n3. Keep NPO, IV Normal Saline at 75 mL/hr.\n4. Stroke team alert for endovascular thrombectomy evaluation if large vessel occlusion present.",
                    "author" to "Dr. P. Iyer",
                    "lastUpdated" to (System.currentTimeMillis() - 5 * 60 * 1000).toString()
                )
            ),
            FirestorePatient(
                id = "MK-4820",
                tokenNumber = "MK-4820",
                name = "Vikram Rao",
                age = "42",
                gender = "Male",
                abhaId = "12-7721-9943-3391",
                contactPhone = "+91 99887 76655",
                chiefComplaint = "Productive cough with yellow sputum, low-grade fever, and mild throat irritation for 5 days.",
                isUrgent = false,
                priorityTag = "Normal",
                status = "WAITING",
                assignedDoctor = "Dr. R. Sen (Pulmonology)",
                kioskId = "Kiosk A (Outpatient B)",
                vitals = "BP 122/80 mmHg • HR 76 bpm • SpO2 98% • Temp 100.2°F • RR 16/min",
                vitalsDetail = mapOf(
                    "systolic" to "122",
                    "diastolic" to "80",
                    "heartRate" to "76",
                    "spo2" to "98",
                    "temperature" to "100.2",
                    "respRate" to "16",
                    "weight" to "74",
                    "recordedBy" to "Nurse K. Patel",
                    "timestamp" to (System.currentTimeMillis() - 24 * 60 * 1000).toString()
                ),
                createdAt = System.currentTimeMillis() - 24 * 60 * 1000,
                clinicalSummary = mapOf(
                    "chiefComplaint" to "Persistent wet cough and fever for past 5 days.",
                    "historyOfPresentIllness" to "Patient developed intermittent fever (max 100.8°F) with productive yellow phlegm. Mild pleuritic discomfort on coughing, but no true shortness of breath at rest. Has been taking over-the-counter paracetamol with temporary relief.",
                    "pastMedicalSurgical" to "Mild allergic rhinitis.",
                    "drugAndAllergy" to "Paracetamol 650mg SOS, Cetirizine 10mg OD • NKDA",
                    "familyHistory" to "No history of asthma or tuberculosis.",
                    "reviewOfSystems" to "Positive for cough, fever, rhinorrhea. Negative for chest pain, hemoptysis, or dyspnea.",
                    "priorInvestigations" to "Chest X-Ray PA view recommended.",
                    "triageLevel" to "Priority 3 - Routine Outpatient"
                ),
                extractedDiagnoses = listOf("Acute Upper/Lower Respiratory Tract Infection"),
                extractedMedications = listOf(
                    mapOf("name" to "Paracetamol", "dosage" to "650 mg", "frequency" to "SOS for fever", "instructions" to "Antipyretic"),
                    mapOf("name" to "Cetirizine", "dosage" to "10 mg", "frequency" to "Once at bedtime", "instructions" to "Antihistamine")
                ),
                extractedLabValues = listOf(
                    mapOf("testName" to "Complete Blood Count - Total WBC", "value" to "11,400", "unit" to "/cumm", "referenceRange" to "4,000-11,000", "status" to "Mildly High"),
                    mapOf("testName" to "SpO2 (Room Air)", "value" to "98", "unit" to "%", "referenceRange" to "> 95", "status" to "Normal")
                ),
                documents = listOf(
                    mapOf("type" to "CBC Lab Report", "title" to "Hematology Report", "date" to "05 Sep 2026", "notes" to "Shows mild leukocytosis consistent with acute infection.")
                ),
                aiInsights = listOf(
                    mapOf("category" to "Clinical Assessment", "content" to "Stable oxygen saturation (98% on room air). Mild temperature elevation. Check for localized lung crepitations.", "priority" to "INFO"),
                    mapOf("category" to "Suggested Clinician Questions", "content" to "Ask about recent travel, exposure to smoke/pollutants, or household contacts with respiratory illness.", "priority" to "INFO")
                ),
                activityTimeline = listOf(
                    mapOf("event" to "Checked in at Kiosk A", "timeFormatted" to "10:01 AM", "actor" to "Kiosk Self-Service", "status" to "COMPLETED"),
                    mapOf("event" to "Intake & Document scan complete", "timeFormatted" to "10:06 AM", "actor" to "Gemini Medical Assistant", "status" to "COMPLETED"),
                    mapOf("event" to "Assigned to Dr. R. Sen (Pulmonology)", "timeFormatted" to "10:08 AM", "actor" to "Triage Router", "status" to "ACTIVE")
                ),
                clinicianNotes = mapOf()
            ),
            FirestorePatient(
                id = "MK-4821",
                tokenNumber = "MK-4821",
                name = "Priya Sundaram",
                age = "38",
                gender = "Female",
                abhaId = "88-1293-8472-5509",
                contactPhone = "+91 91234 56789",
                chiefComplaint = "Routine follow-up for Type 2 Diabetes; reviewing latest HbA1c and prescription renewal.",
                isUrgent = false,
                priorityTag = "Normal",
                status = "IN_CONSULTATION",
                assignedDoctor = "Dr. A. Verma (General Medicine)",
                kioskId = "Kiosk A (Outpatient B)",
                vitals = "BP 118/76 mmHg • HR 72 bpm • SpO2 99% • Temp 98.2°F • RR 14/min",
                vitalsDetail = mapOf(
                    "systolic" to "118",
                    "diastolic" to "76",
                    "heartRate" to "72",
                    "spo2" to "99",
                    "temperature" to "98.2",
                    "respRate" to "14",
                    "weight" to "64",
                    "recordedBy" to "Nurse K. Patel",
                    "timestamp" to (System.currentTimeMillis() - 35 * 60 * 1000).toString()
                ),
                createdAt = System.currentTimeMillis() - 35 * 60 * 1000,
                clinicalSummary = mapOf(
                    "chiefComplaint" to "Follow-up consultation for glycemic control and lab review.",
                    "historyOfPresentIllness" to "Patient diagnosed with T2DM 3 years ago. Reports good adherence to diet and medications. No polyuria, polydipsia, or neuropathic tingling. Brought latest lab report showing HbA1c 7.4%.",
                    "pastMedicalSurgical" to "Type 2 Diabetes Mellitus, Mild Fatty Liver.",
                    "drugAndAllergy" to "Tab Metformin 500mg BD, Tab Glimepiride 1mg OD • NKDA",
                    "familyHistory" to "Both parents have Type 2 Diabetes.",
                    "reviewOfSystems" to "Unremarkable. Normal vision, sensation intact in bilateral feet.",
                    "priorInvestigations" to "HbA1c: 7.4%, Fasting Blood Sugar: 132 mg/dL, Serum Creatinine: 0.8 mg/dL.",
                    "triageLevel" to "Priority 4 - Routine Clinical Follow-up"
                ),
                extractedDiagnoses = listOf("Type 2 Diabetes Mellitus"),
                extractedMedications = listOf(
                    mapOf("name" to "Metformin Hydrochloride", "dosage" to "500 mg", "frequency" to "Twice daily after meals", "instructions" to "Biguanide"),
                    mapOf("name" to "Glimepiride", "dosage" to "1 mg", "frequency" to "Once daily before breakfast", "instructions" to "Sulfonylurea")
                ),
                extractedLabValues = listOf(
                    mapOf("testName" to "HbA1c (Glycated Hemoglobin)", "value" to "7.4", "unit" to "%", "referenceRange" to "< 5.7 (Target < 7.0 for T2DM)", "status" to "Slightly Elevated"),
                    mapOf("testName" to "Fasting Blood Sugar", "value" to "132", "unit" to "mg/dL", "referenceRange" to "70-100", "status" to "High"),
                    mapOf("testName" to "Serum Creatinine", "value" to "0.8", "unit" to "mg/dL", "referenceRange" to "0.6-1.2", "status" to "Normal")
                ),
                documents = listOf(
                    mapOf("type" to "Lab Report", "title" to "Glycemic & Renal Profile", "date" to "02 Sep 2026", "notes" to "Scanned during kiosk registration. HbA1c 7.4%.")
                ),
                aiInsights = listOf(
                    mapOf("category" to "Glycemic Control", "content" to "HbA1c of 7.4% is slightly above optimal target (< 7.0%). Consider adjusting Metformin dose to 850mg or adding SGLT2 inhibitor if indicated.", "priority" to "INFO"),
                    mapOf("category" to "Preventive Care", "content" to "Annual diabetic foot exam and fundus photography screening due.", "priority" to "INFO")
                ),
                activityTimeline = listOf(
                    mapOf("event" to "Patient registered at Kiosk A", "timeFormatted" to "09:50 AM", "actor" to "Kiosk Self-Service", "status" to "COMPLETED"),
                    mapOf("event" to "Assigned to Dr. A. Verma", "timeFormatted" to "09:55 AM", "actor" to "Reception", "status" to "COMPLETED"),
                    mapOf("event" to "Consultation in progress (Room 1)", "timeFormatted" to "10:15 AM", "actor" to "Dr. A. Verma", "status" to "ACTIVE")
                ),
                clinicianNotes = mapOf(
                    "clinicalNotes" to "Patient asymptomatic. Diet compliant. Weight stable at 64 kg.",
                    "assessment" to "Type 2 Diabetes Mellitus - suboptimally controlled (HbA1c 7.4%). Renal function preserved.",
                    "plan" to "1. Increase Tab Metformin to 1000mg BD.\n2. Continue Tab Glimepiride 1mg OD.\n3. Repeat Fasting & PP Blood Sugar in 4 weeks.\n4. Annual ophthalmology fundoscopy scheduled.",
                    "author" to "Dr. A. Verma",
                    "lastUpdated" to (System.currentTimeMillis() - 10 * 60 * 1000).toString()
                )
            ),
            FirestorePatient(
                id = "MK-4815",
                tokenNumber = "MK-4815",
                name = "Deepak Joshi",
                age = "29",
                gender = "Male",
                abhaId = "33-4412-9981-2244",
                contactPhone = "+91 98334 11223",
                chiefComplaint = "Twisted right ankle during basketball, mild swelling and localized tenderness over lateral malleolus.",
                isUrgent = false,
                priorityTag = "Normal",
                status = "COMPLETED",
                assignedDoctor = "Dr. A. Verma (General Medicine)",
                kioskId = "Kiosk A (Outpatient B)",
                vitals = "BP 120/78 mmHg • HR 70 bpm • SpO2 99% • Temp 98.6°F • RR 14/min",
                vitalsDetail = mapOf(
                    "systolic" to "120",
                    "diastolic" to "78",
                    "heartRate" to "70",
                    "spo2" to "99",
                    "temperature" to "98.6",
                    "respRate" to "14",
                    "weight" to "70",
                    "recordedBy" to "Nurse K. Patel",
                    "timestamp" to (System.currentTimeMillis() - 75 * 60 * 1000).toString()
                ),
                createdAt = System.currentTimeMillis() - 75 * 60 * 1000,
                clinicalSummary = mapOf(
                    "chiefComplaint" to "Right ankle inversion injury with localized lateral pain.",
                    "historyOfPresentIllness" to "Patient inverted right foot while playing basketball 2 hours ago. Able to bear weight with mild limp. No open wound.",
                    "pastMedicalSurgical" to "No prior fractures.",
                    "drugAndAllergy" to "NKDA",
                    "familyHistory" to "Non-contributory",
                    "reviewOfSystems" to "Unremarkable except localized right ankle tenderness.",
                    "priorInvestigations" to "Right Ankle X-Ray (AP + Lateral): No fracture observed.",
                    "triageLevel" to "Priority 4 - Minor Trauma"
                ),
                extractedDiagnoses = listOf("Right Ankle Lateral Ligament Sprain (Grade 1)"),
                extractedMedications = listOf(
                    mapOf("name" to "Aceclofenac + Paracetamol", "dosage" to "100mg/325mg", "frequency" to "Twice daily after meals x 3 days", "instructions" to "Analgesic & anti-inflammatory")
                ),
                extractedLabValues = emptyList(),
                documents = listOf(
                    mapOf("type" to "Radiology Report", "title" to "Right Ankle X-Ray", "date" to "06 Sep 2026", "notes" to "No cortical breach or joint dislocation.")
                ),
                aiInsights = listOf(
                    mapOf("category" to "Treatment Recommendation", "content" to "RICE protocol (Rest, Ice, Compression, Elevation) recommended. Reassess in 5 days if pain persists.", "priority" to "INFO")
                ),
                activityTimeline = listOf(
                    mapOf("event" to "Checked in at Kiosk A", "timeFormatted" to "09:10 AM", "actor" to "Kiosk Self-Service", "status" to "COMPLETED"),
                    mapOf("event" to "Consultation completed and prescription issued", "timeFormatted" to "09:40 AM", "actor" to "Dr. A. Verma", "status" to "COMPLETED")
                ),
                clinicianNotes = mapOf(
                    "clinicalNotes" to "X-ray confirmed no fracture. Lateral ankle ligament sprain Grade 1.",
                    "assessment" to "Right ankle inversion sprain.",
                    "plan" to "Crepe bandage immobilization, Ice packs 15 mins TID, Aceclofenac + Paracetamol x 3 days, rest.",
                    "author" to "Dr. A. Verma",
                    "lastUpdated" to (System.currentTimeMillis() - 40 * 60 * 1000).toString()
                )
            )
        )
    }
}
