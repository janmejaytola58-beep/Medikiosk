package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.firebase.FirestorePatient
import com.example.data.firebase.FirestoreRepository
import com.example.data.firebase.SyncStatus
import com.example.data.local.AppDatabase
import com.example.data.local.LocalPatientRepository
import com.example.data.local.entity.PatientRecordEntity
import com.example.data.model.AssistanceRequestItem
import com.example.data.model.ChatMessage
import com.example.data.model.ClinicalSummary
import com.example.data.model.DoctorProfile
import com.example.data.model.ExtractedDocumentData
import com.example.data.model.LabValueItem
import com.example.data.model.Language
import com.example.data.model.MedicationItem
import com.example.data.model.MessageSender
import com.example.data.model.PatientInfo
import com.example.data.model.RedFlagAlert
import com.example.data.model.RedFlagDetector
import com.example.data.model.SocratesData
import com.example.data.model.StaffRole
import com.example.ui.components.AppNotification
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class KioskScreen {
    WELCOME,
    PATIENT_IDENTIFY,
    CONVERSATIONAL_INTAKE,
    DOCUMENT_SCAN,
    SUMMARY,
    CONFIRMATION,
    STAFF_VIEW,
    MY_RECORDS,
    PATIENT_PROFILE,
    ACCESSIBILITY_CENTER,
    QUEUE_TRACKER,
    PRIVACY_CENTER
}

class KioskViewModel(application: Application) : AndroidViewModel(application) {

    private val localRepo = LocalPatientRepository(AppDatabase.getDatabase(application).patientRecordDao())
    val firestoreRepo = FirestoreRepository(application)
    private val prefs: SharedPreferences = application.getSharedPreferences("medikiosk_prefs", Context.MODE_PRIVATE)

    // Navigation & Global
    private val _currentScreen = MutableStateFlow(KioskScreen.WELCOME)
    val currentScreen: StateFlow<KioskScreen> = _currentScreen.asStateFlow()

    // Preferences & Persistence
    private val _selectedLanguage = MutableStateFlow(loadSavedLanguage())
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _patientInfo = MutableStateFlow(PatientInfo())
    val patientInfo: StateFlow<PatientInfo> = _patientInfo.asStateFlow()

    // Unfinished Intake Session state
    private val _hasUnfinishedSession = MutableStateFlow(checkHasUnfinishedSession())
    val hasUnfinishedSession: StateFlow<Boolean> = _hasUnfinishedSession.asStateFlow()

    // Inactivity timeout state
    private val _showInactivityDialog = MutableStateFlow(false)
    val showInactivityDialog: StateFlow<Boolean> = _showInactivityDialog.asStateFlow()

    // Accessibility Controls
    private val _isHighContrast = MutableStateFlow(prefs.getBoolean("pref_high_contrast", false))
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    private val _isLargeFont = MutableStateFlow(prefs.getFloat("pref_text_scale", 1.0f) > 1.1f)
    val isLargeFont: StateFlow<Boolean> = _isLargeFont.asStateFlow()

    private val _textScale = MutableStateFlow(prefs.getFloat("pref_text_scale", 1.0f))
    val textScale: StateFlow<Float> = _textScale.asStateFlow()

    private val _isVoiceGuidanceEnabled = MutableStateFlow(prefs.getBoolean("pref_voice_guidance", false))
    val isVoiceGuidanceEnabled: StateFlow<Boolean> = _isVoiceGuidanceEnabled.asStateFlow()

    private val _isReduceMotionEnabled = MutableStateFlow(prefs.getBoolean("pref_reduce_motion", false))
    val isReduceMotionEnabled: StateFlow<Boolean> = _isReduceMotionEnabled.asStateFlow()

    // In-App Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification(
                id = "notif_1",
                title = "Welcome to City Care Hospital Kiosk",
                message = "Self-registration is available. Please have your ABHA ID or prescriptions ready.",
                timestamp = "Just now",
                icon = Icons.Default.Notifications,
                iconBg = HealthBlueSoft,
                iconTint = HealthNavy,
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Active Visit Tracker
    private val _hasActiveVisit = MutableStateFlow(false)
    val hasActiveVisit: StateFlow<Boolean> = _hasActiveVisit.asStateFlow()

