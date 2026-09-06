package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Science
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExtractedDocumentData
import com.example.data.model.LabValueItem
import com.example.data.model.Language
import com.example.data.model.MedicationItem
import com.example.data.model.PatientInfo
import com.example.ui.components.KioskHeader
import com.example.ui.components.LargeKioskButton
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentScanScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    scannedBitmap: Bitmap?,
    isScanning: Boolean,
    extractedData: ExtractedDocumentData,
    onImageSelected: (Bitmap) -> Unit,
    onLoadSample: () -> Unit,
    onAddDiagnosis: (String) -> Unit,
    onRemoveDiagnosis: (String) -> Unit,
    onUpdateMedication: (MedicationItem) -> Unit,
    onRemoveMedication: (String) -> Unit,
    onUpdateLab: (LabValueItem) -> Unit,
    onRemoveLab: (String) -> Unit,
    onProceedToSummary: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showAddDiagnosisDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<MedicationItem?>(null) }
    var editingLab by remember { mutableStateOf<LabValueItem?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onImageSelected(bitmap)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                stream?.close()
                if (bitmap != null) {
                    onImageSelected(bitmap)
                }
            } catch (e: Exception) {
                onLoadSample()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("document_scan_screen")
    ) {
        KioskHeader(
            title = "Document & Prescription Scan",
            subtitle = "Step 3 of 4 • Gemini Vision OCR",
            currentStep = 3,
            totalSteps = 4,
            stepIndicator = "Step 3 of 4",
            tokenNumber = patientInfo.tokenNumber,
            currentLanguage = selectedLanguage,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upload & Camera Card (Modern White Elevated Card)
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HealthBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Upload or Photograph Records",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                            Text(
                                text = "Prescriptions, discharge slips, and laboratory tests",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Camera Button
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("camera_scan_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, HealthNavy)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Camera",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy
                                )
                            )
                        }

                        // Gallery Button
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("gallery_picker_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, HealthNavy)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gallery",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Sample Button
                    Button(
                        onClick = onLoadSample,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("load_sample_doc_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSoft)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = HealthNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Load Sample Hospital Prescription",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            )
                        )
                    }

                    // Scanned Preview Thumbnail
                    if (scannedBitmap != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(HealthBlueSoft.copy(alpha = 0.6f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = scannedBitmap.asImageBitmap(),
                                contentDescription = "Scanned prescription thumbnail",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Record Attached Successfully",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavy
                                    )
                                )
                                Text(
                                    text = "Analyzed with Gemini Vision OCR",
                                    style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                )
                            }
                        }
                    }

                    // Shimmer Skeleton during OCR extraction
                    if (isScanning) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SkeletonBox(modifier = Modifier.size(16.dp), shape = CircleShape)
                                Text(
                                    text = "Gemini Vision is parsing medications & lab tests...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavy
                                    )
                                )
                            }
                            SkeletonBox(modifier = Modifier.fillMaxWidth().height(14.dp))
                            SkeletonBox(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
                        }
                    }
                }
            }

            // Section 1: Extracted Diagnoses
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelLightBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("1", fontWeight = FontWeight.Bold, color = PastelLightBlueIcon)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Clinical Diagnoses",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavyHeader
                                )
                            )
                        }

                        Button(
                            onClick = { showAddDiagnosisDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSoft),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_diagnosis_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", color = HealthNavy, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (extractedData.diagnoses.isEmpty()) {
                        Text(
                            text = "No prior diagnoses recorded yet. Tap Camera/Sample above or click Add.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextMuted)
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            extractedData.diagnoses.forEach { diag ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = HealthBlueSoft,
                                    border = BorderStroke(1.dp, HealthNavy.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = diag,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HealthNavy
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onRemoveDiagnosis(diag) },
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove $diag",
                                                tint = HealthNavy,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Card-based Medications (One Card per Medication)
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelPeach),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("2", fontWeight = FontWeight.Bold, color = PastelPeachIcon)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Prior Medications (${extractedData.medications.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                Text(
                                    text = "Card layout • Tap to edit dose or schedule",
                                    style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                editingMedication = MedicationItem(name = "", dosage = "", frequency = "")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSoft),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_medication_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Med", color = HealthNavy, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (extractedData.medications.isEmpty()) {
                        Text(
                            text = "No medications extracted yet. Tap Load Sample or Camera above.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextMuted)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            extractedData.medications.forEach { med ->
                                MedicationItemCard(
                                    medication = med,
                                    onEdit = { editingMedication = med },
                                    onDelete = { onRemoveMedication(med.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Card-based Lab Values (One Card per Lab Entry)
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelLavender),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("3", fontWeight = FontWeight.Bold, color = PastelLavenderIcon)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Diagnostic Lab Values (${extractedData.labValues.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                                Text(
                                    text = "Card layout with reference ranges & status",
                                    style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                editingLab = LabValueItem(testName = "", value = "", unit = "", referenceRange = "")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSoft),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_lab_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = HealthNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Lab", color = HealthNavy, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (extractedData.labValues.isEmpty()) {
                        Text(
                            text = "No laboratory values recorded yet. Add manual test results or upload lab slips.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextMuted)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            extractedData.labValues.forEach { lab ->
                                LabValueItemCard(
                                    lab = lab,
                                    onEdit = { editingLab = lab },
                                    onDelete = { onRemoveLab(lab.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Bottom Bar Action
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            border = BorderStroke(1.dp, HealthCardBorderSubtle),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                LargeKioskButton(
                    text = "Generate Doctor Clinical Summary",
                    icon = Icons.Default.ArrowForward,
                    onClick = onProceedToSummary,
                    testTag = "generate_summary_button"
                )
            }
        }
    }

    // Add Diagnosis Dialog
    if (showAddDiagnosisDialog) {
        var newDiag by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDiagnosisDialog = false },
            title = { Text("Add Clinical Diagnosis", fontWeight = FontWeight.Bold, color = HealthNavyHeader) },
            text = {
                OutlinedTextField(
                    value = newDiag,
                    onValueChange = { newDiag = it },
                    placeholder = { Text("e.g. Type 2 Diabetes, Hypertension", color = Color(0xFF6B7280)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = kioskTextFieldColors(),
                    textStyle = kioskInputTextStyle()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDiag.isNotBlank()) onAddDiagnosis(newDiag)
                        showAddDiagnosisDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDiagnosisDialog = false }) { Text("Cancel") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Edit/Add Medication Dialog
    editingMedication?.let { med ->
        var name by remember { mutableStateOf(med.name) }
        var dosage by remember { mutableStateOf(med.dosage) }
        var freq by remember { mutableStateOf(med.frequency) }

        AlertDialog(
            onDismissRequest = { editingMedication = null },
            title = { Text("Edit Medication Entry", fontWeight = FontWeight.Bold, color = HealthNavyHeader) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Drug Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage (e.g. 500 mg)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                    OutlinedTextField(
                        value = freq,
                        onValueChange = { freq = it },
                        label = { Text("Frequency & Route (e.g. Twice daily • Oral)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                }
            },
            confirmButton = {
                Row {
                    if (med.name.isNotBlank()) {
                        TextButton(
                            onClick = {
                                onRemoveMedication(med.id)
                                editingMedication = null
                            }
                        ) {
                            Text("Delete", color = AlertRed)
                        }
                    }
                    Button(
                        onClick = {
                            onUpdateMedication(med.copy(name = name, dosage = dosage, frequency = freq))
                            editingMedication = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthNavy)
                    ) {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMedication = null }) { Text("Cancel") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Edit/Add Lab Test Dialog
    editingLab?.let { lab ->
        var testName by remember { mutableStateOf(lab.testName) }
        var value by remember { mutableStateOf(lab.value) }
        var unit by remember { mutableStateOf(lab.unit) }
        var refRange by remember { mutableStateOf(lab.referenceRange) }
        var status by remember { mutableStateOf(lab.status) }

        AlertDialog(
            onDismissRequest = { editingLab = null },
            title = { Text("Edit Lab Investigation", fontWeight = FontWeight.Bold, color = HealthNavyHeader) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Test Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it },
                            label = { Text("Result") },
                            modifier = Modifier.weight(1f),
                            colors = kioskTextFieldColors(),
                            textStyle = kioskInputTextStyle()
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            colors = kioskTextFieldColors(),
                            textStyle = kioskInputTextStyle()
                        )
                    }
                    OutlinedTextField(
                        value = refRange,
                        onValueChange = { refRange = it },
                        label = { Text("Reference Range") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (Normal, High, Low)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle()
                    )
                }
            },
            confirmButton = {
                Row {
                    if (lab.testName.isNotBlank()) {
                        TextButton(
                            onClick = {
                                onRemoveLab(lab.id)
                                editingLab = null
                            }
                        ) {
                            Text("Delete", color = AlertRed)
                        }
                    }
                    Button(
                        onClick = {
                            onUpdateLab(lab.copy(testName = testName, value = value, unit = unit, referenceRange = refRange, status = status))
                            editingLab = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthNavy)
                    ) {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { editingLab = null }) { Text("Cancel") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// Card-based layout: One card per medication entry
@Composable
private fun MedicationItemCard(
    medication: MedicationItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, HealthCardBorderSubtle), RoundedCornerShape(18.dp))
            .clickable(onClick = onEdit)
            .testTag("medication_card_${medication.name.lowercase().replace(" ", "_")}"),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelPeach),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = "Medication",
                        tint = PastelPeachIcon,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (medication.dosage.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueSoft
                            ) {
                                Text(
                                    text = medication.dosage,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavy
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (medication.frequency.isNotBlank()) {
                            Text(
                                text = medication.frequency,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HealthTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${medication.name}",
                        tint = HealthNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ${medication.name}",
                        tint = AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Card-based layout: One card per lab entry
@Composable
private fun LabValueItemCard(
    lab: LabValueItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isAbnormal = lab.status.equals("High", ignoreCase = true) ||
            lab.status.equals("Critical", ignoreCase = true) ||
            lab.status.equals("Low", ignoreCase = true)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                BorderStroke(
                    if (isAbnormal) 1.5.dp else 1.dp,
                    if (isAbnormal) AlertOrange.copy(alpha = 0.5f) else HealthCardBorderSubtle
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onEdit)
            .testTag("lab_card_${lab.testName.lowercase().replace(" ", "_")}"),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelLavender),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Lab investigation",
                        tint = PastelLavenderIcon,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = lab.testName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${lab.value} ${lab.unit}".trim(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAbnormal) AlertOrange else HealthNavy
                            )
                        )

                        if (lab.referenceRange.isNotBlank()) {
                            Text(
                                text = "• Ref: ${lab.referenceRange}",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAbnormal) AlertOrange.copy(alpha = 0.15f) else PastelMint
                ) {
                    Text(
                        text = if (lab.status.isNotBlank()) lab.status else "Normal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isAbnormal) AlertOrange else PastelMintIcon
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${lab.testName}",
                        tint = HealthNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
