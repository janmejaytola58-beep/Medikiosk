package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PatientRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientRecordDao {
    @Query("SELECT * FROM patient_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<PatientRecordEntity>>

    @Query("SELECT * FROM patient_records WHERE tokenNumber = :token LIMIT 1")
    suspend fun getRecordByToken(token: String): PatientRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PatientRecordEntity)

    @Update
    suspend fun updateRecord(record: PatientRecordEntity)

    @Query("DELETE FROM patient_records WHERE tokenNumber = :token")
    suspend fun deleteRecord(token: String)

    @Query("DELETE FROM patient_records")
    suspend fun clearAllRecords()
}