    private val _activeVisitStatus = MutableStateFlow("WAITING")
    val activeVisitStatus: StateFlow<String> = _activeVisitStatus.asStateFlow()

    private val _queuePosition = MutableStateFlow(4)
    val queuePosition: StateFlow<Int> = _queuePosition.asStateFlow()

    // Urgent Red Flag Clinical Alert State
    private val _redFlagAlert = MutableStateFlow<RedFlagAlert?>(null)
    val redFlagAlert: StateFlow<RedFlagAlert?> = _redFlagAlert.asStateFlow()

    // Screen 3: Chat State
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _currentQuickReplies = MutableStateFlow<List<String>>(emptyList())
    val currentQuickReplies: StateFlow<List<String>> = _currentQuickReplies.asStateFlow()

    private val _socratesData = MutableStateFlow(SocratesData())
    val socratesData: StateFlow<SocratesData> = _socratesData.asStateFlow()

    // Screen 4: Document OCR State
    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap: StateFlow<Bitmap?> = _scannedBitmap.asStateFlow()

    private val _isScanningDocument = MutableStateFlow(false)
    val isScanningDocument: StateFlow<Boolean> = _isScanningDocument.asStateFlow()

    private val _extractedData = MutableStateFlow(ExtractedDocumentData())
    val extractedData: StateFlow<ExtractedDocumentData> = _extractedData.asStateFlow()

    // Screen 5: Clinical Summary State
    private val _clinicalSummary = MutableStateFlow<ClinicalSummary?>(null)
    val clinicalSummary: StateFlow<ClinicalSummary?> = _clinicalSummary.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary.asStateFlow()

    // Realtime Firestore Stream for Staff View
    val syncStatus: StateFlow<SyncStatus> = firestoreRepo.syncStatus

