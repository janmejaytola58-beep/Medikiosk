package com.example.ui.components

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RedFlagAlert
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.BentoSecondaryButton
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthDivider
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthTextMuted
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.KioskBorder
import com.example.ui.theme.RedFlagBg
import com.example.ui.theme.RedFlagBorder
import com.example.ui.theme.RedFlagEmergency
import com.example.ui.theme.RedFlagText
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight

@Composable
fun LargeKioskButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    testTag: String = "kiosk_action_button"
) {
    if (isSecondary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .testTag(testTag),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BentoSecondaryButton,
                contentColor = BentoTextPrimary,
                disabledContainerColor = BentoSecondaryButton.copy(alpha = 0.5f),
                disabledContentColor = BentoTextSecondary.copy(alpha = 0.5f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) BentoTextPrimary else BentoTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) BentoTextPrimary else BentoTextSecondary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .testTag(testTag),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HealthNavy,
                contentColor = Color.White,
                disabledContainerColor = BentoCardBorder,
                disabledContentColor = BentoTextSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AudioTtsButton(
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Read Aloud",
    testTag: String = "tts_speaker_button"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tts_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSpeaking) AlertOrange else HealthBlueSoft,
        label = "bg_color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSpeaking) Color.White else HealthNavy,
        label = "content_color"
    )

    Surface(
        modifier = modifier
            .scale(if (isSpeaking) pulseScale else 1.0f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSpeakClick)
            .testTag(testTag),
        color = bgColor,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isSpeaking) "Speaking..." else label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            )
        }
    }
}

@Composable
fun VoiceInputButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "voice_input_button"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_alpha"
    )

    val buttonColor = if (isListening) AlertOrange else HealthNavy

    Box(
        modifier = modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle outer pulsing ripple animation when active
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(ringScale)
                    .alpha(ringAlpha)
                    .clip(CircleShape)
                    .background(AlertOrange)
            )
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(if (isListening) pulseScale else 1f)
                .clip(CircleShape)
                .background(buttonColor)
                .clickable(onClick = onClick)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Speak into microphone",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun kioskShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF8FAFC),
        Color(0xFFE2E8F0)
    )
    val transition = rememberInfiniteTransition(label = "kiosk_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(kioskShimmerBrush())
    )
}

@Composable
fun SkeletonCardPlaceholder(
    modifier: Modifier = Modifier,
    lines: Int = 3
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonBox(modifier = Modifier.size(36.dp), shape = CircleShape)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBox(modifier = Modifier.width(140.dp).height(14.dp))
                    SkeletonBox(modifier = Modifier.width(90.dp).height(10.dp))
                }
            }
            repeat(lines) { i ->
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(if (i == lines - 1) 0.6f else 0.95f)
                        .height(12.dp)
                )
            }
        }
    }
}

@Composable
fun CoachMarkTooltip(
    stepNumber: String,
    title: String,
    description: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "coach_mark_tooltip"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.5.dp, HealthNavy.copy(alpha = 0.4f)), RoundedCornerShape(16.dp))
            .testTag(testTag),
        color = HealthBlueSoft,
        shadowElevation = 4.dp
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
                Surface(
                    shape = CircleShape,
                    color = HealthNavy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumber,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss guide",
                    tint = HealthNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun kioskTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = Color.Black,
    focusedBorderColor = HealthNavy,
    unfocusedBorderColor = HealthCardBorder,
    focusedPlaceholderColor = Color(0xFF6B7280),
    unfocusedPlaceholderColor = Color(0xFF6B7280),
    focusedLabelColor = HealthNavy,
    unfocusedLabelColor = Color(0xFF475569)
)

@Composable
fun kioskInputTextStyle(): TextStyle = MaterialTheme.typography.bodyLarge.copy(
    color = Color.Black,
    fontWeight = FontWeight.Medium
)

@Composable
fun KioskSectionCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = HealthCardBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp)),
        color = backgroundColor,
        shadowElevation = 1.5.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// Polished Urgent Red-Flag Alert Banner
@Composable
fun UrgentRedAlertBanner(
    alert: RedFlagAlert,
    onAlertStaffClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(BorderStroke(1.5.dp, RedFlagBorder), RoundedCornerShape(20.dp))
            .testTag("urgent_red_flag_alert_banner"),
        color = RedFlagBg,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RedFlagEmergency),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Urgent Red Flag Alert",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedFlagEmergency,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "PRIORITY RED FLAG • IMMEDIATE STAFF EVALUATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedFlagText,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                if (onDismiss != null) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = RedFlagText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RedFlagText,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAlertStaffClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("alert_triage_staff_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedFlagEmergency,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Alert Triage Nurse / ER Staff Now",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// Polished List Screen Row (white row, small icon left, label text, chevron right, thin divider)
@Composable
fun KioskListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = HealthNavy,
    iconBgColor: Color = HealthBlueSoft,
    trailingBadge: String? = null,
    trailingBadgeColor: Color = HealthBlueAccent,
    showChevron: Boolean = true,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    testTag: String = "kiosk_list_row"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = HealthTextPrimary
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trailingBadge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = trailingBadgeColor.copy(alpha = 0.12f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = trailingBadge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = trailingBadgeColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (showChevron) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Navigate",
                        tint = HealthTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = HealthDivider,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Accessibility Settings Dialog (High contrast & Large font toggles)
@Composable
fun AccessibilitySettingsDialog(
    isHighContrast: Boolean,
    isLargeFont: Boolean,
    onToggleHighContrast: (Boolean) -> Unit,
    onToggleLargeFont: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(HealthBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = HealthNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Accessibility Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavy
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Customize display settings for comfortable reading in the hospital waiting area.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HealthTextSecondary)
                )

                // High Contrast Toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = HealthBackground,
                    border = BorderStroke(1.dp, HealthCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contrast,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "High Contrast Mode",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthTextPrimary
                                    )
                                )
                                Text(
                                    text = "Enhances borders, cards & dark text",
                                    style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                )
                            }
                        }
                        Switch(
                            checked = isHighContrast,
                            onCheckedChange = onToggleHighContrast,
                            modifier = Modifier.testTag("toggle_high_contrast_switch")
                        )
                    }
                }

                // Larger Font Toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = HealthBackground,
                    border = BorderStroke(1.dp, HealthCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = HealthNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Larger Text Size",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HealthTextPrimary
                                    )
                                )
                                Text(
                                    text = "Senior-friendly large typography",
                                    style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                                )
                            }
                        }
                        Switch(
                            checked = isLargeFont,
                            onCheckedChange = onToggleLargeFont,
                            modifier = Modifier.testTag("toggle_large_font_switch")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

