package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String, // "Exam Alert", "Curriculum", "General", "Result"
    val timestamp: Long = System.currentTimeMillis(),
    val author: String = "EduRank Academic Board"
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val pointsReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

// UI Aggregate models
data class QuestionWithOptions(
    val question: QuestionEntity,
    val options: List<QuestionOptionEntity>
)

data class StudentPerformanceStats(
    val totalExams: Int,
    val totalQuestionsAnswered: Int,
    val averageScore: Float,
    val highestScore: Float,
    val totalCorrect: Int,
    val totalWrong: Int,
    val overallAccuracyPercentage: Float,
    val status: PerformanceStatus,
    val strongestSubject: String,
    val weakestSubject: String
)

enum class PerformanceStatus(val label: String, val colorHex: String) {
    EXCELLENT("Excellent", "#10B981"),
    IMPROVING("Improving", "#6345ED"),
    AVERAGE("Average", "#F59E0B"),
    NEEDS_IMPROVEMENT("Needs Improvement", "#EF4444")
}

data class LeaderboardItem(
    val rank: Int,
    val userId: Long,
    val name: String,
    val institution: String,
    val educationLevel: String,
    val points: Int,
    val examsCompleted: Int,
    val accuracy: Float,
    val avatarUrl: String,
    val isCurrentUser: Boolean = false
)

data class WeeklyScoreData(
    val dayName: String,
    val scorePercentage: Float,
    val examCount: Int
)

data class SubjectScoreData(
    val subjectName: String,
    val averagePercentage: Float,
    val totalAttempts: Int,
    val colorHex: String
)
