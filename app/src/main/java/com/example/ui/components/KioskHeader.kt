package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary

@Composable
fun KioskHeader(
    title: String,
    subtitle: String? = null,
    currentStep: Int? = null,
    totalSteps: Int = 4,
    stepIndicator: String? = null,
    tokenNumber: String? = null,
    currentLanguage: Language? = null,
    onLanguageClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kiosk_header"),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Top Bar: Hospital Identity + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Optional Back Button + Hospital Logo & Brand
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(HealthBackground)
                                .testTag("header_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = HealthNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HealthNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "MediKiosk Hospital Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MediKiosk",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                letterSpacing = (-0.3).sp,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = if (tokenNumber != null) "Queue ID: #MK-$tokenNumber" else "City Care Outpatient Intake",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthTextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Right Badges & Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tokenNumber != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HealthBlueSoft
                        ) {
                            Text(
                                text = "#MK-$tokenNumber",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthNavy
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    if (currentLanguage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HealthBackground,
                            modifier = Modifier.clickable(enabled = onLanguageClick != null) {
                                onLanguageClick?.invoke()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language Selector",
                                    tint = HealthNavy,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentLanguage.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = HealthNavy
                                    )
                                )
                            }
                        }
                    }

                    if (onSettingsClick != null) {
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HealthBackground)
                                .testTag("header_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Accessibility Settings",
                                tint = HealthNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Step Progress Indicator Row (Step X of Y)
            val effectiveStep = currentStep ?: when (stepIndicator) {
                "Step 1 of 4" -> 1
                "Step 2 of 4" -> 2
                "Step 3 of 4" -> 3
                "Step 4 of 4" -> 4
                else -> null
            }

            if (effectiveStep != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val progressValue by animateFloatAsState(
                    targetValue = effectiveStep.toFloat() / totalSteps.toFloat(),
                    label = "step_progress"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("step_progress_indicator")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthNavy
                            ) {
                                Text(
                                    text = "Step $effectiveStep of $totalSteps",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            if (title.isNotBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthNavyHeader
                                    )
                                )
                            }
                        }

                        Text(
                            text = "${(progressValue * 100).toInt()}% Completed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HealthTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HealthBlueAccent,
                        trackColor = HealthBlueSoft,
                        strokeCap = StrokeCap.Round
                    )
                }
            } else if (title.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader,
                            fontSize = 20.sp
                        )
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = HealthTextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}
