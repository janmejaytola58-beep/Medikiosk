package com.example.data.model

import java.util.UUID

enum class MessageSender {
    AI,
    PATIENT
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val quickReplies: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class SocratesData(
    val site: String = "",
    val onset: String = "",
    val character: String = "",
    val radiation: String = "",
    val associations: String = "",
    val timing: String = "",
    val exacerbatingRelieving: String = "",
    val severity: String = ""
)

data class IntakeRecord(
    val chiefComplaint: String = "",
    val socrates: SocratesData = SocratesData(),
    val pastMedicalHistory: String = "",
    val pastSurgicalHistory: String = "",
    val currentMedications: String = "",
    val knownAllergies: String = "",
    val familyHistory: String = "",
    val reviewOfSystems: String = ""
)

data class MedicationItem(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var dosage: String,
    var frequency: String,
    var instructions: String = ""
)

data class LabValueItem(
    val id: String = UUID.randomUUID().toString(),
    var testName: String,
    var value: String,
    var unit: String,
    var referenceRange: String,
    var status: String = "Normal" // Normal, High, Low, Abnormal
)

data class ExtractedDocumentData(
    val diagnoses: MutableList<String> = mutableListOf(),
    val medications: MutableList<MedicationItem> = mutableListOf(),
    val labValues: MutableList<LabValueItem> = mutableListOf(),
    val documentTitle: String = "Prescription & Lab Investigation Record",
    val documentDate: String = "Recent",
    val summaryNotes: String = ""
)

data class ClinicalSummary(
    var chiefComplaint: String = "",
    var historyOfPresentIllness: String = "",
    var pastMedicalSurgical: String = "",
    var drugAndAllergy: String = "",
    var familyHistory: String = "",
    var reviewOfSystems: String = "",
    var priorInvestigations: String = "",
    var triageLevel: String = "Priority 3 (Standard Evaluation)",
    var summaryTimestamp: String = ""
)

data class RedFlagAlert(
    val title: String,
    val description: String,
    val category: String,
    val severity: String = "IMMEDIATE TRIAGE",
    val matchedSymptoms: List<String> = emptyList()
)

object RedFlagDetector {
    fun analyze(text: String): RedFlagAlert? {
        val lower = text.lowercase()

        // 1. Chest pain + Breathlessness
        val hasChestPain = lower.contains("chest pain") || lower.contains("chest tightness") ||
                lower.contains("chest pressure") || lower.contains("heavy chest") ||
                lower.contains("pain in my chest") || lower.contains("pain in chest") ||
                lower.contains("angina") || lower.contains("सीने में दर्द") || lower.contains("छाती में दर्द")

        val hasBreathless = lower.contains("breathless") || lower.contains("shortness of breath") ||
                lower.contains("difficulty breathing") || lower.contains("hard to breathe") ||
                lower.contains("trouble breathing") || lower.contains("struggling to breathe") ||
                lower.contains("cannot breathe") || lower.contains("सांस फूलना") || lower.contains("सांस लेने में तकलीफ")

        if (hasChestPain && hasBreathless) {
            return RedFlagAlert(
                title = "Urgent: Cardiopulmonary Red Flag",
                description = "Patient reports acute chest pain combined with breathlessness. High risk of Acute Coronary Syndrome or Pulmonary Embolism. Immediate staff attention required instead of routine queueing.",
                category = "Cardiopulmonary",
                matchedSymptoms = listOf("Chest Pain / Pressure", "Shortness of Breath")
            )
        }

        // 2. Stroke-like symptoms
        val hasStrokeKeywords = lower.contains("face drooping") || lower.contains("drooping face") ||
                lower.contains("facial droop") || lower.contains("facial numbness") ||
                lower.contains("arm weakness") || lower.contains("weakness in arm") ||
                lower.contains("slurred speech") || lower.contains("speech slurred") ||
                lower.contains("trouble speaking") || lower.contains("cannot speak") ||
                lower.contains("loss of speech") || lower.contains("numbness on one side") ||
                lower.contains("one-sided numbness") || lower.contains("paralysis") ||
                lower.contains("stroke") || lower.contains("hemiplegia") || lower.contains("लकवा") ||
                lower.contains("चेहरा टेढ़ा")

        if (hasStrokeKeywords) {
            return RedFlagAlert(
                title = "Urgent: Stroke-Like Neurological Red Flag",
                description = "Patient reports acute neurological symptoms (facial droop, arm weakness, or slurred speech). Immediate hospital emergency staff evaluation required instead of routine queueing.",
                category = "Neurological",
                matchedSymptoms = listOf("Acute Neurological Deficit", "Possible Stroke Symptoms")
            )
        }

        // 3. Severe acute cardiac signs
        if (lower.contains("heart attack") || lower.contains("cardiac arrest") ||
            (hasChestPain && (lower.contains("jaw") || lower.contains("left arm") || lower.contains("sweat")))) {
            return RedFlagAlert(
                title = "Urgent: Acute Cardiac Red Flag",
                description = "Patient reports severe chest pain with radiation or cardiac arrest symptoms. Immediate clinical triage required instead of routine queueing.",
                category = "Cardiac",
                matchedSymptoms = listOf("Radiating Chest Pain", "Suspected Cardiac Event")
            )
        }

        return null
    }

    fun analyzeConversation(messages: List<ChatMessage>, currentInput: String = ""): RedFlagAlert? {
        val combinedText = messages.joinToString(" ") { it.text } + " " + currentInput
        return analyze(combinedText)
    }
}

