package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.PatientInfo
import com.example.ui.components.AudioTtsButton
import com.example.ui.components.KioskHeader
import com.example.ui.components.KioskSectionCard
import com.example.ui.components.LargeKioskButton
import com.example.ui.components.kioskInputTextStyle
import com.example.ui.components.kioskTextFieldColors
import com.example.ui.theme.KioskBackgroundLight
import com.example.ui.theme.KioskBorder
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SageSecondaryLight
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight

@Composable
fun PatientIdentifyScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    isTtsSpeaking: Boolean,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAbhaChange: (String) -> Unit,
    onNewPatientToggle: (Boolean) -> Unit,
    onConsentToggle: (Boolean) -> Unit,
    onReadConsentAloud: () -> Unit,
    onPrefillSample: () -> Unit,
    onProceed: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("patient_identify_screen")
    ) {
        KioskHeader(
            title = "Patient Identification",
            subtitle = "Step 1 of 4 • Basic Demographics",
            currentStep = 1,
            totalSteps = 4,
            stepIndicator = "Step 1 of 4",
            tokenNumber = patientInfo.tokenNumber,
            currentLanguage = selectedLanguage,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick testing autofill button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Please enter patient details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                TextButton(
                    onClick = onPrefillSample,
                    modifier = Modifier.testTag("prefill_sample_button")
                ) {
                    Text(
                        text = "Prefill Sample",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    )
                }
            }

            // Name Field
            KioskSectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Full Name / पूरा नाम *",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = patientInfo.name,
                    onValueChange = onNameChange,
                    placeholder = { Text("Enter patient full name", color = Color(0xFF6B7280)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = kioskTextFieldColors(),
                    textStyle = kioskInputTextStyle(),
                    singleLine = true
                )
            }

            // Age & Gender Row
            KioskSectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Age Field
                    Column(modifier = Modifier.weight(0.42f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Age / उम्र *",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = patientInfo.age,
                            onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) onAgeChange(it) },
                            placeholder = { Text("e.g. 54", color = Color(0xFF6B7280)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("patient_age_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            colors = kioskTextFieldColors(),
                            textStyle = kioskInputTextStyle(),
                            singleLine = true
                        )
                    }

                    // Gender Field
                    Column(modifier = Modifier.weight(0.58f)) {
                        Text(
                            text = "Gender / लिंग *",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GenderChip(
                                label = "M",
                                icon = Icons.Default.Male,
                                isSelected = patientInfo.gender.equals("Male", true),
                                onSelect = { onGenderChange("Male") },
                                modifier = Modifier.weight(1f)
                            )
                            GenderChip(
                                label = "F",
                                icon = Icons.Default.Female,
                                isSelected = patientInfo.gender.equals("Female", true),
                                onSelect = { onGenderChange("Female") },
                                modifier = Modifier.weight(1f)
                            )
                            GenderChip(
                                label = "O",
                                icon = Icons.Default.Transgender,
                                isSelected = patientInfo.gender.equals("Other", true),
                                onSelect = { onGenderChange("Other") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ABHA ID & New Patient Toggle Card
            KioskSectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "New Patient Registration",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Toggle ON if patient has no ABHA ID card",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Switch(
                        checked = patientInfo.isNewPatient,
                        onCheckedChange = onNewPatientToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SageSecondary
                        ),
                        modifier = Modifier.testTag("new_patient_toggle")
                    )
                }

                AnimatedVisibility(visible = !patientInfo.isNewPatient) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ABHA ID (Ayushman Bharat Health Account)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = patientInfo.abhaId,
                            onValueChange = onAbhaChange,
                            placeholder = { Text("e.g. 91-4829-1029-4821", color = Color(0xFF6B7280)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("abha_id_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = kioskTextFieldColors(),
                            textStyle = kioskInputTextStyle(),
                            singleLine = true
                        )
                    }
                }
            }

            // Consent Card with TTS read aloud
            KioskSectionCard(
                backgroundColor = SageSecondaryLight.copy(alpha = 0.35f),
                borderColor = SageSecondary.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Patient Informed Consent",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageSecondary
                        )
                    )

                    // Audio Read Aloud Button
                    AudioTtsButton(
                        isSpeaking = isTtsSpeaking,
                        onSpeakClick = onReadConsentAloud,
                        label = "Read Aloud",
                        testTag = "read_consent_tts_button"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConsentToggle(!patientInfo.hasConsented) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = patientInfo.hasConsented,
                        onCheckedChange = onConsentToggle,
                        colors = CheckboxDefaults.colors(checkedColor = SageSecondary),
                        modifier = Modifier.testTag("consent_checkbox")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = selectedLanguage.consentExplanation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Bottom Proceed Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                LargeKioskButton(
                    text = "Proceed to Clinical Intake",
                    icon = Icons.Default.ArrowForward,
                    onClick = onProceed,
                    enabled = patientInfo.isValid,
                    testTag = "proceed_to_intake_button"
                )
            }
        }
    }
}

@Composable
fun GenderChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) TealPrimary else KioskBorder
    val bgColor = if (isSelected) TealPrimaryLight else Color.White
    val contentColor = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .testTag("gender_chip_$label"),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            )
        }
    }
}
