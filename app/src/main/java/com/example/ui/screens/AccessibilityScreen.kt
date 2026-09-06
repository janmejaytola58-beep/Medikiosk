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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.components.KioskHeader
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
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
fun AccessibilityScreen(
    currentTextScale: Float,
    isHighContrast: Boolean,
    isVoiceGuidanceEnabled: Boolean,
    isReduceMotionEnabled: Boolean,
    selectedLanguage: Language,
    onTextScaleChanged: (Float) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onVoiceGuidanceChanged: (Boolean) -> Unit,
    onReduceMotionChanged: (Boolean) -> Unit,
    onLanguageSelected: (Language) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("accessibility_screen")
    ) {
        KioskHeader(
            title = "Accessibility Center",
            subtitle = "Visual, Audio & Interaction Preferences",
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
            // Text Size Section
            AccessibilityCard(
                title = "Text Display Size",
                subtitle = "Adjust readable font scale across all kiosk screens",
                icon = Icons.Default.FormatSize,
                iconBg = PastelLightBlue,
                iconTint = PastelLightBlueIcon
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val scales = listOf(
                        Triple("Normal", 1.0f, "14sp base"),
                        Triple("Large", 1.18f, "18sp base"),
                        Triple("Extra Large", 1.35f, "22sp base")
                    )

                    scales.forEach { (label, scale, note) ->
                        val isSelected = (currentTextScale - scale).let { it > -0.05f && it < 0.05f }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onTextScaleChanged(scale) }
                                .testTag("text_scale_$label"),
                            color = if (isSelected) HealthBlueSoft else HealthBackground,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) HealthNavy else HealthCardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Aa",
                                    fontSize = when (label) {
                                        "Extra Large" -> 22.sp
                                        "Large" -> 18.sp
                                        else -> 14.sp
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) HealthNavy else HealthTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) HealthNavy else HealthTextPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthTextSecondary,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // High Contrast Mode
            AccessibilityToggleCard(
                title = "High Contrast Mode",
                description = "Enhances border definition and text contrast for low vision",
                icon = Icons.Default.Contrast,
                iconBg = PastelPeach,
                iconTint = PastelPeachIcon,
                checked = isHighContrast,
                onCheckedChange = onHighContrastChanged,
                testTag = "high_contrast_switch"
            )

            // Voice Guidance / Auto Speech
            AccessibilityToggleCard(
                title = "Voice Guidance (Auto TTS)",
                description = "Reads out questions and prompts automatically as you navigate",
                icon = Icons.Default.VolumeUp,
                iconBg = PastelMint,
                iconTint = PastelMintIcon,
                checked = isVoiceGuidanceEnabled,
                onCheckedChange = onVoiceGuidanceChanged,
                testTag = "voice_guidance_switch"
            )

            // Reduce Motion
            AccessibilityToggleCard(
                title = "Reduce Motion",
                description = "Minimizes animations and sliding effects for vestibular comfort",
                icon = Icons.Default.Animation,
                iconBg = PastelLavender,
                iconTint = PastelLavenderIcon,
                checked = isReduceMotionEnabled,
                onCheckedChange = onReduceMotionChanged,
                testTag = "reduce_motion_switch"
            )

            // Language Selector Section
            AccessibilityCard(
                title = "Spoken & Display Language",
                subtitle = "Select regional language with native speech output",
                icon = Icons.Default.Translate,
                iconBg = HealthBlueSoft,
                iconTint = HealthNavy
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Language.SUPPORTED_LANGUAGES.chunked(2).forEach { rowLangs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowLangs.forEach { lang ->
                                val isSelected = lang.code == selectedLanguage.code
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onLanguageSelected(lang) }
                                        .testTag("access_lang_${lang.code}"),
                                    color = if (isSelected) HealthBlueSoft else HealthBackground,
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) HealthNavy else HealthCardBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = lang.nativeName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) HealthNavy else HealthTextPrimary,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            Text(
                                                text = lang.name,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = HealthTextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                        if (isSelected) {
                                            Text("✓", color = HealthNavy, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_accessibility_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HealthNavy)
            ) {
                Text(
                    text = "Apply & Return to Kiosk",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun AccessibilityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
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
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 15.sp
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
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun AccessibilityToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
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
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = HealthTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = HealthNavy,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = HealthBackground
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
