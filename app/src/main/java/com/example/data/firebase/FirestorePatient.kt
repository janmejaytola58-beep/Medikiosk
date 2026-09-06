package com.example.data.firebase

import com.example.data.model.ClinicalSummary
import com.example.data.model.ExtractedDocumentData
import com.example.data.model.LabValueItem
import com.example.data.model.MedicationItem
import com.example.data.model.PatientInfo
import com.example.data.model.RedFlagAlert
import com.example.data.model.SocratesData
import com.google.firebase.firestore.PropertyName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SyncStatus {
    SYNCED,
    SYNCING,
    OFFLINE
}

data class FirestorePatient(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("tokenNumber") @set:PropertyName("tokenNumber")
    var tokenNumber: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("age") @set:PropertyName("age")
    var age: String = "",

    @get:PropertyName("gender") @set:PropertyName("gender")
    var gender: String = "Male",

    @get:PropertyName("abhaId") @set:PropertyName("abhaId")
    var abhaId: String = "",

    @get:PropertyName("contactPhone") @set:PropertyName("contactPhone")
    var contactPhone: String = "+91 98765 43210",

    @get:PropertyName("chiefComplaint") @set:PropertyName("chiefComplaint")
    var chiefComplaint: String = "",

    @get:PropertyName("socratesData") @set:PropertyName("socratesData")
    var socratesData: Map<String, String> = emptyMap(),

    @get:PropertyName("extractedDiagnoses") @set:PropertyName("extractedDiagnoses")
    var extractedDiagnoses: List<String> = emptyList(),

    @get:PropertyName("extractedMedications") @set:PropertyName("extractedMedications")
    var extractedMedications: List<Map<String, String>> = emptyList(),

    @get:PropertyName("extractedLabValues") @set:PropertyName("extractedLabValues")
    var extractedLabValues: List<Map<String, String>> = emptyList(),

    @get:PropertyName("clinicalSummary") @set:PropertyName("clinicalSummary")
    var clinicalSummary: Map<String, String> = emptyMap(),

    @get:PropertyName("priorityTag") @set:PropertyName("priorityTag")
    var priorityTag: String = "Normal", // "Normal", "Priority", "Urgent"

    @get:PropertyName("isUrgent") @set:PropertyName("isUrgent")
    var isUrgent: Boolean = false,

    @get:PropertyName("urgentReason") @set:PropertyName("urgentReason")
    var urgentReason: String? = null,

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "WAITING", // WAITING, CALLED, IN_CONSULTATION, COMPLETED, CANCELLED

    @get:PropertyName("assignedDoctor") @set:PropertyName("assignedDoctor")
    var assignedDoctor: String = "Dr. S. Mehta (Cardiology)",

    @get:PropertyName("kioskId") @set:PropertyName("kioskId")
    var kioskId: String = "Kiosk A (Outpatient B)",

    @get:PropertyName("vitals") @set:PropertyName("vitals")
    var vitals: String = "BP 120/80 mmHg • HR 74 bpm • SpO2 98% • Temp 98.4°F",

    @get:PropertyName("vitalsDetail") @set:PropertyName("vitalsDetail")
    var vitalsDetail: Map<String, String> = emptyMap(),

    @get:PropertyName("clinicianNotes") @set:PropertyName("clinicianNotes")
    var clinicianNotes: Map<String, String> = emptyMap(),

    @get:PropertyName("aiInsights") @set:PropertyName("aiInsights")
    var aiInsights: List<Map<String, String>> = emptyList(),

    @get:PropertyName("documents") @set:PropertyName("documents")
    var documents: List<Map<String, String>> = emptyList(),

    @get:PropertyName("activityTimeline") @set:PropertyName("activityTimeline")
    var activityTimeline: List<Map<String, String>> = emptyList(),

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
) {
    val normalizedStatus: String
        get() = when (status.uppercase()) {
            "SEEN" -> "COMPLETED"
            else -> status.uppercase()
        }

    val normalizedPriority: String
        get() = when {
            isUrgent || priorityTag.equals("Urgent", ignoreCase = true) || priorityTag.equals("URGENT", ignoreCase = true) -> "Urgent"
            priorityTag.equals("Priority", ignoreCase = true) || priorityTag.equals("PRIORITY", ignoreCase = true) -> "Priority"
            else -> "Normal"
        }

    val arrivalTimeFormatted: String
        get() {
            return try {
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(createdAt))
            } catch (e: Exception) {
                "Just now"
            }
        }

    fun toClinicalSummary(): ClinicalSummary {
        return ClinicalSummary(
            chiefComplaint = clinicalSummary["chiefComplaint"] ?: chiefComplaint,
            historyOfPresentIllness = clinicalSummary["historyOfPresentIllness"] ?: "",
            pastMedicalSurgical = clinicalSummary["pastMedicalSurgical"] ?: "",
            drugAndAllergy = clinicalSummary["drugAndAllergy"] ?: "",
            familyHistory = clinicalSummary["familyHistory"] ?: "",
            reviewOfSystems = clinicalSummary["reviewOfSystems"] ?: "",
            priorInvestigations = clinicalSummary["priorInvestigations"] ?: "",
            triageLevel = clinicalSummary["triageLevel"] ?: if (isUrgent) "Priority 1 (Urgent)" else "Priority 3 (Standard Evaluation)",
            summaryTimestamp = clinicalSummary["summaryTimestamp"] ?: ""
        )
    }

    val calculatedWaitMinutes: Int
        get() {
            val diffMs = System.currentTimeMillis() - createdAt
            val mins = (diffMs / (1000 * 60)).toInt()
            return mins.coerceAtLeast(1)
        }

    companion object {
        fun fromIntake(
            patientInfo: PatientInfo,
            socratesData: SocratesData,
            extractedData: ExtractedDocumentData,
            clinicalSummary: ClinicalSummary?,
            redFlagAlert: RedFlagAlert?,
            assignedDoc: String = "Dr. S. Mehta (Cardiology)",
            kioskNode: String = "Kiosk A (Outpatient B)"
        ): FirestorePatient {
            val tokenClean = patientInfo.tokenNumber.replace("MK-", "").trim()
            val isUrgent = redFlagAlert != null || (clinicalSummary?.triageLevel?.contains("Priority 1", ignoreCase = true) == true)
            val urgentReason = redFlagAlert?.description ?: if (isUrgent) "Flagged for immediate emergency triage evaluation" else null

            val socratesMap = mapOf(
                "site" to socratesData.site,
                "onset" to socratesData.onset,
                "character" to socratesData.character,
                "radiation" to socratesData.radiation,
                "associations" to socratesData.associations,
                "timing" to socratesData.timing,
                "exacerbatingRelieving" to socratesData.exacerbatingRelieving,
                "severity" to socratesData.severity
            )

            val medsList = extractedData.medications.map { med ->
                mapOf(
                    "name" to med.name,
                    "dosage" to med.dosage,
                    "frequency" to med.frequency,
                    "instructions" to med.instructions
                )
            }

            val labsList = extractedData.labValues.map { lab ->
                mapOf(
                    "testName" to lab.testName,
                    "value" to lab.value,
                    "unit" to lab.unit,
                    "referenceRange" to lab.referenceRange,
                    "status" to lab.status
                )
            }

            val summaryMap = clinicalSummary?.let { s ->
                mapOf(
                    "chiefComplaint" to s.chiefComplaint,
                    "historyOfPresentIllness" to s.historyOfPresentIllness,
                    "pastMedicalSurgical" to s.pastMedicalSurgical,
                    "drugAndAllergy" to s.drugAndAllergy,
                    "familyHistory" to s.familyHistory,
                    "reviewOfSystems" to s.reviewOfSystems,
                    "priorInvestigations" to s.priorInvestigations,
                    "triageLevel" to s.triageLevel,
                    "summaryTimestamp" to s.summaryTimestamp
                )
            } ?: mapOf(
                "chiefComplaint" to (socratesData.site.ifBlank { "General Health Check" }),
                "triageLevel" to if (isUrgent) "Priority 1 (Urgent)" else "Priority 3 (Standard Evaluation)"
            )

            val simVitalsDetail = if (isUrgent) {
                mapOf(
                    "systolic" to "158",
                    "diastolic" to "96",
                    "heartRate" to "102",
                    "spo2" to "94",
                    "temperature" to "99.1",
                    "respRate" to "22",
                    "weight" to "72",
                    "recordedBy" to "Triage Nurse K. Patel",
                    "timestamp" to System.currentTimeMillis().toString()
                )
            } else {
                mapOf(
                    "systolic" to "120",
                    "diastolic" to "78",
                    "heartRate" to "76",
                    "spo2" to "99",
                    "temperature" to "98.6",
                    "respRate" to "16",
                    "weight" to "68",
                    "recordedBy" to "Triage Nurse K. Patel",
                    "timestamp" to System.currentTimeMillis().toString()
                )
            }

            val simVitalsStr = "BP ${simVitalsDetail["systolic"]}/${simVitalsDetail["diastolic"]} mmHg • HR ${simVitalsDetail["heartRate"]} bpm • SpO2 ${simVitalsDetail["spo2"]}% • Temp ${simVitalsDetail["temperature"]}°F"

            val initialTimeline = listOf(
                mapOf(
                    "event" to "Patient checked in at $kioskNode",
                    "timeFormatted" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis() - 5 * 60 * 1000)),
                    "actor" to "Kiosk Self-Service",
                    "status" to "COMPLETED"
                ),
                mapOf(
                    "event" to "Multilingual conversational AI intake completed",
                    "timeFormatted" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis() - 3 * 60 * 1000)),
                    "actor" to "Gemini Medical Assistant",
                    "status" to "COMPLETED"
                ),
                mapOf(
                    "event" to "Documents scanned & OCR clinical summary generated",
                    "timeFormatted" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis() - 1 * 60 * 1000)),
                    "actor" to "OCR Extraction Pipeline",
                    "status" to "COMPLETED"
                ),
                mapOf(
                    "event" to "Queued for $assignedDoc",
                    "timeFormatted" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                    "actor" to "Triage Router",
                    "status" to "ACTIVE"
                )
            )

            val insights = mutableListOf<Map<String, String>>()
            if (isUrgent) {
                insights.add(
                    mapOf(
                        "category" to "Red Flag Alert",
                        "content" to (urgentReason ?: "High acuity symptom combination requiring rapid clinician assessment."),
                        "priority" to "URGENT"
                    )
                )
            }
            if (medsList.isNotEmpty()) {
                insights.add(
                    mapOf(
                        "category" to "Medication Verification",
                        "content" to "Identified ${medsList.size} active medication regimen(s) from patient documents. Cross-verify current adherence and potential drug-drug interactions.",
                        "priority" to "CAUTION"
                    )
                )
            }
            insights.add(
                mapOf(
                    "category" to "Suggested Clinician Inquiries",
                    "content" to "Confirm symptom duration, exacerbating physical exertion, and any recent adjustments to daily prescription schedule.",
                    "priority" to "INFO"
                )
            )

            val docsList = if (extractedData.medications.isNotEmpty() || extractedData.labValues.isNotEmpty()) {
                listOf(
                    mapOf(
                        "type" to "Prescription & Lab Report",
                        "title" to extractedData.documentTitle,
                        "date" to extractedData.documentDate,
                        "notes" to "Scanned at kiosk intake desk with ${medsList.size} medications and ${labsList.size} lab investigations extracted."
                    )
                )
            } else emptyList()

            return FirestorePatient(
                id = patientInfo.tokenNumber,
                tokenNumber = patientInfo.tokenNumber,
                name = patientInfo.name.ifBlank { "Patient $tokenClean" },
                age = patientInfo.age.ifBlank { "45" },
                gender = patientInfo.gender,
                abhaId = patientInfo.abhaId,
                contactPhone = "+91 98765 43210",
                chiefComplaint = clinicalSummary?.chiefComplaint?.ifBlank { socratesData.site }
                    ?: (socratesData.site.ifBlank { "General Medical Evaluation" }),
                socratesData = socratesMap,
                extractedDiagnoses = extractedData.diagnoses.toList(),
                extractedMedications = medsList,
                extractedLabValues = labsList,
                clinicalSummary = summaryMap,
                priorityTag = if (isUrgent) "Urgent" else "Normal",
                isUrgent = isUrgent,
                urgentReason = urgentReason,
                status = "WAITING",
                assignedDoctor = assignedDoc,
                kioskId = kioskNode,
                vitals = simVitalsStr,
                vitalsDetail = simVitalsDetail,
                aiInsights = insights,
                documents = docsList,
                activityTimeline = initialTimeline,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
