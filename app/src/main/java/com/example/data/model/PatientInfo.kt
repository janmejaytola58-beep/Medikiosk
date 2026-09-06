package com.example.data.model

data class PatientInfo(
    val name: String = "",
    val age: String = "",
    val gender: String = "Male",
    val abhaId: String = "",
    val isNewPatient: Boolean = false,
    val hasConsented: Boolean = false,
    val tokenNumber: String = "MK-" + (1000..9999).random()
) {
    val isValid: Boolean
        get() {
            val nameValid = name.trim().isNotBlank()
            val ageValid = age.trim().toIntOrNull()?.let { it in 1..120 } ?: false
            val abhaValid = isNewPatient || abhaId.trim().isNotBlank()
            return nameValid && ageValid && gender.isNotBlank() && abhaValid && hasConsented
        }
}