    val patientsQueue: StateFlow<List<FirestorePatient>> = firestoreRepo.listenToPatients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = firestoreRepo.getInitialFallbackPatients()
        )

    val assistanceRequests: StateFlow<List<AssistanceRequestItem>> = firestoreRepo.listenToAssistanceRequests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = firestoreRepo.getFallbackAssistanceRequests()
        )

    // Local Room DB Stream for Patient's "My Records"
    val myLocalRecords: StateFlow<List<PatientRecordEntity>> = localRepo.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Phase 3: Staff & Doctor State Management ---
    private val _staffRole = MutableStateFlow(StaffRole.DOCTOR)
    val staffRole: StateFlow<StaffRole> = _staffRole.asStateFlow()

    private val _selectedDoctor = MutableStateFlow(DoctorProfile.DOCTORS.first())
    val selectedDoctor: StateFlow<DoctorProfile> = _selectedDoctor.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _queueFilter = MutableStateFlow("ALL") // ALL, WAITING, URGENT, IN_CONSULTATION, COMPLETED
    val queueFilter: StateFlow<String> = _queueFilter.asStateFlow()

    private val _queueSortBy = MutableStateFlow("QUEUE_NUMBER") // QUEUE_NUMBER, WAITING_TIME, PRIORITY, ARRIVAL_TIME
    val queueSortBy: StateFlow<String> = _queueSortBy.asStateFlow()

    private val _selectedPatientForDetail = MutableStateFlow<FirestorePatient?>(null)
    val selectedPatientForDetail: StateFlow<FirestorePatient?> = _selectedPatientForDetail.asStateFlow()

    fun setStaffRole(role: StaffRole) {
        _staffRole.value = role
    }

    fun setSelectedDoctor(doctor: DoctorProfile) {
        _selectedDoctor.value = doctor
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setQueueFilter(filter: String) {
        _queueFilter.value = filter
    }

    fun setQueueSortBy(sortBy: String) {
        _queueSortBy.value = sortBy
    }

    fun selectPatientForDetail(patient: FirestorePatient?) {
        _selectedPatientForDetail.value = patient
    }

    fun callPatient(patientId: String, caller: String = "Staff") {
        viewModelScope.launch {
            firestoreRepo.updatePatientStatus(patientId, "CALLED", changedBy = caller)
            localRepo.updateStatus(patientId, "CALLED")
            if (_patientInfo.value.tokenNumber == patientId || patientId.contains(_patientInfo.value.tokenNumber)) {
                _activeVisitStatus.value = "CALLED"
            }
        }
    }

    fun startConsultation(patientId: String, doctorName: String) {
        viewModelScope.launch {
            firestoreRepo.updatePatientStatus(patientId, "IN_CONSULTATION", changedBy = doctorName)
            localRepo.updateStatus(patientId, "IN_CONSULTATION")
            if (_patientInfo.value.tokenNumber == patientId || patientId.contains(_patientInfo.value.tokenNumber)) {
                _activeVisitStatus.value = "IN_CONSULTATION"
            }
        }
    }

    fun completeVisit(patientId: String, doctorName: String) {
        viewModelScope.launch {
            firestoreRepo.updatePatientStatus(patientId, "COMPLETED", changedBy = doctorName)
            localRepo.updateStatus(patientId, "COMPLETED")
            if (_patientInfo.value.tokenNumber == patientId || patientId.contains(_patientInfo.value.tokenNumber)) {
                _activeVisitStatus.value = "COMPLETED"
            }
        }
    }

    fun assignDoctor(patientId: String, doctorName: String, assignedBy: String = "Reception") {
        viewModelScope.launch {
            firestoreRepo.assignDoctor(patientId, doctorName, assignedBy = assignedBy)
        }
    }

    fun updatePriority(patientId: String, priority: String, urgentReason: String? = null) {
        viewModelScope.launch {
            firestoreRepo.updatePriority(patientId, priority, urgentReason)
        }
    }

    fun saveClinicianNotes(
        patientId: String,
        clinicalNotes: String,
        assessment: String,
        plan: String,
        author: String
    ) {
        viewModelScope.launch {
            val notesMap = mapOf(
                "clinicalNotes" to clinicalNotes,
                "assessment" to assessment,
                "plan" to plan,
                "author" to author,
                "lastUpdated" to System.currentTimeMillis().toString()
            )
            firestoreRepo.saveClinicianNotes(patientId, notesMap)
        }
    }

    fun recordVitals(
        patientId: String,
        systolic: String,
        diastolic: String,
        heartRate: String,
        spo2: String,
        temperature: String,
        respRate: String,
        weight: String,
        recordedBy: String
    ) {
        viewModelScope.launch {
            val vitalsDetail = mapOf(
                "systolic" to systolic,
                "diastolic" to diastolic,
                "heartRate" to heartRate,
                "spo2" to spo2,
                "temperature" to temperature,
                "respRate" to respRate,
                "weight" to weight,
                "recordedBy" to recordedBy,
                "timestamp" to System.currentTimeMillis().toString()
            )
            val vitalsSummary = "BP $systolic/$diastolic mmHg • HR $heartRate bpm • SpO2 $spo2% • Temp $temperature°F • RR $respRate/min"
            firestoreRepo.updateVitals(patientId, vitalsDetail, vitalsSummary)
        }
    }

    fun acknowledgeAssistanceRequest(reqId: String) {
        viewModelScope.launch {
            firestoreRepo.updateAssistanceRequestStatus(reqId, "ACKNOWLEDGED")
        }
    }

    fun resolveAssistanceRequest(reqId: String) {
        viewModelScope.launch {
            firestoreRepo.updateAssistanceRequestStatus(reqId, "RESOLVED")
        }
    }

    fun navigateTo(screen: KioskScreen) {
        _currentScreen.value = screen
        if (screen == KioskScreen.CONVERSATIONAL_INTAKE && _messages.value.isEmpty()) {
            startConversationalIntake()
        } else if (screen == KioskScreen.SUMMARY && _clinicalSummary.value == null) {
            generateSummary()
        }
    }

    fun selectLanguage(language: Language) {
        _selectedLanguage.value = language
        prefs.edit().putString("pref_language_code", language.code).apply()
    }

    private fun loadSavedLanguage(): Language {
        val code = prefs.getString("pref_language_code", "en") ?: "en"
        return Language.SUPPORTED_LANGUAGES.find { it.code == code } ?: Language.DEFAULT
    }

    private fun checkHasUnfinishedSession(): Boolean {
        return prefs.getBoolean("has_unfinished_session", false)
    }

    fun resumeUnfinishedSession() {
        val name = prefs.getString("draft_name", "") ?: ""
        val age = prefs.getString("draft_age", "") ?: ""
        val gender = prefs.getString("draft_gender", "Male") ?: "Male"
        val abha = prefs.getString("draft_abha", "") ?: ""
        val token = prefs.getString("draft_token", "MK-4822") ?: "MK-4822"
        val chief = prefs.getString("draft_chief", "") ?: ""

        _patientInfo.value = PatientInfo(
            name = name,
            age = age,
            gender = gender,
            abhaId = abha,
            tokenNumber = token,
            hasConsented = true
        )
        if (chief.isNotBlank()) {
            _socratesData.value = _socratesData.value.copy(site = chief)
        }
        _hasUnfinishedSession.value = false
        prefs.edit().putBoolean("has_unfinished_session", false).apply()
        _currentScreen.value = KioskScreen.CONVERSATIONAL_INTAKE
    }

    fun discardUnfinishedSession() {
        _hasUnfinishedSession.value = false
        prefs.edit()
            .putBoolean("has_unfinished_session", false)
            .remove("draft_name")
            .remove("draft_age")
            .remove("draft_gender")
            .remove("draft_abha")
            .remove("draft_token")
            .remove("draft_chief")
            .apply()
        resetForNewPatient()
    }

    fun toggleHighContrast(enabled: Boolean = !_isHighContrast.value) {
        _isHighContrast.value = enabled
        prefs.edit().putBoolean("pref_high_contrast", enabled).apply()
    }

    fun toggleLargeFont(enabled: Boolean = !_isLargeFont.value) {
        _isLargeFont.value = enabled
        val scale = if (enabled) 1.25f else 1.0f
        _textScale.value = scale
        prefs.edit().putFloat("pref_text_scale", scale).apply()
    }

    fun setTextScale(scale: Float) {
        _textScale.value = scale
        _isLargeFont.value = scale > 1.1f
        prefs.edit().putFloat("pref_text_scale", scale).apply()
    }

    fun setVoiceGuidance(enabled: Boolean) {
        _isVoiceGuidanceEnabled.value = enabled
        prefs.edit().putBoolean("pref_voice_guidance", enabled).apply()
    }

    fun setReduceMotion(enabled: Boolean) {
        _isReduceMotionEnabled.value = enabled
        prefs.edit().putBoolean("pref_reduce_motion", enabled).apply()
    }

    fun setInactivityDialogVisible(visible: Boolean) {
        _showInactivityDialog.value = visible
    }

    fun requestStaffAssistance(reason: String) {
        val newNotif = AppNotification(
            id = "assist_${System.currentTimeMillis()}",
            title = "Staff Assistance Requested",
            message = "A nurse has been alerted for '$reason' at this kiosk.",
            timestamp = "Just now",
            icon = Icons.Default.PhoneInTalk,
            iconBg = PastelPeach,
            iconTint = PastelPeachIcon,
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value

        viewModelScope.launch {
            firestoreRepo.saveAssistanceRequest(
                kioskId = "Kiosk A (Outpatient B)",
                reason = reason,
                tokenNumber = _patientInfo.value.tokenNumber,
                patientName = _patientInfo.value.name.ifBlank { "Patient" }
            )
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    fun dismissRedFlag() {
        _redFlagAlert.value = null
    }

    fun clearRedFlagAlert() {
        _redFlagAlert.value = null
    }

    fun checkPotentialRedFlag(input: String) {
        val detected = RedFlagDetector.analyzeConversation(_messages.value, input)
        if (detected != null) {
            _redFlagAlert.value = detected
        }
    }

    // --- Screen 2: Patient Info ---

    fun updatePatientName(name: String) {
        _patientInfo.value = _patientInfo.value.copy(name = name)
    }

    fun updatePatientAge(age: String) {
        _patientInfo.value = _patientInfo.value.copy(age = age)
    }

    fun updatePatientGender(gender: String) {
        _patientInfo.value = _patientInfo.value.copy(gender = gender)
    }

    fun updatePatientAbha(abha: String) {
        _patientInfo.value = _patientInfo.value.copy(abhaId = abha)
    }

    fun toggleNewPatient(isNew: Boolean) {
        _patientInfo.value = _patientInfo.value.copy(isNewPatient = isNew)
    }

    fun toggleConsent(consented: Boolean) {
        _patientInfo.value = _patientInfo.value.copy(hasConsented = consented)
    }

    fun prefillMockPatient() {
        _patientInfo.value = PatientInfo(
            name = "Ramesh Kumar",
            age = "52",
            gender = "Male",
            abhaId = "14-8891-2304-9812",
            isNewPatient = false,
            hasConsented = true,
            tokenNumber = "MK-4822"
        )
    }

    // --- Screen 3: Conversational Intake ---

    private fun startConversationalIntake() {
        viewModelScope.launch {
            _isAiThinking.value = true
            val lang = _selectedLanguage.value
            val patient = _patientInfo.value

            val aiResponse = GeminiApiClient.generateIntakeResponse(
                patientInfo = patient,
                languageName = lang.name,
                conversationHistory = emptyList(),
                currentIntakeStep = "CHIEF_COMPLAINT"
            )

            val initialMsg = ChatMessage(
                sender = MessageSender.AI,
                text = aiResponse.displayText,
                quickReplies = aiResponse.quickReplies
            )
            _messages.value = listOf(initialMsg)
            _currentQuickReplies.value = aiResponse.quickReplies
            _isAiThinking.value = false
        }
    }

    fun sendUserMessage(text: String) {
        if (text.trim().isBlank()) return

        val userMsg = ChatMessage(
            sender = MessageSender.PATIENT,
            text = text.trim()
        )
        val updated = _messages.value.toMutableList()
        updated.add(userMsg)
        _messages.value = updated
        _currentQuickReplies.value = emptyList()

        // Realtime red flag analysis on every message
        checkPotentialRedFlag(text)

        viewModelScope.launch {
            _isAiThinking.value = true
            val historyPairs = updated.map {
                (if (it.sender == MessageSender.AI) "model" else "user") to it.text
            }

            val step = when (updated.size) {
                1, 2 -> "CHIEF_COMPLAINT"
                3, 4 -> "HISTORY_OF_PRESENT_ILLNESS"
                5, 6 -> "PAST_HISTORY"
                else -> "MEDICATIONS"
            }

            val aiResponse = GeminiApiClient.generateIntakeResponse(
                patientInfo = _patientInfo.value,
                languageName = _selectedLanguage.value.name,
                conversationHistory = historyPairs,
                currentIntakeStep = step
            )

            val curSocrates = _socratesData.value
            _socratesData.value = curSocrates.copy(
                site = aiResponse.extractedFields["site"] ?: curSocrates.site.ifBlank { text },
                onset = aiResponse.extractedFields["onset"] ?: curSocrates.onset,
                character = aiResponse.extractedFields["character"] ?: curSocrates.character,
                radiation = aiResponse.extractedFields["radiation"] ?: curSocrates.radiation,
                associations = aiResponse.extractedFields["associations"] ?: curSocrates.associations,
                timing = aiResponse.extractedFields["timing"] ?: curSocrates.timing,
                exacerbatingRelieving = aiResponse.extractedFields["exacerbating_relieving"] ?: curSocrates.exacerbatingRelieving,
                severity = aiResponse.extractedFields["severity"] ?: curSocrates.severity
            )

            if (_redFlagAlert.value == null) {
                val detected = RedFlagDetector.analyzeConversation(updated, aiResponse.displayText)
                if (detected != null) {
                    _redFlagAlert.value = detected
                }
            }

            val aiMsg = ChatMessage(
                sender = MessageSender.AI,
                text = aiResponse.displayText,
                quickReplies = aiResponse.quickReplies
            )
            _messages.value = _messages.value + aiMsg
            _currentQuickReplies.value = aiResponse.quickReplies
            _isAiThinking.value = false
        }
    }

    // --- Screen 4: Document OCR ---

    fun onDocumentImageSelected(bitmap: Bitmap) {
        _scannedBitmap.value = bitmap
        extractDocumentFromBitmap(bitmap)
    }

    fun loadSampleDocument() {
        viewModelScope.launch {
            _isScanningDocument.value = true
            val sample = GeminiApiClient.getFallbackDocumentData()
            _extractedData.value = sample
            _isScanningDocument.value = false
        }
    }

    private fun extractDocumentFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isScanningDocument.value = true
            val data = GeminiApiClient.extractDocumentData(bitmap)
            _extractedData.value = data
            _isScanningDocument.value = false
        }
    }

    fun addDiagnosis(diagnosis: String) {
        val current = _extractedData.value
        val list = current.diagnoses.toMutableList()
        if (diagnosis.isNotBlank() && !list.contains(diagnosis.trim())) {
            list.add(diagnosis.trim())
            _extractedData.value = current.copy(diagnoses = list)
        }
    }

    fun removeDiagnosis(diagnosis: String) {
        val current = _extractedData.value
        val list = current.diagnoses.toMutableList()
        list.remove(diagnosis)
        _extractedData.value = current.copy(diagnoses = list)
    }

    fun updateMedication(med: MedicationItem) {
        val current = _extractedData.value
        val list = current.medications.toMutableList()
        val index = list.indexOfFirst { it.id == med.id }
        if (index >= 0) {
            list[index] = med
        } else {
            list.add(med)
        }
        _extractedData.value = current.copy(medications = list)
    }

    fun removeMedication(medId: String) {
        val current = _extractedData.value
        val list = current.medications.toMutableList()
        list.removeAll { it.id == medId }
        _extractedData.value = current.copy(medications = list)
    }

    fun updateLabValue(lab: LabValueItem) {
        val current = _extractedData.value
        val list = current.labValues.toMutableList()
        val index = list.indexOfFirst { it.id == lab.id }
        if (index >= 0) {
            list[index] = lab
        } else {
            list.add(lab)
        }
        _extractedData.value = current.copy(labValues = list)
    }

    fun removeLabValue(labId: String) {
        val current = _extractedData.value
        val list = current.labValues.toMutableList()
        list.removeAll { it.id == labId }
        _extractedData.value = current.copy(labValues = list)
    }

    // --- Screen 5: Clinical Summary ---

    fun generateSummary() {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            val historyPairs = _messages.value.map {
                (if (it.sender == MessageSender.AI) "model" else "user") to it.text
            }
            val summary = GeminiApiClient.generateClinicalSummary(
                patientInfo = _patientInfo.value,
                conversationHistory = historyPairs,
                extractedDocData = _extractedData.value
            )
            _clinicalSummary.value = summary
            _isGeneratingSummary.value = false
        }
    }

    fun updateSummarySection(section: String, newContent: String) {
        val current = _clinicalSummary.value ?: return
        val updated = when (section) {
            "Chief Complaint" -> current.copy(chiefComplaint = newContent)
            "History of Present Illness" -> current.copy(historyOfPresentIllness = newContent)
            "Past Medical/Surgical History" -> current.copy(pastMedicalSurgical = newContent)
            "Drug & Allergy History" -> current.copy(drugAndAllergy = newContent)
            "Family History" -> current.copy(familyHistory = newContent)
            "Review of Systems" -> current.copy(reviewOfSystems = newContent)
            "Prior Investigations" -> current.copy(priorInvestigations = newContent)
            else -> current
        }
        _clinicalSummary.value = updated
    }

    /**
     * Submits intake: persists to Firestore and local Room DB.
     */
    fun submitIntake() {
        val patient = _patientInfo.value
        val socrates = _socratesData.value
        val docs = _extractedData.value
        val summary = _clinicalSummary.value
        val redFlag = _redFlagAlert.value

        val firestorePatient = FirestorePatient.fromIntake(
            patientInfo = patient,
            socratesData = socrates,
            extractedData = docs,
            clinicalSummary = summary,
            redFlagAlert = redFlag,
            assignedDoc = _selectedDoctor.value.name + " (" + _selectedDoctor.value.specialty + ")",
            kioskNode = "Kiosk A (Outpatient B)"
        )

        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val isUrgent = redFlag != null || (summary?.triageLevel?.contains("Priority 1", ignoreCase = true) == true)

        val localEntity = PatientRecordEntity(
            tokenNumber = patient.tokenNumber,
            patientName = patient.name.ifBlank { "Patient #${patient.tokenNumber}" },
            patientAge = patient.age.ifBlank { "--" },
            patientGender = patient.gender,
            abhaId = patient.abhaId,
            chiefComplaint = summary?.chiefComplaint ?: socrates.site.ifBlank { "Outpatient Intake" },
            triageLevel = summary?.triageLevel ?: if (isUrgent) "Priority 1 (Urgent)" else "Priority 3 (Standard)",
            isUrgent = isUrgent,
            urgentReason = redFlag?.description,
            formattedDate = dateFormatted,
            timestamp = System.currentTimeMillis(),
            clinicalSummaryJson = summary?.historyOfPresentIllness ?: socrates.associations,
            medicationsSummary = docs.medications.joinToString(", ") { "${it.name} ${it.dosage}" }.ifBlank { "None documented" },
            vitals = firestorePatient.vitals,
            status = "WAITING"
        )

        viewModelScope.launch {
            firestoreRepo.savePatientRecord(firestorePatient)
            localRepo.insertRecord(localEntity)
        }

        val waitingPatients = patientsQueue.value.count { it.status == "WAITING" }
        _queuePosition.value = (waitingPatients + 1).coerceAtLeast(1)

        _hasActiveVisit.value = true
        _activeVisitStatus.value = "WAITING"

        prefs.edit()
            .putBoolean("has_unfinished_session", false)
            .remove("draft_name")
            .remove("draft_age")
            .remove("draft_gender")
            .remove("draft_abha")
            .remove("draft_token")
            .remove("draft_chief")
            .apply()
        _hasUnfinishedSession.value = false

        val intakeNotif = AppNotification(
            id = "intake_sub_${System.currentTimeMillis()}",
            title = "Intake Submitted (#${patient.tokenNumber})",
            message = "Your intake was sent to staff. You are #${_queuePosition.value} in the queue.",
            timestamp = "Just now",
            icon = Icons.Default.CheckCircle,
            iconBg = PastelMint,
            iconTint = PastelMintIcon,
            isRead = false
        )
        _notifications.value = listOf(intakeNotif) + _notifications.value

        _currentScreen.value = KioskScreen.CONFIRMATION
    }

    /**
     * Updates patient status in Firestore and Room DB.
     */
    fun updatePatientStatus(patientId: String, newStatus: String) {
        viewModelScope.launch {
            firestoreRepo.updatePatientStatus(patientId, newStatus)
            localRepo.updateStatus(patientId, newStatus)
            if (_patientInfo.value.tokenNumber == patientId || patientId.contains(_patientInfo.value.tokenNumber)) {
                _activeVisitStatus.value = newStatus
                if (newStatus == "IN_CONSULTATION") {
                    val roomNotif = AppNotification(
                        id = "room_${System.currentTimeMillis()}",
                        title = "Doctor Ready — Room 3",
                        message = "Please proceed to Consulting Room 3 for Dr. S. Mehta.",
                        timestamp = "Just now",
                        icon = Icons.Default.MeetingRoom,
                        iconBg = PastelMint,
                        iconTint = PastelMintIcon,
                        isRead = false
                    )
                    _notifications.value = listOf(roomNotif) + _notifications.value
                }
            }
        }
    }

    /**
     * Deletes a local record from Room DB.
     */
    fun deleteLocalRecord(token: String) {
        viewModelScope.launch {
            localRepo.deleteRecord(token)
        }
    }

    fun resetForNewPatient() {
        _patientInfo.value = PatientInfo()
        _messages.value = emptyList()
        _currentQuickReplies.value = emptyList()
        _socratesData.value = SocratesData()
        _scannedBitmap.value = null
        _extractedData.value = ExtractedDocumentData()
        _clinicalSummary.value = null
        _redFlagAlert.value = null
        _hasActiveVisit.value = false
        _activeVisitStatus.value = "WAITING"
        _showInactivityDialog.value = false
        _currentScreen.value = KioskScreen.WELCOME
    }
}
