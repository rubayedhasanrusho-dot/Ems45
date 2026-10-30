package com.example.data.remote

import android.util.Log
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.LeaderboardItem
import com.example.data.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseSyncService {

    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseSyncService", "Firebase Firestore initialization error: ${e.message}")
            null
        }
    }

    // Sync student profile to Firestore
    suspend fun syncUserToCloud(user: UserEntity) = withContext(Dispatchers.IO) {
        val firestore = db ?: return@withContext
        try {
            val userMap = hashMapOf(
                "id" to user.id,
                "fullName" to user.fullName,
                "email" to user.email,
                "phone" to user.phone,
                "institution" to user.institution,
                "educationLevel" to user.educationLevel,
                "role" to user.role,
                "points" to user.points,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(user.id.toString())
                .set(userMap, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncService", "User synced to Firestore: ${user.fullName}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Cloud sync user failed (running offline): ${e.message}")
        }
    }

    // Sync exam attempt to Firestore
    suspend fun syncAttemptToCloud(attempt: ExamAttemptEntity) = withContext(Dispatchers.IO) {
        val firestore = db ?: return@withContext
        try {
            val attemptMap = hashMapOf(
                "id" to attempt.id,
                "userId" to attempt.userId,
                "examId" to attempt.examId,
                "examTitle" to attempt.examTitle,
                "subjectName" to attempt.subjectName,
                "score" to attempt.score,
                "totalMarks" to attempt.totalMarks,
                "accuracyPercentage" to attempt.accuracyPercentage,
                "correctCount" to attempt.correctCount,
                "wrongCount" to attempt.wrongCount,
                "unattemptedCount" to attempt.unattemptedCount,
                "timeTakenSeconds" to attempt.timeTakenSeconds,
                "isPassed" to attempt.isPassed,
                "timestamp" to attempt.endTime
            )
            firestore.collection("exam_attempts")
                .document(attempt.id)
                .set(attemptMap, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncService", "Attempt synced to Firestore: ${attempt.id}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Cloud sync attempt failed: ${e.message}")
        }
    }

    // Sync creative submission
    suspend fun syncCreativeSubmissionToCloud(submission: CreativeSubmissionEntity) = withContext(Dispatchers.IO) {
        val firestore = db ?: return@withContext
        try {
            val subMap = hashMapOf(
                "id" to submission.id,
                "userId" to submission.userId,
                "studentName" to submission.studentName,
                "examTitle" to submission.examTitle,
                "subjectName" to submission.subjectName,
                "answerA" to submission.answerA,
                "answerB" to submission.answerB,
                "answerC" to submission.answerC,
                "answerD" to submission.answerD,
                "status" to submission.status,
                "submittedAt" to submission.submittedAt
            )
            firestore.collection("creative_submissions")
                .document(submission.id)
                .set(subMap, SetOptions.merge())
                .await()
            Log.d("FirebaseSyncService", "Creative submission synced to Firestore: ${submission.id}")
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Cloud sync creative failed: ${e.message}")
        }
    }

    // Fetch live Cloud Leaderboard from Firestore
    suspend fun fetchCloudLeaderboard(currentUserId: Long): List<LeaderboardItem>? = withContext(Dispatchers.IO) {
        val firestore = db ?: return@withContext null
        try {
            val snapshot = firestore.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            if (snapshot.isEmpty) return@withContext null

            snapshot.documents.mapIndexed { index, doc ->
                val id = doc.getLong("id") ?: (index + 1).toLong()
                val name = doc.getString("fullName") ?: "Student"
                val inst = doc.getString("institution") ?: "College"
                val level = doc.getString("educationLevel") ?: "HSC"
                val points = doc.getLong("points")?.toInt() ?: 1000

                LeaderboardItem(
                    rank = index + 1,
                    userId = id,
                    name = name,
                    institution = inst,
                    educationLevel = level,
                    points = points,
                    examsCompleted = (points / 150).coerceAtLeast(1),
                    accuracy = 85.0f + (index % 10),
                    avatarUrl = "",
                    isCurrentUser = (id == currentUserId)
                )
            }
        } catch (e: Exception) {
            Log.w("FirebaseSyncService", "Fetch cloud leaderboard error: ${e.message}")
            null
        }
    }
}
