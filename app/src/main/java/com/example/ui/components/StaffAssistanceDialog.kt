package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HearingDisabled
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon

data class AssistanceOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
fun StaffAssistanceDialog(
    kioskId: String = "Kiosk 01 (Outpatient)",
    onDismiss: () -> Unit,
    onRequestAssistance: (AssistanceOption) -> Unit
) {
    val options = remember {
        listOf(
            AssistanceOption(
                id = "CALL_STAFF",
                title = "Call Attending Nurse",
                description = "Request in-person assistance at this kiosk",
                icon = Icons.Default.PhoneInTalk,
                iconBg = PastelPeach,
                iconTint = PastelPeachIcon
            ),
            AssistanceOption(
                id = "NEED_TRANSLATION",
                title = "Need Language Translator",
                description = "Request staff fluent in local regional dialect",
                icon = Icons.Default.Translate,
                iconBg = HealthBlueSoft,
                iconTint = HealthNavy
            ),
            AssistanceOption(
                id = "HEARING_ASSIST",
                title = "Hearing / Audio Assistance",
                description = "Need louder volume or visual-only assistance",
                icon = Icons.Default.HearingDisabled,
                iconBg = HealthBackground,
                iconTint = HealthNavy
            ),
            AssistanceOption(
                id = "VISION_ASSIST",
                title = "Reading / Vision Assistance",
                description = "Need help reading questions on screen",
                icon = Icons.Default.VisibilityOff,
                iconBg = HealthBackground,
                iconTint = HealthNavy
            ),
            AssistanceOption(
                id = "TROUBLE_KIOSK",
                title = "Trouble Using Kiosk",
                description = "Technical issue or unsure how to continue",
                icon = Icons.Default.HelpOutline,
                iconBg = HealthBackground,
                iconTint = HealthNavy
            )
        )
    }

    var selectedOption by remember { mutableStateOf<AssistanceOption?>(options.first()) }
    var submitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("staff_assistance_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = HealthCardBg,
        tonalElevation = 6.dp,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AlertRed.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🆘", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Staff Assistance",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = kioskId,
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = HealthTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (submitted) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = HealthBlueSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔔 Assistance Dispatched",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A hospital care coordinator has been notified for ${selectedOption?.title ?: "assistance"}. Please remain near this kiosk.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "How can our hospital team assist you today?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = HealthNavy
                        )
                    )

                    options.forEach { opt ->
                        val isSelected = selectedOption?.id == opt.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedOption = opt }
                                .testTag("assistance_option_${opt.id}"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) HealthBlueSoft else HealthCardBg,
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) HealthNavy else HealthCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(opt.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = opt.icon,
                                        contentDescription = null,
                                        tint = opt.iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = opt.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) HealthNavy else HealthTextPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = opt.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = HealthTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (submitted) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                    modifier = Modifier.testTag("dismiss_assistance_button")
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        selectedOption?.let {
                            onRequestAssistance(it)
                            submitted = true
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    modifier = Modifier.testTag("submit_assistance_button")
                ) {
                    Text("Call Staff", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel", color = HealthTextSecondary)
                }
            }
        }
    )
}
