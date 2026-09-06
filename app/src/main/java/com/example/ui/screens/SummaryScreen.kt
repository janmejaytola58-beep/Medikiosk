package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicalSummary
import com.example.data.model.Language
import com.example.data.model.PatientInfo
import com.example.ui.components.KioskHeader
import com.example.ui.components.SkeletonBox
import com.example.ui.components.SkeletonCardPlaceholder
import com.example.ui.components.kioskInputTextStyle
import com.example.ui.components.kioskTextFieldColors
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
fun SummaryScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    clinicalSummary: ClinicalSummary?,
    isGenerating: Boolean,
    onRegenerate: () -> Unit,
    onUpdateSection: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var editingSectionTitle by remember { mutableStateOf<String?>(null) }
    var editingSectionContent by remember { mutableStateOf("") }
    var showShareModal by remember { mutableStateOf(false) }

    val formattedShareText = remember(clinicalSummary, patientInfo) {
        buildString {
            appendLine("════════════════════════════════════════")
            appendLine("CITY CARE HOSPITAL • PRE-CONSULT CLINICAL SUMMARY")
            appendLine("Token: #MK-${patientInfo.tokenNumber} | ABHA: ${patientInfo.abhaId.ifBlank { "N/A" }}")
            appendLine("Patient: ${patientInfo.name.ifBlank { "Anonymous" }}, ${patientInfo.age}y ${patientInfo.gender}")
            appendLine("════════════════════════════════════════")
            clinicalSummary?.let { s ->
                appendLine("\n[1] CHIEF COMPLAINT & HPI")
                appendLine(s.chiefComplaint)
                if (s.historyOfPresentIllness.isNotBlank()) {
                    appendLine(s.historyOfPresentIllness)
                }
                appendLine("\n[2] MEDICATIONS & ALLERGIES")
                appendLine("Meds & Allergies: ${s.drugAndAllergy}")
                appendLine("\n[3] PAST HISTORY & REVIEW OF SYSTEMS")
                appendLine("Past Hx: ${s.pastMedicalSurgical}")
                appendLine("Family: ${s.familyHistory}")
                appendLine("ROS: ${s.reviewOfSystems}")
                appendLine("\n[4] INVESTIGATIONS & LABS")
                appendLine(s.priorInvestigations)
                appendLine("\n[5] TRIAGE LEVEL")
                appendLine(s.triageLevel)
            }
            appendLine("════════════════════════════════════════")
            appendLine("Generated by AI Digital Intake Kiosk • EHR Ready")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("summary_screen")
    ) {
        KioskHeader(
            title = "Review Clinical Summary",
            subtitle = "Step 4 of 4 • Physician Pre-Consultation Summary",
            currentStep = 4,
            totalSteps = 4,
            stepIndicator = "Step 4 of 4",
            tokenNumber = patientInfo.tokenNumber,
            currentLanguage = selectedLanguage,
            onBackClick = onBackClick
        )

        if (isGenerating || clinicalSummary == null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shimmer Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = HealthNavy,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SkeletonBox(modifier = Modifier.size(44.dp), shape = CircleShape)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SkeletonBox(modifier = Modifier.width(160.dp).height(16.dp))
                            SkeletonBox(modifier = Modifier.width(220.dp).height(12.dp))
                        }
                    }
                }

                Text(
                    text = "Synthesizing conversational history & prescription data with Gemini AI...",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader
                    )
                )

                // Shimmer Placeholders for Clinical Cards
                SkeletonCardPlaceholder(lines = 4)
                SkeletonCardPlaceholder(lines = 3)
                SkeletonCardPlaceholder(lines = 3)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Patient Demographics Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = HealthNavy,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = patientInfo.name.ifBlank { "Unregistered Patient" },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Age: ${patientInfo.age.ifBlank { "--" }} Yrs • Gender: ${patientInfo.gender.ifBlank { "--" }} • Token #MK-${patientInfo.tokenNumber}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Share/Export Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable { showShareModal = true }
                                    .testTag("share_summary_card_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "EXPORT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            // Edit Demographics Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable {
                                        editingSectionTitle = "Patient Demographics"
                                        editingSectionContent = "${patientInfo.name} (${patientInfo.age} Yrs, ${patientInfo.gender})"
                                    }
                                    .testTag("edit_patient_identity")
                            ) {
                                Text(
                                    text = "EDIT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Section 1: Chief Complaint & HPI (with MedicalServices Icon)
                HistorySectionCard(
                    title = "Chief Complaint & HPI",
                    icon = Icons.Default.MedicalServices,
                    iconBg = PastelLightBlue,
                    iconTint = PastelLightBlueIcon,
                    content = if (clinicalSummary.historyOfPresentIllness.isNotBlank()) {
                        "${clinicalSummary.chiefComplaint}\n\n${clinicalSummary.historyOfPresentIllness}"
                    } else {
                        clinicalSummary.chiefComplaint
                    },
                    onEdit = {
                        editingSectionTitle = "Chief Complaint & HPI"
                        editingSectionContent = clinicalSummary.historyOfPresentIllness.ifBlank { clinicalSummary.chiefComplaint }
                    },
                    testTag = "doctor_section_chief_complaint"
                )

                // Row 2: Medications & Allergies
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left: Medications (with Medication Icon)
                    HistoryColumnCard(
                        modifier = Modifier.weight(1f),
                        title = "Medications",
                        icon = Icons.Default.Medication,
                        iconBg = PastelPeach,
                        iconTint = PastelPeachIcon,
                        content = clinicalSummary.drugAndAllergy.lineSequence()
                            .filter { it.contains("mg", ignoreCase = true) || it.contains("tab", ignoreCase = true) || it.contains("•") || it.contains("-") }
                            .joinToString("\n")
                            .ifBlank { clinicalSummary.drugAndAllergy.take(120) },
                        onEdit = {
                            editingSectionTitle = "Drug & Allergy History"
                            editingSectionContent = clinicalSummary.drugAndAllergy
                        },
                        testTag = "doctor_section_drug_&_allergy_history"
                    )

                    // Right: Allergies & Alerts (with Warning Icon)
                    HistoryColumnCard(
                        modifier = Modifier.weight(1f),
                        title = "Allergies",
                        icon = Icons.Default.Warning,
                        iconBg = Color(0xFFFFEBEE),
                        iconTint = AlertRed,
                        content = if (clinicalSummary.drugAndAllergy.contains("Allerg", ignoreCase = true) ||
                            clinicalSummary.drugAndAllergy.contains("Penicillin", ignoreCase = true) ||
                            clinicalSummary.drugAndAllergy.contains("Sulfa", ignoreCase = true)) {
                            clinicalSummary.drugAndAllergy.lineSequence().find { it.contains("Allerg", true) }
                                ?: "Penicillin documented"
                        } else {
                            "NKDA (No Known Drug Allergies)"
                        },
                        subNote = "Source: Vision Scan & Socrates",
                        onEdit = {
                            editingSectionTitle = "Drug & Allergy History"
                            editingSectionContent = clinicalSummary.drugAndAllergy
                        },
                        testTag = "doctor_section_allergies"
                    )
                }

                // Row 3: Past Medical & Family / Systems
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left: Past Medical History (with History Icon)
                    HistoryColumnCard(
                        modifier = Modifier.weight(1f),
                        title = "Past Medical",
                        icon = Icons.Default.History,
                        iconBg = PastelLavender,
                        iconTint = PastelLavenderIcon,
                        content = clinicalSummary.pastMedicalSurgical.ifBlank { "No prior major medical or surgical history reported." },
                        onEdit = {
                            editingSectionTitle = "Past Medical/Surgical History"
                            editingSectionContent = clinicalSummary.pastMedicalSurgical
                        },
                        testTag = "doctor_section_past_medical/surgical_history"
                    )

                    // Right: Family & Systems (with People Icon)
                    HistoryColumnCard(
                        modifier = Modifier.weight(1f),
                        title = "Family & Systems",
                        icon = Icons.Default.People,
                        iconBg = PastelMint,
                        iconTint = PastelMintIcon,
                        content = (clinicalSummary.familyHistory + "\n" + clinicalSummary.reviewOfSystems).trim().ifBlank { "Review of systems non-contributory." },
                        onEdit = {
                            editingSectionTitle = "Family & Systems History"
                            editingSectionContent = "${clinicalSummary.familyHistory}\n\n${clinicalSummary.reviewOfSystems}"
                        },
                        testTag = "doctor_section_family_history"
                    )
                }

                // Section 4: Prior Investigations & Lab Values (with Science Icon)
                HistorySectionCard(
                    title = "Prior Investigations & Labs",
                    icon = Icons.Default.Science,
                    iconBg = PastelLavender,
                    iconTint = PastelLavenderIcon,
                    content = if (clinicalSummary.priorInvestigations.isNotBlank()) clinicalSummary.priorInvestigations else "No previous lab investigations recorded.",
                    onEdit = {
                        editingSectionTitle = "Prior Investigations"
                        editingSectionContent = clinicalSummary.priorInvestigations
                    },
                    testTag = "doctor_section_prior_investigations"
                )

                // Section 5: Triage Priority & Attending Physician Handoff (with LocalHospital Icon)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doctor_section_triage"),
                    shape = RoundedCornerShape(22.dp),
                    color = HealthCardBg,
                    border = BorderStroke(1.dp, HealthCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(HealthBlueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = HealthNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "TRIAGE CLASSIFICATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthTextSecondary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = clinicalSummary.triageLevel.ifBlank { "Priority 3: Standard Outpatient" },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavy
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = HealthNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Dual Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, HealthCardBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Secondary Button: Re-scan / Regenerate
                    Button(
                        onClick = onRegenerate,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("regenerate_summary_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthBlueSoft,
                            contentColor = HealthNavy
                        ),
                        border = BorderStroke(1.dp, HealthCardBorder)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RE-GENERATE",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    // Primary Button: Confirm & Submit
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp)
                            .testTag("submit_summary_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CONFIRM & TRANSMIT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Share / Export PDF Card Modal
    if (showShareModal) {
        AlertDialog(
            onDismissRequest = { showShareModal = false },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Clinical Summary - Patient #${patientInfo.tokenNumber}")
                            putExtra(Intent.EXTRA_TEXT, formattedShareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Clinical Summary"))
                        showShareModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Send")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Clinical Summary", formattedShareText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Clinical Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                        showShareModal = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Copy Text")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = HealthNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Clinical Summary", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthBackground)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = formattedShareText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = HealthNavy
                        )
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(22.dp)
        )
    }

    // Dialog for Editing Sections
    editingSectionTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { editingSectionTitle = null },
            title = { Text("Edit $title", fontWeight = FontWeight.Bold, color = HealthNavyHeader) },
            text = {
                OutlinedTextField(
                    value = editingSectionContent,
                    onValueChange = { editingSectionContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = kioskTextFieldColors(),
                    textStyle = kioskInputTextStyle(),
                    maxLines = 8
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSection(title, editingSectionContent)
                        editingSectionTitle = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSectionTitle = null }) { Text("Cancel") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun HistorySectionCard(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    content: String,
    onEdit: () -> Unit,
    testTag: String = ""
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        color = HealthCardBg,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $title",
                        tint = HealthNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = HealthTextPrimary,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Composable
private fun HistoryColumnCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    content: String,
    subNote: String? = null,
    onEdit: () -> Unit,
    testTag: String = ""
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onEdit)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        color = HealthCardBg,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $title",
                        tint = HealthNavy,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = HealthTextPrimary,
                    lineHeight = 18.sp
                ),
                maxLines = 4
            )

            if (subNote != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subNote,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HealthTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
