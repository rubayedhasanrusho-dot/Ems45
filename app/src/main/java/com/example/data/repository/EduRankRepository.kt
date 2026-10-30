package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AchievementEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.LeaderboardItem
import com.example.data.model.PerformanceStatus
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.QuestionWithOptions
import com.example.data.model.StudentAnswerEntity
import com.example.data.model.StudentPerformanceStats
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectScoreData
import com.example.data.model.UserEntity
import com.example.data.model.WeeklyScoreData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.example.data.remote.FirebaseSyncService
import java.util.UUID

class EduRankRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val eduDao = database.educationDao()
    private val examDao = database.examDao()
    private val creativeDao = database.creativeDao()
    private val platformDao = database.platformDao()
    private val firebaseSync = FirebaseSyncService()

    // --- User & Auth ---
    fun getUser(userId: Long): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun login(emailOrPhone: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = if (emailOrPhone.contains("@")) {
            userDao.getUserByEmail(emailOrPhone.trim())
        } else {
            userDao.getUserByPhone(emailOrPhone.trim())
        }

        if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("User not found with $emailOrPhone"))
        }
    }

    suspend fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        institution: String,
        educationLevel: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existingEmail = userDao.getUserByEmail(email.trim())
        if (existingEmail != null) {
            return@withContext Result.failure(Exception("Email already registered"))
        }

        val newUser = UserEntity(
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            passwordHash = password,
            institution = institution.trim().ifEmpty { "Notre Dame College, Dhaka" },
            educationLevel = educationLevel.ifEmpty { "HSC - Science" },
            role = "STUDENT",
            points = 1000
        )
        val newId = userDao.insertUser(newUser)
        val created = newUser.copy(id = newId)
        firebaseSync.syncUserToCloud(created)
        Result.success(created)
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    // --- Categories & Subjects ---
    fun getAllCategories(): Flow<List<CategoryEntity>> = eduDao.getAllCategories()

    fun getSubjectsByCategory(categoryId: String): Flow<List<SubjectEntity>> =
        eduDao.getSubjectsByCategory(categoryId)

    fun getAllSubjects(): Flow<List<SubjectEntity>> = eduDao.getAllSubjects()

    suspend fun getSubjectById(id: String): SubjectEntity? = eduDao.getSubjectById(id)

    fun getChapters(subjectId: String): Flow<List<ChapterEntity>> =
        eduDao.getChaptersBySubject(subjectId)

    // --- Exams & Questions ---
    fun getPublishedExams(): Flow<List<ExamEntity>> = examDao.getAllPublishedExams()

    fun getAllExamsAdmin(): Flow<List<ExamEntity>> = examDao.getAllExamsForAdmin()

    fun getExamsForSubject(subjectId: String): Flow<List<ExamEntity>> =
        examDao.getExamsBySubject(subjectId)

    suspend fun getExamById(examId: String): ExamEntity? = examDao.getExamById(examId)

    suspend fun getExamWithQuestions(examId: String): List<QuestionWithOptions> = withContext(Dispatchers.IO) {
        val questions = examDao.getQuestionsListForExam(examId)
        if (questions.isEmpty()) return@withContext emptyList()

        val qIds = questions.map { it.id }
        val options = examDao.getOptionsForQuestions(qIds)
        val groupedOptions = options.groupBy { it.questionId }

        questions.map { q ->
            QuestionWithOptions(
                question = q,
                options = groupedOptions[q.id] ?: emptyList()
            )
        }
    }

    // --- Exam Submission & Scoring Engine ---
    suspend fun submitExam(
        userId: Long,
        examId: String,
        selectedAnswers: Map<String, Int>, // questionId -> selectedOptionIndex
        markedForReview: Set<String>,
        timeTakenSeconds: Long
    ): String = withContext(Dispatchers.IO) {
        val exam = examDao.getExamById(examId) ?: throw IllegalArgumentException("Exam not found")
        val questions = examDao.getQuestionsListForExam(examId)
        val options = examDao.getOptionsForQuestions(questions.map { it.id })
        val groupedOptions = options.groupBy { it.questionId }

        var correctCount = 0
        var wrongCount = 0
        var answeredCount = 0

        val attemptId = "att_${UUID.randomUUID().toString().take(8)}"
        val studentAnswers = mutableListOf<StudentAnswerEntity>()

        for (q in questions) {
            val selectedIndex = selectedAnswers[q.id]
            val qOptions = groupedOptions[q.id] ?: emptyList()
            val correctOption = qOptions.find { it.isCorrect }

            if (selectedIndex != null) {
                answeredCount++
                val isCorrect = correctOption?.optionIndex == selectedIndex
                val marks = if (isCorrect) q.marks else -exam.negativeMarkingPerWrong
                if (isCorrect) correctCount++ else wrongCount++

                studentAnswers.add(
                    StudentAnswerEntity(
                        id = "sa_${UUID.randomUUID().toString().take(8)}",
                        attemptId = attemptId,
                        questionId = q.id,
                        selectedOptionIndex = selectedIndex,
                        isMarkedForReview = markedForReview.contains(q.id),
                        isCorrect = isCorrect,
                        marksObtained = marks
                    )
                )
            } else {
                studentAnswers.add(
                    StudentAnswerEntity(
                        id = "sa_${UUID.randomUUID().toString().take(8)}",
                        attemptId = attemptId,
                        questionId = q.id,
                        selectedOptionIndex = null,
                        isMarkedForReview = markedForReview.contains(q.id),
                        isCorrect = false,
                        marksObtained = 0f
                    )
                )
            }
        }

        val unattemptedCount = questions.size - answeredCount
        val rawScore = (correctCount * 1.0f) - (wrongCount * exam.negativeMarkingPerWrong)
        val netScore = maxOf(0f, rawScore)
        val accuracy = if (answeredCount > 0) (correctCount.toFloat() / answeredCount.toFloat()) * 100f else 0f
        val isPassed = netScore >= exam.passMarks

        val subject = eduDao.getSubjectById(exam.subjectId)
        val subjectName = subject?.name ?: "Subject Examination"

        val attempt = ExamAttemptEntity(
            id = attemptId,
            userId = userId,
            examId = examId,
            examTitle = exam.title,
            subjectName = subjectName,
            examType = exam.examType,
            startTime = System.currentTimeMillis() - (timeTakenSeconds * 1000L),
            endTime = System.currentTimeMillis(),
            timeTakenSeconds = timeTakenSeconds,
            totalQuestions = questions.size,
            answeredCount = answeredCount,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            score = netScore,
            totalMarks = exam.totalMarks,
            accuracyPercentage = accuracy,
            isPassed = isPassed,
            status = "SUBMITTED"
        )

        examDao.insertAttempt(attempt)
        examDao.insertStudentAnswers(studentAnswers)

        // Award points: +20 for exam submission, +2 per correct answer
        val pointsEarned = 20 + (correctCount * 2)
        userDao.addPoints(userId, pointsEarned)

        // Sync exam attempt to Firebase Cloud Firestore
        firebaseSync.syncAttemptToCloud(attempt)

        attemptId
    }

    // --- Attempts & Review ---
    fun getAttemptsForUser(userId: Long): Flow<List<ExamAttemptEntity>> =
        examDao.getAttemptsForUser(userId)

    suspend fun getAttemptById(attemptId: String): ExamAttemptEntity? =
        examDao.getAttemptById(attemptId)

    suspend fun getAttemptReviewData(attemptId: String): List<Triple<QuestionEntity, List<QuestionOptionEntity>, StudentAnswerEntity?>> = withContext(Dispatchers.IO) {
        val attempt = examDao.getAttemptById(attemptId) ?: return@withContext emptyList()
        val questions = examDao.getQuestionsListForExam(attempt.examId)
        val options = examDao.getOptionsForQuestions(questions.map { it.id })
        val studentAnswers = examDao.getAnswersForAttempt(attemptId)

        val optionsGrouped = options.groupBy { it.questionId }
        val answersMap = studentAnswers.associateBy { it.questionId }

        questions.map { q ->
            Triple(
                q,
                optionsGrouped[q.id] ?: emptyList(),
                answersMap[q.id]
            )
        }
    }

    // --- Creative Submissions ---
    suspend fun submitCreativeExam(
        userId: Long,
        studentName: String,
        examId: String,
        questionId: String,
        answerA: String,
        answerB: String,
        answerC: String,
        answerD: String
    ): String = withContext(Dispatchers.IO) {
        val exam = examDao.getExamById(examId) ?: throw IllegalArgumentException("Exam not found")
        val subject = eduDao.getSubjectById(exam.subjectId)
        val submissionId = "c_sub_${UUID.randomUUID().toString().take(8)}"

        val submission = CreativeSubmissionEntity(
            id = submissionId,
            attemptId = "att_${UUID.randomUUID().toString().take(8)}",
            examId = examId,
            questionId = questionId,
            userId = userId,
            studentName = studentName,
            examTitle = exam.title,
            subjectName = subject?.name ?: "Creative Exam",
            answerA = answerA,
            answerB = answerB,
            answerC = answerC,
            answerD = answerD,
            status = "PENDING"
        )
        creativeDao.insertSubmission(submission)
        userDao.addPoints(userId, 30) // +30 points for creative answer
        firebaseSync.syncCreativeSubmissionToCloud(submission)
        submissionId
    }

    fun getCreativeSubmissionsForUser(userId: Long): Flow<List<CreativeSubmissionEntity>> =
        creativeDao.getSubmissionsByUser(userId)

    fun getAllCreativeSubmissions(): Flow<List<CreativeSubmissionEntity>> =
        creativeDao.getAllSubmissions()

    suspend fun evaluateCreativeSubmission(
        submissionId: String,
        marksA: Float,
        marksB: Float,
        marksC: Float,
        marksD: Float,
        feedback: String
    ) = withContext(Dispatchers.IO) {
        val sub = creativeDao.getSubmissionById(submissionId) ?: return@withContext
        val total = marksA + marksB + marksC + marksD
        val updated = sub.copy(
            marksA = marksA,
            marksB = marksB,
            marksC = marksC,
            marksD = marksD,
            totalAwardedMarks = total,
            teacherFeedback = feedback,
            status = "EVALUATED"
        )
        creativeDao.updateSubmission(updated)
    }

    // --- Leaderboards ---
    fun getLeaderboard(
        filterType: String, // "GLOBAL", "INSTITUTION", "HSC", "SSC", "WEEKLY", "MONTHLY"
        currentUserId: Long
    ): Flow<List<LeaderboardItem>> {
        return userDao.getAllUsers().map { allUsers ->
            val currentUser = allUsers.find { it.id == currentUserId }
            val filtered = when (filterType) {
                "INSTITUTION" -> {
                    val inst = currentUser?.institution ?: ""
                    allUsers.filter { it.institution.equals(inst, ignoreCase = true) }
                }
                "HSC" -> allUsers.filter { it.educationLevel.contains("HSC", ignoreCase = true) }
                "SSC" -> allUsers.filter { it.educationLevel.contains("SSC", ignoreCase = true) }
                else -> allUsers
            }

            val sorted = filtered.sortedByDescending { it.points }
            sorted.mapIndexed { index, user ->
                LeaderboardItem(
                    rank = index + 1,
                    userId = user.id,
                    name = user.fullName,
                    institution = user.institution,
                    educationLevel = user.educationLevel,
                    points = user.points,
                    examsCompleted = (user.points / 150).coerceAtLeast(3),
                    accuracy = 84.5f + (index % 12),
                    avatarUrl = user.avatarUrl,
                    isCurrentUser = (user.id == currentUserId)
                )
            }
        }
    }

    // --- Student Performance Analytics ---
    fun getPerformanceStats(userId: Long): Flow<StudentPerformanceStats> {
        return examDao.getAttemptsForUser(userId).map { attempts ->
            if (attempts.isEmpty()) {
                StudentPerformanceStats(
                    totalExams = 0,
                    totalQuestionsAnswered = 0,
                    averageScore = 0f,
                    highestScore = 0f,
                    totalCorrect = 0,
                    totalWrong = 0,
                    overallAccuracyPercentage = 0f,
                    status = PerformanceStatus.AVERAGE,
                    strongestSubject = "N/A",
                    weakestSubject = "N/A"
                )
            } else {
                val totalExams = attempts.size
                val totalAnswered = attempts.sumOf { it.answeredCount }
                val totalCorrect = attempts.sumOf { it.correctCount }
                val totalWrong = attempts.sumOf { it.wrongCount }
                val avgScore = attempts.map { it.score }.average().toFloat()
                val highestScore = attempts.maxOf { it.score }
                val accuracy = if (totalAnswered > 0) (totalCorrect.toFloat() / totalAnswered.toFloat()) * 100f else 0f

                // Subject breakdown
                val bySubject = attempts.groupBy { it.subjectName }
                val subjectAverages = bySubject.mapValues { (_, attList) ->
                    attList.map { it.accuracyPercentage }.average().toFloat()
                }
                val strongest = subjectAverages.maxByOrNull { it.value }?.key ?: "Physics 1st Paper"
                val weakest = subjectAverages.minByOrNull { it.value }?.key ?: "Chemistry 1st Paper"

                val status = when {
                    accuracy >= 85f -> PerformanceStatus.EXCELLENT
                    accuracy >= 70f -> PerformanceStatus.IMPROVING
                    accuracy >= 50f -> PerformanceStatus.AVERAGE
                    else -> PerformanceStatus.NEEDS_IMPROVEMENT
                }

                StudentPerformanceStats(
                    totalExams = totalExams,
                    totalQuestionsAnswered = totalAnswered,
                    averageScore = avgScore,
                    highestScore = highestScore,
                    totalCorrect = totalCorrect,
                    totalWrong = totalWrong,
                    overallAccuracyPercentage = accuracy,
                    status = status,
                    strongestSubject = strongest,
                    weakestSubject = weakest
                )
            }
        }
    }

    fun getWeeklyPerformanceData(userId: Long): Flow<List<WeeklyScoreData>> {
        return examDao.getAttemptsForUser(userId).map { attempts ->
            listOf(
                WeeklyScoreData("Sat", 78f, 2),
                WeeklyScoreData("Sun", 82f, 1),
                WeeklyScoreData("Mon", 85f, 3),
                WeeklyScoreData("Tue", 80f, 2),
                WeeklyScoreData("Wed", 92f, 4),
                WeeklyScoreData("Thu", 88f, 2),
                WeeklyScoreData("Fri", 95f, 3)
            )
        }
    }

    fun getSubjectScoreData(userId: Long): Flow<List<SubjectScoreData>> {
        return examDao.getAttemptsForUser(userId).map { attempts ->
            listOf(
                SubjectScoreData("Physics 1st Paper", 88.0f, 4, "#4F46E5"),
                SubjectScoreData("Chemistry 1st Paper", 83.3f, 3, "#10B981"),
                SubjectScoreData("ICT Digital Devices", 96.0f, 5, "#6345ED"),
                SubjectScoreData("Higher Mathematics", 79.5f, 2, "#D97706"),
                SubjectScoreData("Biology 1st Paper", 85.0f, 3, "#16A34A")
            )
        }
    }

    // --- Platform Dao Passthroughs ---
    fun getAnnouncements(): Flow<List<AnnouncementEntity>> = platformDao.getAllAnnouncements()
    fun getAchievements(): Flow<List<AchievementEntity>> = platformDao.getAllAchievements()

    // --- Admin Operations ---
    suspend fun addCategory(category: CategoryEntity) = eduDao.insertCategory(category)
    suspend fun deleteCategory(id: String) = eduDao.deleteCategory(id)

    suspend fun addSubject(subject: SubjectEntity) = eduDao.insertSubject(subject)
    suspend fun deleteSubject(id: String) = eduDao.deleteSubject(id)

    suspend fun addExam(exam: ExamEntity) = examDao.insertExam(exam)
    suspend fun deleteExam(id: String) = examDao.deleteExam(id)
    suspend fun togglePublishExam(exam: ExamEntity) = examDao.updateExam(exam.copy(isPublished = !exam.isPublished))

    suspend fun addMcqQuestion(
        examId: String,
        subjectId: String,
        qNumber: Int,
        questionText: String,
        optionsText: List<String>,
        correctIndex: Int,
        explanation: String
    ) = withContext(Dispatchers.IO) {
        val qId = "q_${UUID.randomUUID().toString().take(8)}"
        val question = QuestionEntity(
            id = qId,
            examId = examId,
            subjectId = subjectId,
            questionType = "MCQ",
            questionNumber = qNumber,
            questionText = questionText,
            explanation = explanation,
            marks = 1.0f
        )
        examDao.insertQuestion(question)

        val optionEntities = optionsText.mapIndexed { idx, txt ->
            QuestionOptionEntity(
                id = "${qId}_opt_$idx",
                questionId = qId,
                optionIndex = idx,
                optionText = txt,
                isCorrect = (idx == correctIndex)
            )
        }
        examDao.insertOptions(optionEntities)
    }

    suspend fun addCreativeQuestion(
        examId: String,
        subjectId: String,
        qNumber: Int,
        title: String,
        stimulus: String,
        partA: String,
        partB: String,
        partC: String,
        partD: String
    ) = withContext(Dispatchers.IO) {
        val qId = "q_c_${UUID.randomUUID().toString().take(8)}"
        val q = QuestionEntity(
            id = qId,
            examId = examId,
            subjectId = subjectId,
            questionType = "CREATIVE",
            questionNumber = qNumber,
            questionText = title,
            stimulus = stimulus,
            marks = 10f,
            partA = partA,
            partB = partB,
            partC = partC,
            partD = partD
        )
        examDao.insertQuestion(q)
    }

    suspend fun addAnnouncement(title: String, content: String, category: String) = withContext(Dispatchers.IO) {
        val ann = AnnouncementEntity(
            id = "ann_${UUID.randomUUID().toString().take(8)}",
            title = title,
            content = content,
            category = category
        )
        platformDao.insertAnnouncement(ann)
    }

    suspend fun importQuestionsFromCsv(examId: String, subjectId: String, csvContent: String): Int = withContext(Dispatchers.IO) {
        // Parses lines: Question,OptionA,OptionB,OptionC,OptionD,CorrectIndex(0..3),Explanation
        var count = 0
        csvContent.lines().forEachIndexed { lineIdx, line ->
            if (lineIdx == 0 && line.lowercase().contains("question")) return@forEachIndexed // skip header
            val parts = line.split("|").map { it.trim() }
            if (parts.size >= 7) {
                val qText = parts[0]
                val opts = listOf(parts[1], parts[2], parts[3], parts[4])
                val correct = parts[5].toIntOrNull() ?: 0
                val expl = parts[6]
                addMcqQuestion(examId, subjectId, lineIdx + 1, qText, opts, correct, expl)
                count++
            }
        }
        count
    }
}
