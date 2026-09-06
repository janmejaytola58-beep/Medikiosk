package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.PatientInfo
import com.example.ui.components.AppHeader
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

@Composable
fun ProfileScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    hasActiveVisit: Boolean,
    activeVisitStatus: String,
    onNavigateToRecords: () -> Unit,
    onNavigateToAccessibility: () -> Unit,
    onNavigateToPrivacyCenter: () -> Unit,
    onNavigateToStaffView: () -> Unit,
    onNavigateToQueueTracker: () -> Unit,
    onResetSession: () -> Unit,
    onNeedHelpClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("profile_screen")
    ) {
        AppHeader(
            title = "Profile & Settings",
            subtitle = "Patient Details & Kiosk Configuration"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Identification Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_patient_card"),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(HealthBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = patientInfo.name.ifBlank { "Unregistered Patient" },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Token #${patientInfo.tokenNumber.ifBlank { "MK-4822" }} • ${patientInfo.age.ifBlank { "--" }} yrs • ${patientInfo.gender}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (hasActiveVisit) PastelMint else HealthBackground
                        ) {
                            Text(
                                text = if (hasActiveVisit) "Active" else "Idle",
                                color = if (hasActiveVisit) PastelMintIcon else HealthTextSecondary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = HealthCardBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileAttribute(label = "ABHA ID", value = patientInfo.abhaId.ifBlank { "Not Linked" })
                        ProfileAttribute(label = "Language", value = selectedLanguage.name)
                        ProfileAttribute(label = "Kiosk Node", value = "Desk #1")
                    }
                }
            }

            // Quick Actions & Navigation
            Text(
                text = "Health & Records",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 14.sp
                )
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column {
                    SettingsRow(
                        title = "My Health Records",
                        subtitle = "View offline consultations, summaries and scans",
                        icon = Icons.Default.History,
                        iconBg = PastelLavender,
                        iconTint = PastelLavenderIcon,
                        onClick = onNavigateToRecords,
                        testTag = "settings_my_records"
                    )

                    HorizontalDivider(color = HealthCardBorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsRow(
                        title = "Live Queue Tracker",
                        subtitle = "Check your current queue position and consulting room",
                        icon = Icons.Default.Badge,
                        iconBg = PastelLightBlue,
                        iconTint = PastelLightBlueIcon,
                        onClick = onNavigateToQueueTracker,
                        testTag = "settings_queue_tracker"
                    )

                    HorizontalDivider(color = HealthCardBorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsRow(
                        title = "Accessibility Center",
                        subtitle = "Text scale, voice guidance, high contrast & speed",
                        icon = Icons.Default.Tune,
                        iconBg = PastelMint,
                        iconTint = PastelMintIcon,
                        onClick = onNavigateToAccessibility,
                        testTag = "settings_accessibility"
                    )
                }
            }

            Text(
                text = "Privacy & Support",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 14.sp
                )
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column {
                    SettingsRow(
                        title = "Privacy & Data Policy",
                        subtitle = "Demonstration disclaimer and patient confidentiality",
                        icon = Icons.Default.PrivacyTip,
                        iconBg = PastelPeach,
                        iconTint = PastelPeachIcon,
                        onClick = onNavigateToPrivacyCenter,
                        testTag = "settings_privacy"
                    )

                    HorizontalDivider(color = HealthCardBorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsRow(
                        title = "Request Staff Assistance",
                        subtitle = "Alert on-duty nurse or attending coordinator",
                        icon = Icons.Default.PhoneInTalk,
                        iconBg = AlertRed.copy(alpha = 0.1f),
                        iconTint = AlertRed,
                        onClick = onNeedHelpClick,
                        testTag = "settings_need_help"
                    )
                }
            }

            Text(
                text = "Staff & Administration",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 14.sp
                )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onNavigateToStaffView)
                    .testTag("settings_staff_mode"),
                shape = RoundedCornerShape(20.dp),
                color = HealthNavy,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hospital Staff Portal",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Live queue triage, red-flag monitoring & room calls",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Session Reset Button
            OutlinedButton(
                onClick = onResetSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reset_kiosk_session_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AlertRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Kiosk for Next Patient", color = AlertRed, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileAttribute(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = HealthTextSecondary,
                fontSize = 10.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = HealthNavyHeader,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = HealthTextSecondary,
                    fontSize = 11.sp
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = HealthTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
