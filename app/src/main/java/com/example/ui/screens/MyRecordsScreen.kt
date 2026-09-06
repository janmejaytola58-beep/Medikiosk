package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PatientRecordEntity
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthCardBorderSubtle
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextMuted
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderIcon
import com.example.ui.theme.PastelLightBlue
import com.example.ui.theme.PastelLightBlueIcon
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon
import com.example.ui.theme.SuccessGreen

@Composable
fun MyRecordsScreen(
    records: List<PatientRecordEntity>,
    onBackClick: () -> Unit,
    onDeleteRecord: (String) -> Unit,
    onStartNewIntake: () -> Unit
) {
    val context = LocalContext.current
    var selectedRecordForDetails by remember { mutableStateOf<PatientRecordEntity?>(null) }

    val totalVisits = records.size
    val activeVisits = records.count { it.status == "WAITING" || it.status == "IN_CONSULTATION" }
    val completedVisits = records.count { it.status == "COMPLETED" || it.status == "SEEN" }
    val documentsCount = records.count { it.medicationsSummary.isNotBlank() && it.medicationsSummary != "None documented" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("my_records_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = HealthNavy,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "My Health Records",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Your outpatient consultations & intake history",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // 4-Stat Summary Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RecordStatCard(
                        modifier = Modifier.weight(1f),
                        value = "$totalVisits",
                        label = "Total Visits",
                        icon = Icons.Default.History,
                        bg = PastelLightBlue,
                        iconTint = PastelLightBlueIcon
                    )

                    RecordStatCard(
                        modifier = Modifier.weight(1f),
                        value = "$activeVisits",
                        label = "Active Visit",
                        icon = Icons.Default.HourglassTop,
                        bg = if (activeVisits > 0) AlertOrange.copy(alpha = 0.15f) else HealthBlueSoft,
                        iconTint = if (activeVisits > 0) AlertOrange else HealthNavy
                    )

                    RecordStatCard(
                        modifier = Modifier.weight(1f),
                        value = "$documentsCount",
                        label = "Documents",
                        icon = Icons.Default.Description,
                        bg = PastelLavender,
                        iconTint = PastelLavenderIcon
                    )

                    RecordStatCard(
                        modifier = Modifier.weight(1f),
                        value = "$completedVisits",
                        label = "Completed",
                        icon = Icons.Default.CheckCircle,
                        bg = PastelMint,
                        iconTint = PastelMintIcon
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (records.isEmpty()) {
                item {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(HealthBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Health Records Found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "When you complete a self-intake or document scan, your visit card will appear here for easy review and doctor handover.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = HealthTextSecondary,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onStartNewIntake,
                            colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("start_intake_from_records")
                        ) {
                            Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start New Consultation Intake")
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Previous & Active Visits (${records.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 15.sp
                        )
                    )
                }

                items(records, key = { it.tokenNumber }) { record ->
                    PatientRecordCard(
                        record = record,
                        onClick = { selectedRecordForDetails = record },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "MediKiosk Clinical Summary #${record.tokenNumber}"
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    """
                                    ==================================
                                    MEDIKIOSK CLINICAL INTAKE RECORD
                                    ==================================
                                    Token: #${record.tokenNumber}
                                    Patient: ${record.patientName} (${record.patientAge}y ${record.patientGender})
                                    Date: ${record.formattedDate}
                                    Status: ${record.status}
                                    Triage Priority: ${record.triageLevel}
                                    
                                    CHIEF COMPLAINT:
                                    ${record.chiefComplaint}
                                    
                                    MEDICATIONS & ALLERGIES:
                                    ${record.medicationsSummary}
                                    
                                    CLINICAL SUMMARY:
                                    ${record.clinicalSummaryJson}
                                    ==================================
                                    """.trimIndent()
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Clinical Record"))
                        },
                        onDelete = { onDeleteRecord(record.tokenNumber) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Full Detail Dialog for Patient Record
    if (selectedRecordForDetails != null) {
        val record = selectedRecordForDetails!!
        AlertDialog(
            onDismissRequest = { selectedRecordForDetails = null },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "MediKiosk Intake #${record.tokenNumber}")
                            putExtra(Intent.EXTRA_TEXT, "Patient: ${record.patientName}\nToken: #${record.tokenNumber}\nTriage: ${record.triageLevel}\n\nChief Complaint:\n${record.chiefComplaint}\n\nSummary:\n${record.clinicalSummaryJson}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Record"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedRecordForDetails = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = record.patientName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader
                            )
                        )
                        Text(
                            text = "#${record.tokenNumber} • ${record.formattedDate}",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (record.isUrgent) AlertRed else HealthBlueSoft
                    ) {
                        Text(
                            text = if (record.isUrgent) "URGENT" else "ROUTINE",
                            color = if (record.isUrgent) Color.White else HealthNavy,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (record.isUrgent && record.urgentReason != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AlertRed.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AlertRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = record.urgentReason,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AlertRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    DetailSection(title = "Queue Status", content = record.status)
                    DetailSection(title = "Triage Priority", content = record.triageLevel)
                    DetailSection(title = "Chief Complaint", content = record.chiefComplaint)
                    DetailSection(title = "Medications / Allergies", content = record.medicationsSummary)
                    DetailSection(title = "Clinical Summary Breakdown", content = record.clinicalSummaryJson)
                }
            },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun RecordStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    bg: Color,
    iconTint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = HealthCardBg,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 16.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = HealthTextSecondary,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PatientRecordCard(
    record: PatientRecordEntity,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("patient_record_card_${record.tokenNumber}"),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(
            if (record.isUrgent) 1.5.dp else 1.dp,
            if (record.isUrgent) AlertRed.copy(alpha = 0.5f) else HealthCardBorder
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Token + Name + Date + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = HealthNavy
                    ) {
                        Text(
                            text = "#${record.tokenNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.patientName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = record.formattedDate,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (record.status) {
                        "SEEN" -> SuccessGreen.copy(alpha = 0.15f)
                        "IN_CONSULTATION" -> HealthBlueSoft
                        "COMPLETED" -> SuccessGreen.copy(alpha = 0.15f)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = when (record.status) {
                            "SEEN", "COMPLETED" -> "✅ Completed"
                            "IN_CONSULTATION" -> "🩺 In Consult"
                            else -> "⏳ In Queue"
                        },
                        color = when (record.status) {
                            "SEEN", "COMPLETED" -> SuccessGreen
                            "IN_CONSULTATION" -> HealthNavy
                            else -> HealthNavy
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chief complaint
            Text(
                text = record.chiefComplaint,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = HealthTextPrimary,
                    fontSize = 13.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = HealthCardBorderSubtle)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer Row: Priority Badge + Actions (Share / Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (record.isUrgent) AlertRed.copy(alpha = 0.12f) else HealthBlueSoft
                ) {
                    Text(
                        text = if (record.isUrgent) "URGENT PRIORITY" else "ROUTINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (record.isUrgent) AlertRed else HealthBlueAccent,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = HealthNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = HealthTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String) {
    if (content.isBlank()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = HealthNavy
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = HealthBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextPrimary),
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
