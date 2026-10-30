package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CreativeSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreativeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: CreativeSubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: CreativeSubmissionEntity)

    @Query("SELECT * FROM creative_submissions WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getSubmissionsByUser(userId: Long): Flow<List<CreativeSubmissionEntity>>

    @Query("SELECT * FROM creative_submissions WHERE id = :id")
    suspend fun getSubmissionById(id: String): CreativeSubmissionEntity?

    @Query("SELECT * FROM creative_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<CreativeSubmissionEntity>>

    @Query("SELECT * FROM creative_submissions WHERE status = 'PENDING' ORDER BY submittedAt ASC")
    fun getPendingSubmissions(): Flow<List<CreativeSubmissionEntity>>
}
