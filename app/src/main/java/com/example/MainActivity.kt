package com.example

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HealthBottomBar
import com.example.ui.components.HealthBottomTab
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.StaffAssistanceDialog
import com.example.ui.screens.AccessibilityScreen
import com.example.ui.screens.ConfirmationScreen
import com.example.ui.screens.ConversationalIntakeScreen
import com.example.ui.screens.DocumentScanScreen
import com.example.ui.screens.MyRecordsScreen
import com.example.ui.screens.PatientIdentifyScreen
import com.example.ui.screens.PrivacyCenterScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QueueTrackerScreen
import com.example.ui.screens.StaffViewScreen
import com.example.ui.screens.SummaryScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.KioskScreen
import com.example.ui.viewmodel.KioskViewModel
import com.example.util.TtsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MediKioskApp()
            }
        }
    }
}

@Composable
fun MediKioskApp(
    viewModel: KioskViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsManager = remember { TtsManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val patientInfo by viewModel.patientInfo.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val quickReplies by viewModel.currentQuickReplies.collectAsStateWithLifecycle()
    val socratesData by viewModel.socratesData.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val scannedBitmap by viewModel.scannedBitmap.collectAsStateWithLifecycle()
    val isScanningDocument by viewModel.isScanningDocument.collectAsStateWithLifecycle()
    val extractedData by viewModel.extractedData.collectAsStateWithLifecycle()
    val clinicalSummary by viewModel.clinicalSummary.collectAsStateWithLifecycle()
    val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsStateWithLifecycle()
    val isTtsSpeaking by ttsManager.isSpeaking.collectAsStateWithLifecycle()

    val redFlagAlert by viewModel.redFlagAlert.collectAsStateWithLifecycle()
    val isHighContrast by viewModel.isHighContrast.collectAsStateWithLifecycle()
    val isLargeFont by viewModel.isLargeFont.collectAsStateWithLifecycle()
    val textScale by viewModel.textScale.collectAsStateWithLifecycle()
    val isVoiceGuidanceEnabled by viewModel.isVoiceGuidanceEnabled.collectAsStateWithLifecycle()
    val isReduceMotionEnabled by viewModel.isReduceMotionEnabled.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val hasActiveVisit by viewModel.hasActiveVisit.collectAsStateWithLifecycle()
    val activeVisitStatus by viewModel.activeVisitStatus.collectAsStateWithLifecycle()
    val queuePosition by viewModel.queuePosition.collectAsStateWithLifecycle()
    val hasUnfinishedSession by viewModel.hasUnfinishedSession.collectAsStateWithLifecycle()
    val showInactivityDialog by viewModel.showInactivityDialog.collectAsStateWithLifecycle()

    // Firestore & Room streams
    val patientsQueue by viewModel.patientsQueue.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val myLocalRecords by viewModel.myLocalRecords.collectAsStateWithLifecycle()
    val assistanceRequests by viewModel.assistanceRequests.collectAsStateWithLifecycle()
    val staffRole by viewModel.staffRole.collectAsStateWithLifecycle()
    val selectedDoctor by viewModel.selectedDoctor.collectAsStateWithLifecycle()

    var isListeningSpeech by remember { mutableStateOf(false) }
    var showStaffAssistanceDialog by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }

    // Speech-to-Text Recognition Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListeningSpeech = false
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTextList?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendUserMessage(spokenText)
            }
        }
    }

    val startVoiceRecognition: () -> Unit = {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.code)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your symptom or answer clearly...")
            }
            isListeningSpeech = true
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            isListeningSpeech = false
            Toast.makeText(context, "Voice input not available. Please type or use quick replies.", Toast.LENGTH_SHORT).show()
        }
    }

    // Inactivity Timeout Dialog
    if (showInactivityDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setInactivityDialogVisible(false) },
            icon = {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = AlertOrange,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Are you still there?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthNavyHeader
                    )
                )
            },
            text = {
                Text(
                    text = "To protect your health privacy, this kiosk will automatically clear your session in 30 seconds unless you continue.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HealthTextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.setInactivityDialogVisible(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I'm Still Here")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.resetForNewPatient()
                        viewModel.setInactivityDialogVisible(false)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Now", color = AlertRed)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Staff Assistance Modal Dialog
    if (showStaffAssistanceDialog) {
        StaffAssistanceDialog(
            kioskId = "Kiosk 01 (Outpatient B)",
            onDismiss = { showStaffAssistanceDialog = false },
            onRequestAssistance = { opt ->
                viewModel.requestStaffAssistance(opt.title)
                showStaffAssistanceDialog = false
                Toast.makeText(context, "🔔 Attending nurse notified: ${opt.title}", Toast.LENGTH_LONG).show()
            }
        )
    }

    // In-App Notification Center Dialog
    if (showNotificationCenter) {
        NotificationCenterDialog(
            notifications = notifications,
            onDismiss = {
                showNotificationCenter = false
                viewModel.markAllNotificationsRead()
            },
            onClearAll = viewModel::clearNotifications
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Persistent Health Bottom Navigation Bar (Home, Records, Docs, Staff View, Profile)
            HealthBottomBar(
                currentScreen = currentScreen,
                onTabSelected = { tab ->
                    when (tab) {
                        HealthBottomTab.HOME -> viewModel.navigateTo(KioskScreen.WELCOME)
                        HealthBottomTab.RECORDS -> viewModel.navigateTo(KioskScreen.MY_RECORDS)
                        HealthBottomTab.DOCUMENTS -> viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                        HealthBottomTab.STAFF -> viewModel.navigateTo(KioskScreen.STAFF_VIEW)
                        HealthBottomTab.PROFILE -> viewModel.navigateTo(KioskScreen.PATIENT_PROFILE)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            val animDuration = if (isReduceMotionEnabled) 0 else 280

            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (isReduceMotionEnabled) {
                        fadeIn(animationSpec = tween(0)).togetherWith(fadeOut(animationSpec = tween(0)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(animDuration)) { width -> (width * 0.15f).toInt() } + fadeIn(animationSpec = tween(animDuration)))
                            .togetherWith(
                                slideOutHorizontally(animationSpec = tween(animDuration)) { width -> (-width * 0.15f).toInt() } + fadeOut(animationSpec = tween(animDuration))
                            )
                    }
                },
                label = "kiosk_screen_animated_content"
            ) { screen ->
                when (screen) {
                    KioskScreen.WELCOME -> {
                        WelcomeScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            hasActiveVisit = hasActiveVisit,
                            activeVisitStatus = activeVisitStatus,
                            queuePosition = queuePosition,
                            hasUnfinishedSession = hasUnfinishedSession,
                            syncStatus = syncStatus,
                            unreadNotificationCount = notifications.count { !it.isRead },
                            recentRecords = myLocalRecords,
                            onResumeUnfinishedSession = viewModel::resumeUnfinishedSession,
                            onDiscardUnfinishedSession = viewModel::discardUnfinishedSession,
                            onLanguageSelected = viewModel::selectLanguage,
                            onSpeakLanguage = { lang ->
                                ttsManager.speak("${lang.greeting}. ${lang.welcomeMessage}", lang.code)
                            },
                            onStartIntake = {
                                viewModel.navigateTo(KioskScreen.PATIENT_IDENTIFY)
                            },
                            onScanDocuments = {
                                viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                            },
                            onShortcutClick = { shortcutKey ->
                                when (shortcutKey) {
                                    "CHIEF_COMPLAINT" -> viewModel.navigateTo(KioskScreen.CONVERSATIONAL_INTAKE)
                                    "MEDICATIONS" -> viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                                    "LAB_REPORTS" -> viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                                    "SUMMARY" -> viewModel.navigateTo(KioskScreen.SUMMARY)
                                }
                            },
                            onTrackQueue = {
                                viewModel.navigateTo(KioskScreen.QUEUE_TRACKER)
                            },
                            onViewRecords = {
                                viewModel.navigateTo(KioskScreen.MY_RECORDS)
                            },
                            onOpenAccessibility = {
                                viewModel.navigateTo(KioskScreen.ACCESSIBILITY_CENTER)
                            },
                            onOpenNotifications = {
                                showNotificationCenter = true
                            },
                            onNeedHelpClick = {
                                showStaffAssistanceDialog = true
                            },
                            onOpenProfile = {
                                viewModel.navigateTo(KioskScreen.PATIENT_PROFILE)
                            }
                        )
                    }

                    KioskScreen.PATIENT_IDENTIFY -> {
                        PatientIdentifyScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            isTtsSpeaking = isTtsSpeaking,
                            onNameChange = viewModel::updatePatientName,
                            onAgeChange = viewModel::updatePatientAge,
                            onGenderChange = viewModel::updatePatientGender,
                            onAbhaChange = viewModel::updatePatientAbha,
                            onNewPatientToggle = viewModel::toggleNewPatient,
                            onConsentToggle = viewModel::toggleConsent,
                            onReadConsentAloud = {
                                if (isTtsSpeaking) {
                                    ttsManager.stop()
                                } else {
                                    ttsManager.speak(selectedLanguage.consentExplanation, selectedLanguage.code)
                                }
                            },
                            onPrefillSample = viewModel::prefillMockPatient,
                            onProceed = {
                                viewModel.navigateTo(KioskScreen.CONVERSATIONAL_INTAKE)
                            },
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            }
                        )
                    }

                    KioskScreen.CONVERSATIONAL_INTAKE -> {
                        ConversationalIntakeScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            messages = messages,
                            quickReplies = quickReplies,
                            socratesData = socratesData,
                            redFlagAlert = redFlagAlert,
                            isAiThinking = isAiThinking,
                            isListeningSpeech = isListeningSpeech,
                            onSendMessage = viewModel::sendUserMessage,
                            onVoiceInputClick = startVoiceRecognition,
                            onSpeakMessage = { text ->
                                ttsManager.speak(text, selectedLanguage.code)
                            },
                            onProceedToDocs = {
                                viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                            },
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.PATIENT_IDENTIFY)
                            },
                            onAlertStaffClick = {
                                viewModel.requestStaffAssistance("Emergency Red Flag Alert Triggered")
                                Toast.makeText(context, "Emergency attending nurse alerted.", Toast.LENGTH_LONG).show()
                            },
                            onDismissRedFlag = { viewModel.clearRedFlagAlert() }
                        )
                    }

                    KioskScreen.DOCUMENT_SCAN -> {
                        DocumentScanScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            scannedBitmap = scannedBitmap,
                            isScanning = isScanningDocument,
                            extractedData = extractedData,
                            onImageSelected = viewModel::onDocumentImageSelected,
                            onLoadSample = viewModel::loadSampleDocument,
                            onAddDiagnosis = viewModel::addDiagnosis,
                            onRemoveDiagnosis = viewModel::removeDiagnosis,
                            onUpdateMedication = viewModel::updateMedication,
                            onRemoveMedication = viewModel::removeMedication,
                            onUpdateLab = viewModel::updateLabValue,
                            onRemoveLab = viewModel::removeLabValue,
                            onProceedToSummary = {
                                viewModel.navigateTo(KioskScreen.SUMMARY)
                            },
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.CONVERSATIONAL_INTAKE)
                            }
                        )
                    }

                    KioskScreen.SUMMARY -> {
                        SummaryScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            clinicalSummary = clinicalSummary,
                            isGenerating = isGeneratingSummary,
                            onRegenerate = viewModel::generateSummary,
                            onUpdateSection = viewModel::updateSummarySection,
                            onSubmit = viewModel::submitIntake,
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.DOCUMENT_SCAN)
                            }
                        )
                    }

                    KioskScreen.CONFIRMATION -> {
                        ConfirmationScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            clinicalSummary = clinicalSummary,
                            queuePosition = queuePosition,
                            patientLiveStatus = activeVisitStatus,
                            syncStatus = syncStatus,
                            onStartNewIntake = viewModel::resetForNewPatient,
                            onNeedHelpClick = {
                                showStaffAssistanceDialog = true
                            }
                        )
                    }

                    KioskScreen.STAFF_VIEW -> {
                        StaffViewScreen(
                            patients = patientsQueue,
                            syncStatus = syncStatus,
                            assistanceRequests = assistanceRequests,
                            currentRole = staffRole,
                            selectedDoctor = selectedDoctor,
                            onSelectRole = viewModel::setStaffRole,
                            onSelectDoctor = viewModel::setSelectedDoctor,
                            onUpdateStatus = viewModel::updatePatientStatus,
                            onAssignDoctor = viewModel::assignDoctor,
                            onUpdatePriority = viewModel::updatePriority,
                            onSaveClinicianNotes = viewModel::saveClinicianNotes,
                            onRecordVitals = viewModel::recordVitals,
                            onAcknowledgeAssistance = viewModel::acknowledgeAssistanceRequest,
                            onResolveAssistance = viewModel::resolveAssistanceRequest,
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            }
                        )
                    }

                    KioskScreen.MY_RECORDS -> {
                        MyRecordsScreen(
                            records = myLocalRecords,
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            },
                            onDeleteRecord = viewModel::deleteLocalRecord,
                            onStartNewIntake = {
                                viewModel.navigateTo(KioskScreen.PATIENT_IDENTIFY)
                            }
                        )
                    }

                    KioskScreen.PATIENT_PROFILE -> {
                        ProfileScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            hasActiveVisit = hasActiveVisit,
                            activeVisitStatus = activeVisitStatus,
                            onNavigateToRecords = { viewModel.navigateTo(KioskScreen.MY_RECORDS) },
                            onNavigateToAccessibility = { viewModel.navigateTo(KioskScreen.ACCESSIBILITY_CENTER) },
                            onNavigateToPrivacyCenter = { viewModel.navigateTo(KioskScreen.PRIVACY_CENTER) },
                            onNavigateToStaffView = { viewModel.navigateTo(KioskScreen.STAFF_VIEW) },
                            onNavigateToQueueTracker = { viewModel.navigateTo(KioskScreen.QUEUE_TRACKER) },
                            onResetSession = viewModel::resetForNewPatient,
                            onNeedHelpClick = { showStaffAssistanceDialog = true }
                        )
                    }

                    KioskScreen.ACCESSIBILITY_CENTER -> {
                        AccessibilityScreen(
                            currentTextScale = textScale,
                            isHighContrast = isHighContrast,
                            isVoiceGuidanceEnabled = isVoiceGuidanceEnabled,
                            isReduceMotionEnabled = isReduceMotionEnabled,
                            selectedLanguage = selectedLanguage,
                            onTextScaleChanged = viewModel::setTextScale,
                            onHighContrastChanged = viewModel::toggleHighContrast,
                            onVoiceGuidanceChanged = viewModel::setVoiceGuidance,
                            onReduceMotionChanged = viewModel::setReduceMotion,
                            onLanguageSelected = viewModel::selectLanguage,
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            }
                        )
                    }

                    KioskScreen.QUEUE_TRACKER -> {
                        QueueTrackerScreen(
                            patientInfo = patientInfo,
                            selectedLanguage = selectedLanguage,
                            queuePosition = queuePosition,
                            patientLiveStatus = activeVisitStatus,
                            syncStatus = syncStatus,
                            lastUpdatedTime = "Just now",
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            },
                            onNeedHelpClick = {
                                showStaffAssistanceDialog = true
                            },
                            onViewRecords = {
                                viewModel.navigateTo(KioskScreen.MY_RECORDS)
                            }
                        )
                    }

                    KioskScreen.PRIVACY_CENTER -> {
                        PrivacyCenterScreen(
                            onBackClick = {
                                viewModel.navigateTo(KioskScreen.WELCOME)
                            }
                        )
                    }
                }
            }
        }
    }
}
