package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.SyncStatus
import com.example.data.model.Language
import com.example.data.model.PatientInfo
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
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon
import com.example.ui.theme.SuccessGreen

enum class QueueStepState {
    COMPLETED,
    IN_PROGRESS,
    PENDING
}

@Composable
fun QueueTrackerScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    queuePosition: Int,
    patientLiveStatus: String,
    syncStatus: SyncStatus,
    lastUpdatedTime: String = "Just now",
    onBackClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onViewRecords: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Status-driven banner and timeline calculations
    val statusPrompt = when (patientLiveStatus) {
        "SEEN" -> "Please be ready. Staff has reviewed your intake."
        "IN_CONSULTATION" -> "Your consultation is starting in Consulting Room 3."
        "COMPLETED" -> "Your visit has been completed. Check My Records for your discharge advice."
        else -> "You are in the queue. Please have a seat in the waiting area."
    }

    val statusPillColor = when (patientLiveStatus) {
        "COMPLETED" -> SuccessGreen
        "IN_CONSULTATION" -> HealthBlueAccent
        "SEEN" -> AlertOrange
        else -> HealthNavy
    }

    val statusPillBg = when (patientLiveStatus) {
        "COMPLETED" -> SuccessGreen.copy(alpha = 0.12f)
        "IN_CONSULTATION" -> HealthBlueSoft
        "SEEN" -> AlertOrange.copy(alpha = 0.12f)
        else -> HealthBlueSoft
    }

    val statusTitle = when (patientLiveStatus) {
        "COMPLETED" -> "Visit Completed"
        "IN_CONSULTATION" -> "In Consultation"
        "SEEN" -> "Reviewed by Staff"
        else -> "Waiting for Doctor"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("queue_tracker_screen")
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Live Queue Tracking",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Real-time outpatient consultation status",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (syncStatus == SyncStatus.SYNCED) SuccessGreen.copy(alpha = 0.25f) else AlertOrange.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (syncStatus == SyncStatus.SYNCED) SuccessGreen else AlertOrange)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (syncStatus == SyncStatus.SYNCED) "Live Sync" else "Cached",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = statusPillBg,
                border = BorderStroke(1.dp, statusPillColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusPillColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (patientLiveStatus) {
                                "IN_CONSULTATION" -> Icons.Default.MeetingRoom
                                "COMPLETED" -> Icons.Default.CheckCircle
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = statusPillColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusPillColor
                            )
                        )
                        Text(
                            text = statusPrompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthNavyHeader,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Big Token Number Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("queue_token_card"),
                shape = RoundedCornerShape(24.dp),
                color = HealthCardBg,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR QUEUE TOKEN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthTextSecondary,
                            letterSpacing = 1.2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        modifier = Modifier.scale(if (patientLiveStatus == "WAITING") pulseScale else 1f),
                        shape = RoundedCornerShape(18.dp),
                        color = HealthNavy
                    ) {
                        Text(
                            text = "#${patientInfo.tokenNumber.ifBlank { "MK-4822" }}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 34.sp,
                                letterSpacing = 2.sp
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = patientInfo.name.ifBlank { "Patient #${patientInfo.tokenNumber}" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Queue Position Text
                    if (patientLiveStatus == "WAITING" || patientLiveStatus == "SEEN") {
                        Text(
                            text = if (queuePosition <= 1) "You are next in line!" else "You are currently #$queuePosition in the queue",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthBlueAccent,
                                fontSize = 15.sp
                            )
                        )
                    } else if (patientLiveStatus == "IN_CONSULTATION") {
                        Text(
                            text = "Doctor is currently consulting with you",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                fontSize = 14.sp
                            )
                        )
                    } else {
                        Text(
                            text = "Consultation finished",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthTextSecondary,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = HealthCardBorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 3-Metric Summary Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QueueMetricItem(
                            icon = Icons.Default.People,
                            value = if (patientLiveStatus == "WAITING") "${(queuePosition - 1).coerceAtLeast(0)}" else "0",
                            label = "Patients Ahead"
                        )

                        QueueMetricItem(
                            icon = Icons.Default.AccessTime,
                            value = if (patientLiveStatus == "WAITING") "~${(queuePosition * 4 + 2)} min" else "0 min",
                            label = "Approx. Waiting*"
                        )

                        QueueMetricItem(
                            icon = Icons.Default.Refresh,
                            value = lastUpdatedTime,
                            label = "Last Updated"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "*Approximate estimation based on average consultation time. Triage priority may affect sequence.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = HealthTextMuted,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Visual Progress Timeline
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = HealthCardBg,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Visit Progress Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TimelineStepRow(
                        stepNumber = 1,
                        title = "Patient Registration",
                        subtitle = "Demographics & consent verified",
                        state = QueueStepState.COMPLETED,
                        isLast = false
                    )

                    TimelineStepRow(
                        stepNumber = 2,
                        title = "Medical History & Symptoms",
                        subtitle = "Conversational SOCRATES assessment",
                        state = QueueStepState.COMPLETED,
                        isLast = false
                    )

                    TimelineStepRow(
                        stepNumber = 3,
                        title = "Medical Records & Documents",
                        subtitle = "Prescriptions & lab values attached",
                        state = QueueStepState.COMPLETED,
                        isLast = false
                    )

                    TimelineStepRow(
                        stepNumber = 4,
                        title = "Waiting for Doctor",
                        subtitle = if (patientLiveStatus == "WAITING") "Triage queue #${patientInfo.tokenNumber}" else "Staff triaged",
                        state = when (patientLiveStatus) {
                            "WAITING" -> QueueStepState.IN_PROGRESS
                            "SEEN", "IN_CONSULTATION", "COMPLETED" -> QueueStepState.COMPLETED
                            else -> QueueStepState.PENDING
                        },
                        isLast = false
                    )

                    TimelineStepRow(
                        stepNumber = 5,
                        title = "Doctor Consultation",
                        subtitle = if (patientLiveStatus == "IN_CONSULTATION") "Active now in Room 3" else "Consulting Room 3 • Dr. S. Mehta",
                        state = when (patientLiveStatus) {
                            "IN_CONSULTATION" -> QueueStepState.IN_PROGRESS
                            "COMPLETED" -> QueueStepState.COMPLETED
                            else -> QueueStepState.PENDING
                        },
                        isLast = false
                    )

                    TimelineStepRow(
                        stepNumber = 6,
                        title = "Visit Completed",
                        subtitle = "Discharge instructions & summary archived",
                        state = if (patientLiveStatus == "COMPLETED") QueueStepState.COMPLETED else QueueStepState.PENDING,
                        isLast = true
                    )
                }
            }

            // Quick Help & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNeedHelpClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPeach),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("queue_need_help_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        tint = PastelPeachIcon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🆘 Need Help", color = HealthNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onViewRecords,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSoft),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("queue_view_records_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = HealthNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Records", color = HealthNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun QueueMetricItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HealthNavy,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = HealthNavyHeader,
                fontSize = 15.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = HealthTextSecondary,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun TimelineStepRow(
    stepNumber: Int,
    title: String,
    subtitle: String,
    state: QueueStepState,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left Column: Circle Icon + Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (state) {
                            QueueStepState.COMPLETED -> SuccessGreen
                            QueueStepState.IN_PROGRESS -> HealthBlueAccent
                            QueueStepState.PENDING -> HealthCardBorderSubtle
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    QueueStepState.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    QueueStepState.IN_PROGRESS -> {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                    QueueStepState.PENDING -> {
                        Text(
                            text = "$stepNumber",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HealthTextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(
                            if (state == QueueStepState.COMPLETED) SuccessGreen else HealthCardBorderSubtle
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Title + Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (state == QueueStepState.IN_PROGRESS) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (state == QueueStepState.PENDING) HealthTextMuted else HealthNavyHeader,
                    fontSize = 14.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (state == QueueStepState.IN_PROGRESS) HealthBlueAccent else HealthTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}
