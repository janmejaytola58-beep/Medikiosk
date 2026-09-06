package com.example.data.api

import android.util.Log
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class UrgencyLevel(val displayName: String) {
    NORMAL("Normal (Routine Queue)"),
    PRIORITY("Priority (Elevated Attention)"),
    URGENT("Urgent (Immediate Clinical Triage)")
}

enum class DataSource {
    PATIENT_REPORTED,
    DOCUMENT_OCR,
    AI_INFERRED,
    CLINICIAN_ENTERED
}

enum class AiConfidence {
    HIGH, MEDIUM, LOW
}

data class StructuredRedFlag(
    val category: String,
    val evidence: String,
    val severity: String,
    val recommendedAction: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MissingInformation(
    val field: String,
    val reason: String,
    val suggestedQuestion: String
)

data class IntakeAnalysis(
    val chiefComplaint: String,
    val symptoms: List<String>,
    val duration: String,
    val severity: String,
    val onset: String,
    val associatedSymptoms: List<String>,
    val relevantHistory: String,
    val medications: List<String>,
    val allergies: List<String>,
    val redFlags: List<StructuredRedFlag>,
    val missingInformation: List<MissingInformation>,
    val confidence: AiConfidence,
    val urgency: UrgencyLevel
)

data class ClinicianQuestionSuggestion(
    val question: String,
    val rationale: String,
    val category: String
)

data class AiAuditRecord(
    val visitId: String,
    val actionType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String,
    val success: Boolean,
    val sourceType: DataSource,
    val latencyMs: Long
)

object AiOrchestrationService {
    private const val TAG = "AiOrchestrationService"

    private val auditLogs = mutableListOf<AiAuditRecord>()

    fun logAudit(visitId: String, actionType: String, success: Boolean, sourceType: DataSource, latencyMs: Long) {
        val record = AiAuditRecord(
            visitId = visitId,
            actionType = actionType,
            modelUsed = "gemini-3.5-flash",
            success = success,
            sourceType = sourceType,
            latencyMs = latencyMs
        )
        auditLogs.add(record)
        if (auditLogs.size > 100) auditLogs.removeAt(0)
        Log.d(TAG, "AI Audit: [visit=$visitId] [action=$actionType] [success=$success] [source=$sourceType] [latency=${latencyMs}ms]")
    }

    fun getAuditLogs(): List<AiAuditRecord> = auditLogs.toList()

    /**
     * Generates structured clinician question suggestions based on intake record.
     */
    fun generateClinicianQuestions(intake: IntakeRecord): List<ClinicianQuestionSuggestion> {
        val suggestions = mutableListOf<ClinicianQuestionSuggestion>()
        val complaint = intake.chiefComplaint.lowercase()

        if (complaint.contains("chest") || complaint.contains("heart")) {
            suggestions.add(ClinicianQuestionSuggestion("Does the chest discomfort radiate to your left arm, jaw, or back?", "Assess radiation for acute coronary syndrome", "Cardiac"))
            suggestions.add(ClinicianQuestionSuggestion("Were you experiencing physical exertion or shortness of breath when it started?", "Evaluate precipitating factors", "Exertion"))
            suggestions.add(ClinicianQuestionSuggestion("Have you taken any aspirin, nitroglycerin, or antacids already?", "Check prior self-treatment", "Medication"))
        } else if (complaint.contains("headache") || complaint.contains("head")) {
            suggestions.add(ClinicianQuestionSuggestion("Was the onset sudden and severe ('thunderclap'), or gradual?", "Rule out subarachnoid hemorrhage vs tension headache", "Onset"))
            suggestions.add(ClinicianQuestionSuggestion("Have you noticed any vision changes, weakness, or difficulty speaking?", "Screen for neurological deficit", "Neurological"))
            suggestions.add(ClinicianQuestionSuggestion("Did you experience any recent head trauma or fever?", "Check for secondary causes", "History"))
        } else if (complaint.contains("fever") || complaint.contains("temperature")) {
            suggestions.add(ClinicianQuestionSuggestion("What was your highest measured temperature, and how many days has the fever lasted?", "Quantify fever duration and height", "Timing"))
            suggestions.add(ClinicianQuestionSuggestion("Are you experiencing chills, persistent cough, or urinary discomfort?", "Identify infection source", "Associated Symptoms"))
            suggestions.add(ClinicianQuestionSuggestion("Have you taken paracetamol or any other fever reducers?", "Review antipyretic use", "Medication"))
        } else {
            suggestions.add(ClinicianQuestionSuggestion("When did the symptom first begin, and has it changed in intensity?", "Establish temporal progression", "Timeline"))
            suggestions.add(ClinicianQuestionSuggestion("Are there any specific factors that make the symptom worse or better?", "Identify exacerbating/relieving factors", "SOCRATES"))
            suggestions.add(ClinicianQuestionSuggestion("What medications or remedies have you already tried?", "Review prior treatments", "Medication"))
        }

        return suggestions
    }

    /**
     * Detects missing information from intake record.
     */
    fun detectMissingInformation(intake: IntakeRecord): List<MissingInformation> {
        val missing = mutableListOf<MissingInformation>()
        if (intake.socrates.onset.isBlank()) {
            missing.add(MissingInformation("Onset", "Exact start time or sudden vs gradual progression not specified.", "When did this symptom first begin?"))
        }
        if (intake.socrates.severity.isBlank()) {
            missing.add(MissingInformation("Severity", "Pain or discomfort scale (1-10) not recorded.", "On a scale of 1 to 10, how severe is your discomfort?"))
        }
        if (intake.currentMedications.isBlank()) {
            missing.add(MissingInformation("Medications", "Current regular medications not documented.", "Are you currently taking any regular prescription or over-the-counter medications?"))
        }
        if (intake.knownAllergies.isBlank()) {
            missing.add(MissingInformation("Allergies", "Drug or food allergy status unconfirmed.", "Do you have any known allergies to medications or foods?"))
        }
        return missing
    }

    /**
     * Performs structured AI analysis of intake data.
     */
    fun analyzeIntake(intake: IntakeRecord, messages: List<ChatMessage>): IntakeAnalysis {
        val combinedText = (messages.joinToString(" ") { it.text } + " " + intake.chiefComplaint + " " + intake.socrates.character + " " + intake.socrates.associations).lowercase()
        
        val redFlagAlert = RedFlagDetector.analyze(combinedText)
        val redFlags = if (redFlagAlert != null) {
            listOf(
                StructuredRedFlag(
                    category = redFlagAlert.category,
                    evidence = redFlagAlert.description,
                    severity = redFlagAlert.severity,
                    recommendedAction = "Immediate clinical attention required."
                )
            )
        } else {
            emptyList()
        }

        val urgency = if (redFlags.isNotEmpty()) {
            UrgencyLevel.URGENT
        } else if (combinedText.contains("severe") || combinedText.contains("high fever") || combinedText.contains("intense") || combinedText.contains("vomiting")) {
            UrgencyLevel.PRIORITY
        } else {
            UrgencyLevel.NORMAL
        }

        val missing = detectMissingInformation(intake)

        return IntakeAnalysis(
            chiefComplaint = intake.chiefComplaint.ifBlank { "Unspecified chief complaint" },
            symptoms = listOf(intake.chiefComplaint, intake.socrates.character, intake.socrates.associations).filter { it.isNotBlank() },
            duration = intake.socrates.timing.ifBlank { "Not provided" },
            severity = intake.socrates.severity.ifBlank { "Not provided" },
            onset = intake.socrates.onset.ifBlank { "Not provided" },
            associatedSymptoms = intake.socrates.associations.ifBlank { "None reported" }.split(",").map { it.trim() }.filter { it.isNotBlank() },
            relevantHistory = intake.pastMedicalHistory.ifBlank { "None reported" },
            medications = intake.currentMedications.ifBlank { "None reported" }.split(",").map { it.trim() }.filter { it.isNotBlank() },
            allergies = intake.knownAllergies.ifBlank { "None reported" }.split(",").map { it.trim() }.filter { it.isNotBlank() },
            redFlags = redFlags,
            missingInformation = missing,
            confidence = AiConfidence.HIGH,
            urgency = urgency
        )
    }
}
