package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.SyncStatus
import com.example.data.local.entity.PatientRecordEntity
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
import com.example.ui.theme.PastelLightBlue
import com.example.ui.theme.PastelLightBlueIcon
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon
import com.example.ui.theme.SuccessGreen

@Composable
fun WelcomeScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    hasActiveVisit: Boolean = false,
    activeVisitStatus: String = "WAITING",
    queuePosition: Int = 4,
    hasUnfinishedSession: Boolean = false,
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    unreadNotificationCount: Int = 1,
    recentRecords: List<PatientRecordEntity> = emptyList(),
    onResumeUnfinishedSession: () -> Unit = {},
    onDiscardUnfinishedSession: () -> Unit = {},
    onLanguageSelected: (Language) -> Unit,
    onSpeakLanguage: (Language) -> Unit,
    onStartIntake: () -> Unit,
    onScanDocuments: () -> Unit,
    onShortcutClick: (String) -> Unit,
    onTrackQueue: () -> Unit = {},
    onViewRecords: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenNotifications: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onOpenProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val patientGreeting = if (patientInfo.name.isNotBlank()) "Good morning, ${patientInfo.name.split(" ").firstOrNull() ?: ""} 👋" else "Good morning 👋"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("welcome_home_screen")
    ) {
        // Top Header Section: Hospital Logo + Actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = HealthCardBg,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(HealthNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Hospital Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "MediKiosk",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "AI-assisted patient intake",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Notification Bell with Badge
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HealthBackground)
                            .testTag("header_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(
                                        containerColor = AlertRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadNotificationCount", fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = HealthNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Accessibility Settings
                    IconButton(
                        onClick = onOpenAccessibility,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HealthBackground)
                            .testTag("header_accessibility_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Accessibility Center",
                            tint = HealthNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Profile
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HealthBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = HealthNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Main Scrollable Dashboard
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting & Kiosk Status
            Column {
                Text(
                    text = patientGreeting,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "How can we help you today?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = HealthTextSecondary,
                        fontSize = 16.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(HealthCardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, HealthCardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (syncStatus == SyncStatus.SYNCED) SuccessGreen else AlertOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (syncStatus == SyncStatus.SYNCED) "Kiosk Online • Kiosk A" else "Offline • Kiosk A",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HealthTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Unfinished Intake Session Recovery Banner
            if (hasUnfinishedSession) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = PastelPeach,
                    border = BorderStroke(1.dp, AlertOrange.copy(alpha = 0.4f)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = PastelPeachIcon,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue Your Visit?",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You have an unfinished intake assessment in progress from your earlier session.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthNavyHeader,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onResumeUnfinishedSession,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Continue Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDiscardUnfinishedSession,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start New", fontSize = 12.sp, color = HealthNavy)
                            }
                        }
                    }
                }
            }

            // Search Bar: Search health records
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = HealthCardBg,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search your health records, meds or labs...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = HealthTextMuted,
                                fontSize = 13.sp
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = HealthNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = HealthTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }

            // Two Large Side-by-Side Primary Action Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Start New Visit (Intake)
                HomePrimaryCard(
                    title = "Start New Visit",
                    subtitle = "Tell us how you're feeling",
                    badge = "AI Triage",
                    icon = Icons.Default.Forum,
                    accentColor = HealthNavy,
                    bgTint = HealthBlueSoft,
                    onClick = onStartIntake,
                    modifier = Modifier.weight(1f),
                    testTag = "card_start_new_visit"
                )

                // Card 2: Scan Documents
                HomePrimaryCard(
                    title = "Scan Documents",
                    subtitle = "Prescription, lab reports",
                    badge = "OCR AI",
                    icon = Icons.Default.Description,
                    accentColor = HealthBlueAccent,
                    bgTint = PastelLightBlue,
                    onClick = onScanDocuments,
                    modifier = Modifier.weight(1f),
                    testTag = "card_scan_documents"
                )
            }

            // Quick Clinical Shortcuts (4 Icon-in-Circle Cards)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Clinical Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShortcutCircleItem(
                        label = "Chief\nComplaint",
                        icon = Icons.Default.MedicalServices,
                        circleBg = PastelLightBlue,
                        iconTint = PastelLightBlueIcon,
                        onClick = { onShortcutClick("CHIEF_COMPLAINT") },
                        testTag = "shortcut_chief_complaint"
                    )

                    ShortcutCircleItem(
                        label = "Medications\nList",
                        icon = Icons.Default.Medication,
                        circleBg = PastelPeach,
                        iconTint = PastelPeachIcon,
                        onClick = { onShortcutClick("MEDICATIONS") },
                        testTag = "shortcut_medications"
                    )

                    ShortcutCircleItem(
                        label = "Lab\nReports",
                        icon = Icons.Default.Science,
                        circleBg = PastelLavender,
                        iconTint = PastelLavenderIcon,
                        onClick = { onShortcutClick("LAB_REPORTS") },
                        testTag = "shortcut_lab_reports"
                    )

                    ShortcutCircleItem(
                        label = "Clinical\nSummary",
                        icon = Icons.Default.Assignment,
                        circleBg = PastelMint,
                        iconTint = PastelMintIcon,
                        onClick = { onShortcutClick("SUMMARY") },
                        testTag = "shortcut_summary"
                    )
                }
            }

            // Current Visit Hero Card (If Active Visit Exists or Queue Token Generated)
            if (hasActiveVisit || patientInfo.tokenNumber.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onTrackQueue)
                        .testTag("active_visit_hero_card"),
                    shape = RoundedCornerShape(22.dp),
                    color = HealthCardBg,
                    border = BorderStroke(1.5.dp, HealthNavy),
                    shadowElevation = 3.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HealthBlueSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏥", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Current Hospital Visit",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HealthNavy
                                        )
                                    )
                                    Text(
                                        text = "Queue Token #${patientInfo.tokenNumber.ifBlank { "MK-4822" }}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = HealthNavyHeader,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when (activeVisitStatus) {
                                    "IN_CONSULTATION" -> PastelMint
                                    "SEEN", "COMPLETED" -> SuccessGreen.copy(alpha = 0.15f)
                                    else -> AlertOrange.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = when (activeVisitStatus) {
                                        "IN_CONSULTATION" -> "Room 3 Called"
                                        "SEEN", "COMPLETED" -> "Completed"
                                        else -> "In Queue (#$queuePosition)"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (activeVisitStatus) {
                                            "IN_CONSULTATION" -> PastelMintIcon
                                            "SEEN", "COMPLETED" -> SuccessGreen
                                            else -> AlertOrange
                                        },
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = {
                                when (activeVisitStatus) {
                                    "IN_CONSULTATION" -> 0.8f
                                    "SEEN", "COMPLETED" -> 1.0f
                                    else -> 0.45f
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = HealthNavy,
                            trackColor = HealthBackground,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Est. Wait: ~${(queuePosition * 4 + 2)} mins • Room 3",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary,
                                    fontSize = 11.sp
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(onClick = onTrackQueue)
                            ) {
                                Text(
                                    text = "Track Live Queue",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthBlueAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = HealthBlueAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = HealthCardBg,
                    border = BorderStroke(1.5.dp, HealthCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No active visit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start a new visit to begin",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = HealthTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onStartIntake,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthBlueAccent)
                        ) {
                            Text("Start Visit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Recent Records Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Records",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "View All →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthBlueAccent
                        ),
                        modifier = Modifier.clickable(onClick = onViewRecords)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                if (recentRecords.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                        color = HealthCardBg,
                        border = BorderStroke(1.dp, HealthCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = HealthTextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No visits yet",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Your completed visits will appear here.",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(onClick = onStartIntake, shape = RoundedCornerShape(12.dp)) {
                                Text("Start New Visit")
                            }
                        }
                    }
                } else {
                    recentRecords.take(3).forEach { record ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { },
                            color = HealthCardBg,
                            border = BorderStroke(1.dp, HealthCardBorder),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = record.tokenNumber.take(5).uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthTextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = record.chiefComplaint.takeIf { it.isNotBlank() } ?: "General Checkup",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HealthNavy
                                        )
                                    )
                                    Text(
                                        text = "Today",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = HealthTextSecondary
                                        )
                                    )
                                }
                                
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SuccessGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Completed",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Recent Activity Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "See All →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthBlueAccent
                        ),
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = HealthCardBg,
                    border = BorderStroke(1.dp, HealthCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ActivityItemRow(
                            title = "History completed",
                            subtitle = "Your medical history has been saved",
                            time = "2 min ago",
                            isCompleted = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActivityItemRow(
                            title = "Patient Registration",
                            subtitle = if (patientInfo.name.isNotBlank()) "${patientInfo.name} registered" else "Anonymous check-in",
                            time = "10 min ago",
                            isCompleted = true
                        )
                    }
                }
            }

            // Need Help Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onNeedHelpClick)
                    .testTag("home_tile_need_help"),
                shape = RoundedCornerShape(20.dp),
                color = AlertRed.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.2f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AlertRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🆘", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Need Help?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed,
                                )
                            )
                            Text(
                                text = "Need assistance using the kiosk?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Get Help",
                        tint = AlertRed
                    )
                }
            }

            // Language Selection Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Language / भाषा चुनें",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Language.SUPPORTED_LANGUAGES.take(3).forEach { lang ->
                        val isSelected = lang.code == selectedLanguage.code
                        LanguagePillCard(
                            language = lang,
                            isSelected = isSelected,
                            onSelect = { onLanguageSelected(lang) },
                            onSpeak = { onSpeakLanguage(lang) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Language.SUPPORTED_LANGUAGES.drop(3).forEach { lang ->
                        val isSelected = lang.code == selectedLanguage.code
                        LanguagePillCard(
                            language = lang,
                            isSelected = isSelected,
                            onSelect = { onLanguageSelected(lang) },
                            onSpeak = { onSpeakLanguage(lang) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ActivityItemRow(
    title: String,
    subtitle: String,
    time: String,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isCompleted) SuccessGreen.copy(alpha = 0.15f) else HealthCardBorderSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                contentDescription = null,
                tint = if (isCompleted) SuccessGreen else HealthTextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader,
                    fontSize = 12.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = HealthTextSecondary,
                    fontSize = 10.sp
                )
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall.copy(
                color = HealthTextMuted,
                fontSize = 10.sp
            )
        )
    }
}

// Side-by-side Large Feature Card
@Composable
private fun HomePrimaryCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    bgTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "home_primary_card"
) {
    Surface(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(BorderStroke(1.dp, HealthCardBorderSubtle), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = HealthCardBg,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 15.sp,
                        lineHeight = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                )
            }
        }
    }
}

// Shortcut Icon-in-Circle with Pastel Background
@Composable
private fun ShortcutCircleItem(
    label: String,
    icon: ImageVector,
    circleBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String = "shortcut_circle_item"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(circleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label.replace("\n", " "),
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = HealthTextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 13.sp
            )
        )
    }
}

// Compact Language Pill Card
@Composable
private fun LanguagePillCard(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) HealthNavy else HealthCardBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val bgColor = if (isSelected) HealthBlueSoft else HealthCardBg

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .testTag("language_card_${language.code}"),
        color = bgColor,
        shadowElevation = if (isSelected) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.nativeName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) HealthNavy else HealthTextPrimary,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            IconButton(
                onClick = onSpeak,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Preview speech",
                    tint = if (isSelected) HealthNavy else HealthTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
