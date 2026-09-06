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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientInfo
import com.example.ui.components.KioskHeader
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
fun PatientProfileScreen(
    patientInfo: PatientInfo,
    onEditDemographics: () -> Unit,
    onViewRecords: () -> Unit,
    onStartNewVisit: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    val displayName = if (patientInfo.name.isNotBlank()) patientInfo.name else "Ramesh Kumar"
    val displayAge = if (patientInfo.age.isNotBlank()) patientInfo.age else "52"
    val displayGender = if (patientInfo.gender.isNotBlank()) patientInfo.gender else "Male"
    val displayAbha = if (patientInfo.abhaId.isNotBlank()) patientInfo.abhaId else "91-3829-1029-4401"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("patient_profile_screen")
    ) {
        KioskHeader(
            title = "Patient Profile",
            subtitle = "Digital Health Identity & History",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Header Card with Avatar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_profile_card"),
                shape = RoundedCornerShape(24.dp),
                color = HealthCardBg,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(HealthNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "PT" },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$displayAge Yrs • $displayGender • Outpatient ID #MK-${patientInfo.tokenNumber}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = HealthTextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = onEditDemographics,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HealthBlueSoft)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Demographics",
                                tint = HealthNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = HealthCardBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mock ABHA ID Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = HealthBlueSoft.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HealthNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ABHA Health Address",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = displayAbha,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HealthNavy,
                                            letterSpacing = 0.5.sp,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PastelMint
                            ) {
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PastelMintIcon,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Known Allergies Section
            ProfileSectionCard(
                title = "Known Drug & Food Allergies",
                icon = Icons.Default.Warning,
                iconTint = AlertOrange,
                iconBg = PastelPeach
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AllergyPill(name = "Penicillin", reaction = "Skin rash & urticaria (Moderate)")
                    AllergyPill(name = "Sulfa Drugs", reaction = "Facial swelling & angioedema (Severe)")
                }
            }

            // Current Active Medications
            ProfileSectionCard(
                title = "Current Medications",
                icon = Icons.Default.Medication,
                iconTint = PastelLightBlueIcon,
                iconBg = PastelLightBlue
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MedicationProfileRow(name = "Metformin HCl", dosage = "500 mg", frequency = "Twice daily after meals", forCondition = "Type 2 Diabetes")
                    MedicationProfileRow(name = "Amlodipine Besylate", dosage = "5 mg", frequency = "Once daily in the morning", forCondition = "Hypertension")
                    MedicationProfileRow(name = "Atorvastatin Calcium", dosage = "10 mg", frequency = "Once daily at bedtime", forCondition = "Hyperlipidemia")
                }
            }

            // Previous Hospital Visits
            ProfileSectionCard(
                title = "Previous Clinic Visits",
                icon = Icons.Default.History,
                iconTint = PastelLavenderIcon,
                iconBg = PastelLavender
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PastVisitRow(
                        token = "MK-2910",
                        date = "18 Jan 2026",
                        doctor = "Dr. S. Mehta (Cardiology)",
                        diagnosis = "Hypertension routine follow-up"
                    )
                    PastVisitRow(
                        token = "MK-1044",
                        date = "04 Nov 2025",
                        doctor = "Dr. A. Roy (Internal Medicine)",
                        diagnosis = "Viral upper respiratory infection"
                    )
                }
            }

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewRecords,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, HealthNavy),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_view_records_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = HealthNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("My Records", color = HealthNavy, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onStartNewVisit,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_start_visit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Visit", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = HealthCardBg,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader,
                        fontSize = 15.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun AllergyPill(name: String, reaction: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AlertRed.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AlertRed)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AlertRed,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = reaction,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MedicationProfileRow(
    name: String,
    dosage: String,
    frequency: String,
    forCondition: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HealthBackground,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$name ($dosage)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavy,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "$frequency • For $forCondition",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun PastVisitRow(
    token: String,
    date: String,
    doctor: String,
    diagnosis: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HealthBackground,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = token,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthBlueAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = HealthTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = diagnosis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = HealthNavy,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = doctor,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
