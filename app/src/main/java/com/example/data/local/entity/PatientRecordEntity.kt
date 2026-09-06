package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_records")
data class PatientRecordEntity(
    @PrimaryKey
    val tokenNumber: String,
    val patientName: String,
    val patientAge: String,
    val patientGender: String,
    val abhaId: String,
    val chiefComplaint: String,
    val triageLevel: String,
    val isUrgent: Boolean,
    val urgentReason: String?,
    val formattedDate: String,
    val timestamp: Long,
    val clinicalSummaryJson: String,
    val medicationsSummary: String,
    val vitals: String,
    val status: String = "WAITING"
)
