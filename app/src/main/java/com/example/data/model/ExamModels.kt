package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subjectId: String,
    val categoryId: String,
    val examType: String, // "MCQ" or "CREATIVE"
    val groupName: String = "Science",
    val totalQuestions: Int = 25,
    val durationMinutes: Int = 25,
    val totalMarks: Float = 25f,
    val passMarks: Float = 10f,
    val negativeMarkingPerWrong: Float = 0.25f,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val examId: String,
    val subjectId: String,
    val questionType: String, // "MCQ" or "CREATIVE"
    val questionNumber: Int,
    val questionText: String,
    val stimulus: String = "", // উদ্দীপক (for creative questions)
    val explanation: String = "",
    val marks: Float = 1.0f,
    // Creative sub-sections
    val partA: String = "", // ক (জ্ঞানমূলক - 1 mark)
    val partB: String = "", // খ (অনুধাবনমূলক - 2 marks)
    val partC: String = "", // গ (প্রয়োগমূলক - 3 marks)
    val partD: String = ""  // ঘ (উচ্চতর দক্ষতামূলক - 4 marks)
)

@Entity(tableName = "question_options")
data class QuestionOptionEntity(
    @PrimaryKey
    val id: String,
    val questionId: String,
    val optionIndex: Int, // 0, 1, 2, 3 (A, B, C, D)
    val optionText: String,
    val isCorrect: Boolean
)

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey
    val id: String,
    val userId: Long,
    val examId: String,
    val examTitle: String,
    val subjectName: String,
    val examType: String, // "MCQ" or "CREATIVE"
    val startTime: Long,
    val endTime: Long,
    val timeTakenSeconds: Long,
    val totalQuestions: Int,
    val answeredCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val score: Float,
    val totalMarks: Float,
    val accuracyPercentage: Float,
    val isPassed: Boolean,
    val status: String // "SUBMITTED", "PENDING_EVALUATION", "EVALUATED"
)

@Entity(tableName = "student_answers")
data class StudentAnswerEntity(
    @PrimaryKey
    val id: String,
    val attemptId: String,
    val questionId: String,
    val selectedOptionIndex: Int?, // null if skipped
    val isMarkedForReview: Boolean = false,
    val isCorrect: Boolean = false,
    val marksObtained: Float = 0f
)

@Entity(tableName = "creative_submissions")
data class CreativeSubmissionEntity(
    @PrimaryKey
    val id: String,
    val attemptId: String,
    val examId: String,
    val questionId: String,
    val userId: Long,
    val studentName: String,
    val examTitle: String,
    val subjectName: String,
    val answerA: String,
    val answerB: String,
    val answerC: String,
    val answerD: String,
    val marksA: Float? = null,
    val marksB: Float? = null,
    val marksC: Float? = null,
    val marksD: Float? = null,
    val totalAwardedMarks: Float? = null,
    val teacherFeedback: String? = null,
    val status: String = "PENDING", // "PENDING", "EVALUATED"
    val submittedAt: Long = System.currentTimeMillis()
)
