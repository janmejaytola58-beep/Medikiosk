package com.example.data.local

import com.example.data.local.dao.PatientRecordDao
import com.example.data.local.entity.PatientRecordEntity
import kotlinx.coroutines.flow.Flow

class LocalPatientRepository(private val dao: PatientRecordDao) {
    val allRecords: Flow<List<PatientRecordEntity>> = dao.getAllRecords()

    suspend fun insertRecord(record: PatientRecordEntity) {
        dao.insertRecord(record)
    }

    suspend fun getRecordByToken(token: String): PatientRecordEntity? {
        return dao.getRecordByToken(token)
    }

    suspend fun updateStatus(token: String, newStatus: String) {
        val existing = dao.getRecordByToken(token)
        if (existing != null) {
            dao.updateRecord(existing.copy(status = newStatus))
        }
    }

    suspend fun deleteRecord(token: String) {
        dao.deleteRecord(token)
    }
}
