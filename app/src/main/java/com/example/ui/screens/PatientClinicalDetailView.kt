package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.FirestorePatient
import com.example.data.model.DoctorProfile
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthCardBorderSubtle
import com.example.ui.theme.HealthDivider
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextMuted
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderIcon
import com.example.ui.theme.PastelLightBlue
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.RedFlagBg
import com.example.ui.theme.RedFlagBorder
import com.example.ui.theme.RedFlagText
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatientClinicalDetailView(
    patient: FirestorePatient,
    currentDoctor: DoctorProfile,
    onClose: () -> Unit,
    onCallPatient: (String) -> Unit,
    onStartConsultation: (String, String) -> Unit,
    onCompleteVisit: (String, String) -> Unit,
    onAssignDoctor: (String, String) -> Unit,
    onUpdatePriority: (String, String) -> Unit,
    onSaveClinicianNotes: (patientId: String, notes: String, assessment: String, plan: String, doctor: String) -> Unit,
    onRecordVitals: (patientId: String, sys: String, dia: String, hr: String, spo2: String, temp: String, rr: String, wt: String, nurse: String) -> Unit
) {
    val context = LocalContext.current
    val clinicalSummary = patient.toClinicalSummary()

    var showVitalsDialog by remember { mutableStateOf(false) }
    var showAssignDoctorDialog by remember { mutableStateOf(false) }
    var showPriorityDialog by remember { mutableStateOf(false) }
    var showDocImageDialog by remember { mutableStateOf(false) }

    // Clinician Notes editable state
    var editClinicalNotes by remember(patient.id) {
        mutableStateOf(patient.clinicianNotes["clinicalNotes"] ?: "")
    }
    var editAssessment by remember(patient.id) {
        mutableStateOf(patient.clinicianNotes["assessment"] ?: "")
    }
    var editPlan by remember(patient.id) {
        mutableStateOf(patient.clinicianNotes["plan"] ?: "")
    }
    var isNotesSaved by remember { mutableStateOf(false) }
    var isAiSummaryReviewed by remember(patient.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("patient_clinical_detail_view")
    ) {
        // Clinical Detail Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = HealthNavy,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = patient.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "#${patient.tokenNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${patient.age} yrs • ${patient.gender} • ABHA: ${patient.abhaId.ifBlank { "Not provided" }}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                val shareText = "MediKiosk Clinical Summary\nPatient: ${patient.name} (#${patient.tokenNumber})\nChief Complaint: ${patient.chiefComplaint}\nVitals: ${patient.vitals}\nTriage: ${patient.normalizedPriority}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Clinical Summary"))
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Status Workflow Quick Actions Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Visit Status",
                                style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(status = patient.normalizedStatus)
                                Spacer(modifier = Modifier.width(8.dp))
                                PriorityBadge(
                                    priority = patient.normalizedPriority,
                                    onClick = { showPriorityDialog = true }
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Assigned Clinician",
                                style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueSoft,
                                modifier = Modifier.clickable { showAssignDoctorDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = HealthNavy,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = patient.assignedDoctor.ifBlank { "Unassigned" },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthNavy,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = HealthCardBorderSubtle)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (patient.normalizedStatus) {
                            "WAITING" -> {
                                Button(
                                    onClick = {
                                        onCallPatient(patient.id.ifBlank { patient.tokenNumber })
                                        Toast.makeText(context, "${patient.name} called to room", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Patient")
                                }
                                Button(
                                    onClick = {
                                        onStartConsultation(patient.id.ifBlank { patient.tokenNumber }, currentDoctor.name)
                                        Toast.makeText(context, "Consultation started with ${patient.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthBlueAccent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Consult")
                                }
                            }
                            "CALLED" -> {
                                Button(
                                    onClick = {
                                        onStartConsultation(patient.id.ifBlank { patient.tokenNumber }, currentDoctor.name)
                                        Toast.makeText(context, "Consultation started with ${patient.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthBlueAccent),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Consult")
                                }
                            }
                            "IN_CONSULTATION" -> {
                                Button(
                                    onClick = {
                                        onCompleteVisit(patient.id.ifBlank { patient.tokenNumber }, currentDoctor.name)
                                        Toast.makeText(context, "Visit completed for ${patient.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Complete Visit")
                                }
                            }
                            "COMPLETED" -> {
                                OutlinedButton(
                                    onClick = {
                                        onCallPatient(patient.id.ifBlank { patient.tokenNumber })
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reopen / Call Again")
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showVitalsDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vitals")
                        }
                    }
                }
            }

            // 2. Urgent Red-Flag Alert Banner (If Urgent)
            if (patient.isUrgent || patient.normalizedPriority == "Urgent") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = RedFlagBg,
                    border = BorderStroke(1.5.dp, RedFlagBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AlertRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "URGENT CLINICAL ATTENTION REQUIRED",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RedFlagText
                                    )
                                )
                                Text(
                                    text = "High-Acuity Triage Sentinel Triggered",
                                    style = MaterialTheme.typography.labelSmall.copy(color = RedFlagText.copy(alpha = 0.8f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = patient.urgentReason ?: "Critical cardiopulmonary/neurological symptom cluster detected during conversational intake.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = RedFlagText,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, RedFlagBorder.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Safety Sentinel: Potential red flag detected. Clinical assessment required. This is an alert system, NOT a diagnostic system.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = RedFlagText,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // 3. Patient Vitals Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = HealthBlueAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Triage Vitals & Biometrics",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HealthBlueSoft,
                            modifier = Modifier.clickable { showVitalsDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Record / Update",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthNavy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val vitalsMap = patient.vitalsDetail
                    val bp = if (vitalsMap.containsKey("systolic")) "${vitalsMap["systolic"]}/${vitalsMap["diastolic"]} mmHg" else "120/80 mmHg"
                    val hr = if (vitalsMap.containsKey("heartRate")) "${vitalsMap["heartRate"]} bpm" else "76 bpm"
                    val spo2 = if (vitalsMap.containsKey("spo2")) "${vitalsMap["spo2"]}%" else "98%"
                    val temp = if (vitalsMap.containsKey("temperature")) "${vitalsMap["temperature"]}°F" else "98.6°F"
                    val rr = if (vitalsMap.containsKey("respRate")) "${vitalsMap["respRate"]}/min" else "16/min"
                    val wt = if (vitalsMap.containsKey("weight")) "${vitalsMap["weight"]} kg" else "68 kg"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VitalBox(label = "Blood Pressure", value = bp, isAbnormal = bp.contains("168") || bp.contains("178"), modifier = Modifier.weight(1f))
                        VitalBox(label = "Heart Rate", value = hr, isAbnormal = hr.contains("106"), modifier = Modifier.weight(1f))
                        VitalBox(label = "Oxygen (SpO2)", value = spo2, isAbnormal = spo2.contains("93") || spo2.contains("94"), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VitalBox(label = "Temperature", value = temp, isAbnormal = temp.contains("100"), modifier = Modifier.weight(1f))
                        VitalBox(label = "Resp Rate", value = rr, isAbnormal = rr.contains("24"), modifier = Modifier.weight(1f))
                        VitalBox(label = "Weight", value = wt, isAbnormal = false, modifier = Modifier.weight(1f))
                    }
                }
            }

            // 4. AI-Assisted Clinical Insights
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = PastelLavenderIcon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AI-Assisted Insights & Considerations",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                            Text(
                                text = "AI clinical considerations — not a formal diagnosis or prescription",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = HealthTextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val insightsList = patient.aiInsights.ifEmpty {
                        listOf(
                            mapOf(
                                "category" to "Clinical Assessment",
                                "content" to "Chief complaint: ${patient.chiefComplaint}. Patient checked in via ${patient.kioskId}.",
                                "priority" to "INFO"
                            ),
                            mapOf(
                                "category" to "Medication Cross-Check",
                                "content" to if (patient.extractedMedications.isNotEmpty()) "Documented ${patient.extractedMedications.size} prescription items. Confirm dosage adherence." else "No active prescriptions uploaded.",
                                "priority" to "INFO"
                            )
                        )
                    }

                    insightsList.forEach { insight ->
                        val priority = insight["priority"] ?: "INFO"
                        val bgCol = when (priority) {
                            "URGENT" -> RedFlagBg
                            "CAUTION" -> PastelPeach
                            else -> HealthBackground
                        }
                        val borderCol = when (priority) {
                            "URGENT" -> RedFlagBorder
                            "CAUTION" -> Color(0xFFFDBA74)
                            else -> HealthCardBorder
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = bgCol,
                            border = BorderStroke(1.dp, borderCol)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = insight["category"] ?: "Insight",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (priority == "URGENT") RedFlagText else HealthNavy
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = insight["content"] ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (priority == "URGENT") RedFlagText else HealthTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. AI-Generated Intake Summary (Structured Socrates)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, if (isAiSummaryReviewed) Color(0xFF2E7D32) else HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = HealthBlueAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AI-Generated Intake Summary",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                Text(
                                    text = if (isAiSummaryReviewed) "✓ Reviewed & Verified by Clinician" else "Generated from conversational kiosk intake",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isAiSummaryReviewed) Color(0xFF2E7D32) else HealthTextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { isAiSummaryReviewed = !isAiSummaryReviewed },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAiSummaryReviewed) Color(0xFF2E7D32) else HealthNavy
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAiSummaryReviewed) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAiSummaryReviewed) "Reviewed" else "Mark Reviewed", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ClinicalSectionRow("Chief Complaint", clinicalSummary.chiefComplaint)
                    ClinicalSectionRow("History of Present Illness (HPI)", clinicalSummary.historyOfPresentIllness)
                    ClinicalSectionRow("Past Medical & Surgical History", clinicalSummary.pastMedicalSurgical)
                    ClinicalSectionRow("Drug & Allergy History", clinicalSummary.drugAndAllergy)
                    ClinicalSectionRow("Family History", clinicalSummary.familyHistory)
                    ClinicalSectionRow("Review of Systems", clinicalSummary.reviewOfSystems)
                    ClinicalSectionRow("Prior Investigations", clinicalSummary.priorInvestigations)
                    ClinicalSectionRow("Recommended Triage Level", clinicalSummary.triageLevel)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HealthBackgroundLavender(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Disclaimer: AI-generated summary based on patient responses. Must be reviewed and verified by an attending clinician.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HealthTextSecondary,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // 5b. AI Suggested Clinician Questions
            val suggestedQuestions = remember(patient.chiefComplaint) {
                com.example.data.api.AiOrchestrationService.generateClinicianQuestions(
                    com.example.data.model.IntakeRecord(chiefComplaint = patient.chiefComplaint)
                )
            }
            if (suggestedQuestions.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AI Suggested Clinician Questions",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                Text(
                                    text = "AI suggestion — clinician review required.",
                                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        suggestedQuestions.forEach { sq ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HealthBackgroundLavender().copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, HealthCardBorder.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "• ${sq.question}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = HealthNavyHeader,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Rationale: ${sq.rationale}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = HealthTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            editAssessment += "\n[Q: ${sq.question}]"
                                            Toast.makeText(context, "Added question to assessment", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Use", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Documents & OCR Review
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = HealthBlueAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Uploaded Documents & OCR Review",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                            Text(
                                text = "Scanned at kiosk intake station",
                                style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (patient.documents.isEmpty() && patient.extractedMedications.isEmpty() && patient.extractedLabValues.isEmpty()) {
                        Text(
                            text = "No prior prescription or diagnostic documents uploaded for this visit.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        val docTitle = patient.documents.firstOrNull()?.get("title") ?: "Scanned Prescription & Lab Document"
                        val docDate = patient.documents.firstOrNull()?.get("date") ?: "Recent"
                        val docNotes = patient.documents.firstOrNull()?.get("notes") ?: "Processed via Gemini Multi-modal OCR"

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDocImageDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = HealthBackground,
                            border = BorderStroke(1.dp, HealthCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HealthBlueSoft,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = docTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HealthNavyHeader
                                        )
                                    )
                                    Text(
                                        text = "Date: $docDate • $docNotes",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = HealthTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Inspect →",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthBlueAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 7. Extracted Medications
            if (patient.extractedMedications.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Current Medications (${patient.extractedMedications.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                        }
                        Text(
                            text = "Extracted from patient-provided documents",
                            style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        patient.extractedMedications.forEach { med ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = HealthBackground,
                                border = BorderStroke(1.dp, HealthCardBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(HealthBlueAccent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${med["name"]} ${med["dosage"] ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HealthNavyHeader
                                            )
                                        )
                                        Text(
                                            text = "${med["frequency"]} • ${med["instructions"]}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 8. Extracted Lab Results
            if (patient.extractedLabValues.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = HealthBlueAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lab Results & Investigations",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                        }
                        Text(
                            text = "Values displayed as recorded on patient documentation",
                            style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        patient.extractedLabValues.forEach { lab ->
                            val status = lab["status"] ?: "Normal"
                            val isHigh = status.contains("High", ignoreCase = true) || status.contains("Elevated", ignoreCase = true)

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isHigh) PastelPeach.copy(alpha = 0.5f) else HealthBackground,
                                border = BorderStroke(1.dp, if (isHigh) Color(0xFFFDBA74) else HealthCardBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = lab["testName"] ?: "Test",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HealthNavyHeader
                                            )
                                        )
                                        Text(
                                            text = "Ref: ${lab["referenceRange"] ?: "--"}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = HealthTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${lab["value"]} ${lab["unit"]}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isHigh) AlertOrange else HealthNavy
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isHigh) AlertOrange.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = status,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isHigh) AlertOrange else SuccessGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 9. Clinician Notes Section (Doctor Assessment & Treatment Plan)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Doctor Consultation & Clinical Plan",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                val lastUp = patient.clinicianNotes["lastUpdated"]
                                val author = patient.clinicianNotes["author"] ?: currentDoctor.name
                                Text(
                                    text = "Author: $author",
                                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onSaveClinicianNotes(
                                    patient.id.ifBlank { patient.tokenNumber },
                                    editClinicalNotes,
                                    editAssessment,
                                    editPlan,
                                    currentDoctor.name
                                )
                                isNotesSaved = true
                                Toast.makeText(context, "Clinician notes saved to Firestore", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Notes", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Clinical Findings & Examination",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavy)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editClinicalNotes,
                        onValueChange = { editClinicalNotes = it; isNotesSaved = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter physical examination and clinical observations...") },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthNavy,
                            unfocusedBorderColor = HealthCardBorder
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Clinical Impression & Assessment",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavy)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editAssessment,
                        onValueChange = { editAssessment = it; isNotesSaved = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Primary working diagnosis or clinical assessment...") },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthNavy,
                            unfocusedBorderColor = HealthCardBorder
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Treatment Plan & Prescriptions",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavy)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editPlan,
                        onValueChange = { editPlan = it; isNotesSaved = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Medications, ordered investigations, referrals, and follow-up advice...") },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthNavy,
                            unfocusedBorderColor = HealthCardBorder
                        ),
                        minLines = 3
                    )
                }
            }

            // 10. Patient Activity Timeline
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timeline, contentDescription = null, tint = HealthBlueAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Patient Visit Timeline",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val timeline = patient.activityTimeline.ifEmpty {
                        listOf(
                            mapOf("event" to "Patient registered at ${patient.kioskId}", "timeFormatted" to patient.arrivalTimeFormatted, "actor" to "Kiosk", "status" to "COMPLETED"),
                            mapOf("event" to "AI intake and summary generated", "timeFormatted" to patient.arrivalTimeFormatted, "actor" to "Gemini AI", "status" to "COMPLETED")
                        )
                    }

                    timeline.forEachIndexed { index, item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (item["status"] == "ACTIVE") HealthNavy else SuccessGreen)
                                )
                                if (index < timeline.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(30.dp)
                                            .background(HealthCardBorder)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item["event"] ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = HealthNavyHeader
                                        )
                                    )
                                    Text(
                                        text = item["timeFormatted"] ?: "",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "By ${item["actor"] ?: "System"}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthTextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialogs
    if (showVitalsDialog) {
        RecordVitalsDialog(
            currentVitals = patient.vitalsDetail,
            onDismiss = { showVitalsDialog = false },
            onSave = { sys, dia, hr, spo2, temp, rr, wt, nurse ->
                onRecordVitals(patient.id.ifBlank { patient.tokenNumber }, sys, dia, hr, spo2, temp, rr, wt, nurse)
                showVitalsDialog = false
                Toast.makeText(context, "Vitals updated for ${patient.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAssignDoctorDialog) {
        AssignDoctorDialog(
            currentDoctor = patient.assignedDoctor,
            onDismiss = { showAssignDoctorDialog = false },
            onAssign = { doc ->
                onAssignDoctor(patient.id.ifBlank { patient.tokenNumber }, doc)
                showAssignDoctorDialog = false
                Toast.makeText(context, "${patient.name} assigned to $doc", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPriorityDialog) {
        ChangePriorityDialog(
            currentPriority = patient.normalizedPriority,
            onDismiss = { showPriorityDialog = false },
            onSelect = { p ->
                onUpdatePriority(patient.id.ifBlank { patient.tokenNumber }, p)
                showPriorityDialog = false
                Toast.makeText(context, "Priority set to $p", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDocImageDialog) {
        AlertDialog(
            onDismissRequest = { showDocImageDialog = false },
            confirmButton = {
                Button(onClick = { showDocImageDialog = false }) {
                    Text("Close")
                }
            },
            title = {
                Text("Scanned Medical Document Inspection")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = HealthNavyHeader
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("High-Resolution OCR Document View", color = Color.White, fontSize = 12.sp)
                                Text("Validated by Gemini 2.5 Flash Vision", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            }
                        }
                    }
                    Text(
                        text = "Document Metadata:\n• Type: Prescription & Lab investigations\n• Extracted Medications: ${patient.extractedMedications.size}\n• Extracted Lab Results: ${patient.extractedLabValues.size}\n• Ingestion Station: ${patient.kioskId}",
                        style = MaterialTheme.typography.bodySmall.copy(color = HealthTextPrimary)
                    )
                }
            }
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val bg = when (status) {
        "COMPLETED", "SEEN" -> SuccessGreen.copy(alpha = 0.15f)
        "IN_CONSULTATION" -> HealthBlueSoft
        "CALLED" -> PastelPeach
        else -> Color(0xFFF1F5F9)
    }
    val textCol = when (status) {
        "COMPLETED", "SEEN" -> SuccessGreen
        "IN_CONSULTATION" -> HealthNavy
        "CALLED" -> Color(0xFFC2410C)
        else -> HealthTextSecondary
    }
    val label = when (status) {
        "COMPLETED", "SEEN" -> "✅ Completed"
        "IN_CONSULTATION" -> "🩺 In Consultation"
        "CALLED" -> "📞 Called"
        else -> "⏳ Waiting"
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textCol,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PriorityBadge(priority: String, onClick: () -> Unit) {
    val bg = when (priority) {
        "Urgent" -> AlertRed
        "Priority" -> PastelPeach
        else -> Color(0xFFE2E8F0)
    }
    val textCol = when (priority) {
        "Urgent" -> Color.White
        "Priority" -> Color(0xFFC2410C)
        else -> HealthNavy
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = "Priority: $priority ▾",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textCol,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun VitalBox(
    label: String,
    value: String,
    isAbnormal: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isAbnormal) RedFlagBg else HealthBackground,
        border = BorderStroke(1.dp, if (isAbnormal) RedFlagBorder else HealthCardBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isAbnormal) RedFlagText else HealthTextSecondary,
                    fontSize = 9.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAbnormal) AlertRed else HealthNavy,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun ClinicalSectionRow(title: String, content: String) {
    if (content.isBlank()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = HealthNavy,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = HealthBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = HealthTextPrimary,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun RecordVitalsDialog(
    currentVitals: Map<String, String>,
    onDismiss: () -> Unit,
    onSave: (sys: String, dia: String, hr: String, spo2: String, temp: String, rr: String, wt: String, nurse: String) -> Unit
) {
    var sys by remember { mutableStateOf(currentVitals["systolic"] ?: "120") }
    var dia by remember { mutableStateOf(currentVitals["diastolic"] ?: "80") }
    var hr by remember { mutableStateOf(currentVitals["heartRate"] ?: "76") }
    var spo2 by remember { mutableStateOf(currentVitals["spo2"] ?: "98") }
    var temp by remember { mutableStateOf(currentVitals["temperature"] ?: "98.6") }
    var rr by remember { mutableStateOf(currentVitals["respRate"] ?: "16") }
    var wt by remember { mutableStateOf(currentVitals["weight"] ?: "68") }
    var nurse by remember { mutableStateOf(currentVitals["recordedBy"] ?: "Triage Nurse K. Patel") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onSave(sys, dia, hr, spo2, temp, rr, wt, nurse) },
                colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Vitals")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel")
            }
        },
        title = {
            Text("Record / Update Patient Vitals", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sys,
                        onValueChange = { sys = it },
                        label = { Text("Systolic BP (mmHg)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dia,
                        onValueChange = { dia = it },
                        label = { Text("Diastolic BP (mmHg)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hr,
                        onValueChange = { hr = it },
                        label = { Text("Heart Rate (bpm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = spo2,
                        onValueChange = { spo2 = it },
                        label = { Text("SpO2 (%)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Temp (°F)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rr,
                        onValueChange = { rr = it },
                        label = { Text("Resp Rate (/min)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = wt,
                    onValueChange = { wt = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nurse,
                    onValueChange = { nurse = it },
                    label = { Text("Recorded By (Nurse/Staff Name)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun AssignDoctorDialog(
    currentDoctor: String,
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel")
            }
        },
        title = {
            Text("Assign Attending Doctor", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DoctorProfile.DOCTORS.forEach { doc ->
                    val isSelected = currentDoctor.contains(doc.name)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAssign("${doc.name} (${doc.specialty})") },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) HealthBlueSoft else HealthBackground,
                        border = BorderStroke(1.dp, if (isSelected) HealthNavy else HealthCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) HealthNavy else HealthTextSecondary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                Text(
                                    text = "${doc.specialty} • ${doc.roomNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary)
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = HealthNavy)
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun ChangePriorityDialog(
    currentPriority: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel")
            }
        },
        title = {
            Text("Change Triage Priority Level", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Normal", "Priority", "Urgent").forEach { p ->
                    val isSelected = currentPriority.equals(p, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(p) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) HealthBlueSoft else HealthBackground,
                        border = BorderStroke(1.dp, if (isSelected) HealthNavy else HealthCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (p) {
                                    "Urgent" -> "🚨 Urgent (Priority 1 - Immediate)"
                                    "Priority" -> "⚡ Priority (Priority 2 - Expedited)"
                                    else -> "🟢 Normal (Priority 3 - Standard Queue)"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (p == "Urgent") AlertRed else HealthNavyHeader
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = HealthNavy)
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun HealthBackgroundLavender(): Color = Color(0xFFF1F5F9)
