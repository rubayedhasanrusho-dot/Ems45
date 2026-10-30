package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.StudentAnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getAllPublishedExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams ORDER BY createdAt DESC")
    fun getAllExamsForAdmin(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE subjectId = :subjectId AND isPublished = 1")
    fun getExamsBySubject(subjectId: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :examId")
    suspend fun getExamById(examId: String): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExam(examId: String)

    @Query("SELECT * FROM questions WHERE examId = :examId ORDER BY questionNumber ASC")
    fun getQuestionsForExam(examId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE examId = :examId ORDER BY questionNumber ASC")
    suspend fun getQuestionsListForExam(examId: String): List<QuestionEntity>

    @Query("SELECT * FROM question_options WHERE questionId = :questionId ORDER BY optionIndex ASC")
    suspend fun getOptionsForQuestion(questionId: String): List<QuestionOptionEntity>

    @Query("SELECT * FROM question_options WHERE questionId IN (:questionIds) ORDER BY optionIndex ASC")
    suspend fun getOptionsForQuestions(questionIds: List<String>): List<QuestionOptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<QuestionOptionEntity>)

    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: String)

    @Query("DELETE FROM question_options WHERE questionId = :questionId")
    suspend fun deleteOptionsForQuestion(questionId: String)

    // Attempts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttemptEntity)

    @Query("SELECT * FROM exam_attempts WHERE userId = :userId ORDER BY endTime DESC")
    fun getAttemptsForUser(userId: Long): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts WHERE id = :attemptId")
    suspend fun getAttemptById(attemptId: String): ExamAttemptEntity?

    @Query("SELECT * FROM exam_attempts ORDER BY endTime DESC")
    fun getAllAttempts(): Flow<List<ExamAttemptEntity>>

    // Student Answers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentAnswers(answers: List<StudentAnswerEntity>)

    @Query("SELECT * FROM student_answers WHERE attemptId = :attemptId")
    suspend fun getAnswersForAttempt(attemptId: String): List<StudentAnswerEntity>
}
