package com.example.data.model

enum class StaffRole(val title: String, val badge: String, val description: String) {
    RECEPTION("Reception Staff", "Reception", "Queue management, check-in, doctor assignment"),
    NURSE("Triage Nurse", "Triage / Nursing", "Intake review, vitals recording, priority escalation"),
    DOCTOR("Attending Physician", "Doctor", "Clinical summary review, AI insights, clinician notes & consults"),
    ADMIN("Hospital Administrator", "Admin", "System oversight, kiosk status, operational statistics")
}

data class DoctorProfile(
    val id: String,
    val name: String,
    val specialty: String,
    val department: String,
    val roomNumber: String,
    val isAvailable: Boolean = true
) {
    companion object {
        val DOCTORS = listOf(
            DoctorProfile("doc_1", "Dr. S. Mehta", "Cardiology", "Cardiopulmonary", "Room 3 (Outpatient B)"),
            DoctorProfile("doc_2", "Dr. A. Verma", "General & Internal Medicine", "General Medicine", "Room 1 (Ground Floor)"),
            DoctorProfile("doc_3", "Dr. R. Sen", "Pulmonology & Respiratory", "Chest Clinic", "Room 4 (Outpatient B)"),
            DoctorProfile("doc_4", "Dr. P. Iyer", "Neurology & Stroke", "Neuro Outpatient", "Room 7 (Specialty Wing)")
        )
    }
}

data class VitalsData(
    val systolic: String = "120",
    val diastolic: String = "80",
    val heartRate: String = "74",
    val spo2: String = "98",
    val temperature: String = "98.4",
    val respRate: String = "16",
    val weight: String = "68",
    val recordedBy: String = "Nurse K. Patel",
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedString: String
        get() = "BP $systolic/$diastolic mmHg • HR $heartRate bpm • SpO2 $spo2% • Temp $temperature°F • RR $respRate/min • Wt ${weight}kg"
}

data class ClinicianNote(
    val clinicalNotes: String = "",
    val assessment: String = "",
    val plan: String = "",
    val author: String = "Dr. S. Mehta",
    val timestamp: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class AssistanceRequestItem(
    val id: String = "",
    val kioskId: String = "Kiosk A (Outpatient B)",
    val reason: String = "",
    val tokenNumber: String = "",
    val patientName: String = "",
    val status: String = "PENDING", // PENDING, ACKNOWLEDGED, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = try {
            java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(createdAt))
        } catch (e: Exception) {
            "Just now"
        }
}

data class TimelineActivityItem(
    val event: String,
    val timeFormatted: String,
    val actor: String,
    val status: String = "COMPLETED"
)

data class AIClinicalInsight(
    val category: String, // "Red Flag", "Medication Highlight", "Suggested Questions", "Missing Info"
    val content: String,
    val priority: String = "INFO" // URGENT, CAUTION, INFO
)
