package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.SyncStatus
import com.example.data.model.ClinicalSummary
import com.example.data.model.Language
import com.example.data.model.PatientInfo
import com.example.ui.components.KioskHeader
import com.example.ui.components.LargeKioskButton
import com.example.ui.theme.AlertOrange
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
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.SuccessGreen

enum class QueueStepStatus {
    COMPLETED,
    ACTIVE,
    UPCOMING
}

data class QueueTimelineStep(
    val title: String,
    val description: String,
    val status: QueueStepStatus
)

@Composable
fun ConfirmationScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    clinicalSummary: ClinicalSummary?,
    queuePosition: Int = 3,
    patientLiveStatus: String = "WAITING", // WAITING, IN_CONSULTATION, SEEN, COMPLETED
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    onStartNewIntake: () -> Unit,
    onNeedHelpClick: () -> Unit = {}
) {
    val scaleAnim = remember { Animatable(0f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Build timeline steps based on live status
    val timelineSteps = remember(patientLiveStatus) {
        listOf(
            QueueTimelineStep(
                title = "Registration Complete",
                description = "Demographics & ABHA verification",
                status = QueueStepStatus.COMPLETED
            ),
            QueueTimelineStep(
                title = "Medical History Complete",
                description = "AI SOCRATES symptom intake",
                status = QueueStepStatus.COMPLETED
            ),
            QueueTimelineStep(
                title = "Documents Processed",
                description = "OCR prescriptions & lab records attached",
                status = QueueStepStatus.COMPLETED
            ),
            QueueTimelineStep(
                title = "Waiting for Doctor",
                description = when (patientLiveStatus) {
                    "IN_CONSULTATION" -> "Attending physician called your token"
                    "SEEN", "COMPLETED" -> "Consultation completed"
                    else -> "In queue for Room 3 (Outpatient Clinic B)"
                },
                status = when (patientLiveStatus) {
                    "WAITING" -> QueueStepStatus.ACTIVE
                    else -> QueueStepStatus.COMPLETED
                }
            ),
            QueueTimelineStep(
                title = "Doctor Consultation",
                description = when (patientLiveStatus) {
                    "IN_CONSULTATION" -> "Active in Room 3 with Dr. S. Mehta"
                    "SEEN", "COMPLETED" -> "Completed"
                    else -> "Awaiting room call"
                },
                status = when (patientLiveStatus) {
                    "IN_CONSULTATION" -> QueueStepStatus.ACTIVE
                    "SEEN", "COMPLETED" -> QueueStepStatus.COMPLETED
                    else -> QueueStepStatus.UPCOMING
                }
            ),
            QueueTimelineStep(
                title = "Pharmacy & Discharge",
                description = if (patientLiveStatus == "SEEN" || patientLiveStatus == "COMPLETED") "Prescription sent to pharmacy counter" else "After consultation",
                status = if (patientLiveStatus == "SEEN" || patientLiveStatus == "COMPLETED") QueueStepStatus.ACTIVE else QueueStepStatus.UPCOMING
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("confirmation_screen")
    ) {
        KioskHeader(
            title = "Queue Status & Check-in",
            tokenNumber = patientInfo.tokenNumber,
            currentLanguage = selectedLanguage
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Header Animation
            Box(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Intake Transmitted to Staff",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your preliminary summary is live on the doctor's workstation.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = HealthTextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                )
            }

            // Real-time Queue Token Hero Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("queue_token_hero_card"),
                shape = RoundedCornerShape(24.dp),
                color = HealthCardBg,
                border = BorderStroke(1.5.dp, HealthNavy),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HealthBlueSoft
                        ) {
                            Text(
                                text = "OUTPATIENT CLINIC B",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Live Sync Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (syncStatus == SyncStatus.SYNCED) SuccessGreen else AlertOrange)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (syncStatus == SyncStatus.SYNCED) "● Live Synced" else "🟡 Syncing...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (syncStatus == SyncStatus.SYNCED) SuccessGreen else AlertOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "YOUR QUEUE TOKEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = HealthTextSecondary,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "#MK-${patientInfo.tokenNumber}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = HealthNavy,
                            letterSpacing = 1.sp,
                            fontSize = 34.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic queue position badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (patientLiveStatus) {
                            "IN_CONSULTATION" -> PastelMint
                            "SEEN", "COMPLETED" -> HealthBlueSoft
                            else -> HealthNavy
                        }
                    ) {
                        Text(
                            text = when (patientLiveStatus) {
                                "IN_CONSULTATION" -> "🔔 NOW CALLED IN ROOM 3"
                                "SEEN", "COMPLETED" -> "✓ VISIT COMPLETED"
                                else -> "You are #$queuePosition in the queue (approx ~${(queuePosition * 6).coerceAtLeast(5)} mins)"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (patientLiveStatus) {
                                    "IN_CONSULTATION" -> PastelMintIcon
                                    "SEEN", "COMPLETED" -> HealthNavy
                                    else -> Color.White
                                },
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = HealthCardBorderSubtle)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Patient: ${patientInfo.name.ifBlank { "Registered Patient" }} (${patientInfo.age.ifBlank { "52" }} Y)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = if (patientInfo.abhaId.isNotBlank()) "ABHA: ${patientInfo.abhaId}" else "ABHA: 91-3829-1029-4401",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthNavy,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Interactive Queue Timeline
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("queue_timeline_card"),
                shape = RoundedCornerShape(20.dp),
                color = HealthCardBg,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Visit Progress Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    timelineSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Icon indicator & vertical line
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                when (step.status) {
                                    QueueStepStatus.COMPLETED -> {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(SuccessGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    QueueStepStatus.ACTIVE -> {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(HealthNavy),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                            )
                                        }
                                    }
                                    QueueStepStatus.UPCOMING -> {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(HealthBackground)
                                                .border(BorderStroke(1.5.dp, HealthCardBorder), CircleShape)
                                        )
                                    }
                                }

                                if (index < timelineSteps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(28.dp)
                                            .background(
                                                if (step.status == QueueStepStatus.COMPLETED) SuccessGreen.copy(alpha = 0.5f) else HealthCardBorder
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (step.status == QueueStepStatus.ACTIVE) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (step.status == QueueStepStatus.UPCOMING) HealthTextMuted else HealthNavyHeader,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (step.status == QueueStepStatus.ACTIVE) HealthNavy else HealthTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            // Need Assistance Action Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = HealthBlueSoft.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, HealthCardBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🆘", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Need Help While Waiting?",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy
                                )
                            )
                            Text(
                                text = "Staff are available at the front desk",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onNeedHelpClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, HealthNavy),
                        modifier = Modifier.testTag("queue_need_help_button")
                    ) {
                        Text("Call Staff", color = HealthNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bottom Action Button: Start New Intake
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = HealthCardBg,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                LargeKioskButton(
                    text = "Finish & Start New Patient Intake",
                    icon = Icons.Default.Refresh,
                    onClick = onStartNewIntake,
                    testTag = "start_new_patient_button"
                )
            }
        }
    }
}
