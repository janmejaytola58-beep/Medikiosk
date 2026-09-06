package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Language
import com.example.data.model.PatientInfo
import com.example.ui.viewmodel.KioskScreen
import com.example.ui.viewmodel.KioskViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MediKiosk", appName)
    }

    @Test
    fun `verify supported languages contain English and Hindi`() {
        val languages = Language.SUPPORTED_LANGUAGES
        assertTrue(languages.size >= 5)
        assertTrue(languages.any { it.code == "en" })
        assertTrue(languages.any { it.code == "hi" && it.nativeName == "हिन्दी" })
    }

    @Test
    fun `verify patient validation requirements`() {
        val invalidPatient = PatientInfo(name = "", age = "", hasConsented = false)
        assertFalse(invalidPatient.isValid)

        val partialPatient = PatientInfo(name = "Ramesh", age = "54", hasConsented = false)
        assertFalse(partialPatient.isValid)

        val validPatient = PatientInfo(
            name = "Ramesh Sharma",
            age = "54",
            gender = "Male",
            abhaId = "91-4829-1029-4821",
            isNewPatient = false,
            hasConsented = true
        )
        assertTrue(validPatient.isValid)

        val validNewPatient = PatientInfo(
            name = "Priya Patel",
            age = "32",
            gender = "Female",
            abhaId = "",
            isNewPatient = true,
            hasConsented = true
        )
        assertTrue(validNewPatient.isValid)
    }

    @Test
    fun `verify kiosk viewModel navigation flow`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = KioskViewModel(app)
        assertEquals(KioskScreen.WELCOME, viewModel.currentScreen.value)

        viewModel.navigateTo(KioskScreen.PATIENT_IDENTIFY)
        assertEquals(KioskScreen.PATIENT_IDENTIFY, viewModel.currentScreen.value)

        viewModel.prefillMockPatient()
        assertTrue(viewModel.patientInfo.value.isValid)
        assertEquals("Ramesh Kumar", viewModel.patientInfo.value.name)

        viewModel.loadSampleDocument()
        assertTrue(viewModel.extractedData.value.medications.isNotEmpty())
    }

    @Test
    fun `verify red flag detection triggers on chest pain and breathlessness`() {
        val alert = com.example.data.model.RedFlagDetector.analyze("I have heavy chest pain and I feel breathless")
        org.junit.Assert.assertNotNull(alert)
        assertEquals("Urgent: Cardiopulmonary Red Flag", alert?.title)
        assertTrue(alert?.matchedSymptoms?.contains("Chest Pain / Pressure") == true)
        assertTrue(alert?.matchedSymptoms?.contains("Shortness of Breath") == true)
    }

    @Test
    fun `verify red flag detection returns null on non-emergency complaint`() {
        val alert = com.example.data.model.RedFlagDetector.analyze("I have had a mild headache and runny nose since yesterday")
        org.junit.Assert.assertNull(alert)
    }

    @Test
    fun `verify accessibility settings toggling in viewModel`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = KioskViewModel(app)
        assertFalse(viewModel.isHighContrast.value)
        assertFalse(viewModel.isLargeFont.value)

        viewModel.toggleHighContrast(true)
        assertTrue(viewModel.isHighContrast.value)

        viewModel.toggleLargeFont(true)
        assertTrue(viewModel.isLargeFont.value)

        viewModel.toggleHighContrast(false)
        assertFalse(viewModel.isHighContrast.value)
    }
}
