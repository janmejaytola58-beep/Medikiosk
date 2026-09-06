package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {
    private val TAG = "TtsManager"
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                textToSpeech?.language = Locale.ENGLISH
                textToSpeech?.setSpeechRate(0.95f) // Slightly slower, calm cadence for healthcare kiosk

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        Log.e(TAG, "TTS Error playing utterance: $utteranceId")
                    }
                })
            } else {
                Log.e(TAG, "Failed to initialize TextToSpeech: status=$status")
            }
        }
    }

    fun speak(text: String, languageCode: String = "en", utteranceId: String = "kiosk_tts") {
        if (!isInitialized || textToSpeech == null) return

        stop()

        val locale = when (languageCode) {
            "hi" -> Locale("hi", "IN")
            "ta" -> Locale("ta", "IN")
            "te" -> Locale("te", "IN")
            "bn" -> Locale("bn", "IN")
            "mr" -> Locale("mr", "IN")
            else -> Locale.ENGLISH
        }

        try {
            val result = textToSpeech?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.language = Locale.ENGLISH
            }
        } catch (e: Exception) {
            textToSpeech?.language = Locale.ENGLISH
        }

        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
