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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.Language
import com.example.data.model.MessageSender
import com.example.data.model.PatientInfo
import com.example.data.model.RedFlagAlert
import com.example.data.model.SocratesData
import com.example.ui.components.KioskHeader
import com.example.ui.components.UrgentRedAlertBanner
import com.example.ui.components.VoiceInputButton
import com.example.ui.components.kioskInputTextStyle
import com.example.ui.components.kioskTextFieldColors
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoPrimaryBlue
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.KioskBackgroundLight
import com.example.ui.theme.KioskBorder
import com.example.ui.theme.SageSecondary
import com.example.ui.theme.SageSecondaryLight
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConversationalIntakeScreen(
    patientInfo: PatientInfo,
    selectedLanguage: Language,
    messages: List<ChatMessage>,
    quickReplies: List<String>,
    socratesData: SocratesData,
    isAiThinking: Boolean,
    isListeningSpeech: Boolean,
    redFlagAlert: RedFlagAlert? = null,
    onSendMessage: (String) -> Unit,
    onVoiceInputClick: () -> Unit,
    onSpeakMessage: (String) -> Unit,
    onProceedToDocs: () -> Unit,
    onAlertStaffClick: () -> Unit = {},
    onDismissRedFlag: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("conversational_intake_screen")
    ) {
        KioskHeader(
            title = "Clinical History Intake",
            subtitle = "Step 2 of 4 • AI Doctor SOCRATES",
            currentStep = 2,
            totalSteps = 4,
            stepIndicator = "Step 2 of 4",
            tokenNumber = patientInfo.tokenNumber,
            currentLanguage = selectedLanguage,
            onBackClick = onBackClick
        )

        // Urgent Red Flag Emergency Alert Banner (e.g. chest pain + breathlessness or stroke)
        if (redFlagAlert != null) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                UrgentRedAlertBanner(
                    alert = redFlagAlert,
                    onAlertStaffClick = onAlertStaffClick,
                    onDismiss = onDismissRedFlag
                )
            }
        }

        // SOCRATES clinical badge progress tracker
        SocratesProgressTracker(socratesData = socratesData)

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeak = { onSpeakMessage(msg.text) }
                )
            }

            if (isAiThinking) {
                item {
                    AiThinkingBubble()
                }
            }
        }

        // Quick Reply Buttons (2-4 tappable buttons)
        AnimatedVisibility(
            visible = quickReplies.isNotEmpty() && !isAiThinking,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Suggested Quick Responses:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickReplies.forEach { reply ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(
                                        BorderStroke(1.5.dp, TealPrimary),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onSendMessage(reply) }
                                    .testTag("quick_reply_chip"),
                                color = TealPrimaryLight.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = reply,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TealPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Bar (Voice mic, text field, send button)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Voice speech-to-text mic
                    VoiceInputButton(
                        isListening = isListeningSpeech,
                        onClick = onVoiceInputClick,
                        testTag = "voice_mic_button"
                    )

                    // Text input field
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                if (isListeningSpeech) "Listening to voice..." else "Type or use mic...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280))
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = kioskTextFieldColors(),
                        textStyle = kioskInputTextStyle(),
                        singleLine = true
                    )

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (textInput.isNotBlank()) HealthNavy else HealthCardBorder)
                            .clickable(enabled = textInput.isNotBlank() && !isAiThinking) {
                                onSendMessage(textInput)
                                textInput = ""
                            }
                            .testTag("send_message_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Transition button to Document Scan
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HealthBlueSoft)
                        .clickable(onClick = onProceedToDocs)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("proceed_to_docs_button"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Have medical reports or prescriptions? Scan them now",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = HealthNavy
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = HealthNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SocratesProgressTracker(socratesData: SocratesData) {
    val items = listOf(
        "Site" to socratesData.site.isNotBlank(),
        "Onset" to socratesData.onset.isNotBlank(),
        "Character" to socratesData.character.isNotBlank(),
        "Radiation" to socratesData.radiation.isNotBlank(),
        "Associated" to socratesData.associations.isNotBlank(),
        "Timing" to socratesData.timing.isNotBlank(),
        "Factors" to socratesData.exacerbatingRelieving.isNotBlank(),
        "Severity" to socratesData.severity.isNotBlank()
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TealPrimaryLight.copy(alpha = 0.3f),
        tonalElevation = 1.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(items) { (name, done) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (done) SageSecondary else Color.White,
                    border = BorderStroke(1.dp, if (done) SageSecondary else KioskBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (done) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (done) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (done) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onSpeak: () -> Unit
) {
    val isAi = message.sender == MessageSender.AI
    val alignment = if (isAi) Alignment.Start else Alignment.End

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_bubble_${message.sender.name.lowercase()}"),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (isAi) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(HealthNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "AI Doctor",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isAi) 4.dp else 20.dp,
                    bottomEnd = if (isAi) 20.dp else 4.dp
                ),
                color = if (isAi) HealthCardBg else HealthNavy,
                border = if (isAi) BorderStroke(1.dp, HealthCardBorder) else null,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isAi) HealthTextPrimary else Color.White,
                            lineHeight = 22.sp,
                            fontWeight = if (isAi) FontWeight.Normal else FontWeight.Medium
                        )
                    )

                    if (isAi) {
                        Spacer(modifier = Modifier.height(10.dp))
                        // TTS read-aloud button on every question
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HealthBlueSoft,
                            modifier = Modifier
                                .clickable(onClick = onSpeak)
                                .testTag("tts_read_aloud_${message.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Read aloud question",
                                    tint = HealthNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Read aloud",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (!isAi) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(HealthBlueAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Patient",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiThinkingBubble() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TealPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, KioskBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AI Nurse is analyzing symptoms...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
