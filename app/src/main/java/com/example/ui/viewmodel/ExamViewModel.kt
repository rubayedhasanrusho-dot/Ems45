package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.QuestionWithOptions
import com.example.data.model.StudentAnswerEntity
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExamSessionState(
    val exam: ExamEntity? = null,
    val questionsWithOptions: List<QuestionWithOptions> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(), // questionId -> optionIndex
    val markedForReview: Set<String> = emptySet(),
    val remainingSeconds: Long = 0L,
    val totalSeconds: Long = 0L,
    val isExamActive: Boolean = false,
    val isSubmitted: Boolean = false,
    val submittedAttemptId: String? = null,
    val latestAttempt: ExamAttemptEntity? = null,
    val reviewItems: List<Triple<QuestionEntity, List<QuestionOptionEntity>, StudentAnswerEntity?>> = emptyList(),
    val showConfirmSubmitDialog: Boolean = false,
    val showQuestionsPaletteSheet: Boolean = false,
    val isLoading: Boolean = false
)

class ExamViewModel(private val repository: EduRankRepository) : ViewModel() {

    private val _sessionState = MutableStateFlow(ExamSessionState())
    val sessionState: StateFlow<ExamSessionState> = _sessionState.asStateFlow()

    private var timerJob: Job? = null

    fun startExam(examId: String, userId: Long) {
        viewModelScope.launch {
            _sessionState.value = ExamSessionState(isLoading = true)
            val exam = repository.getExamById(examId) ?: return@launch
            val questions = repository.getExamWithQuestions(examId)

            val totalSecs = (exam.durationMinutes * 60).toLong()

            _sessionState.value = ExamSessionState(
                exam = exam,
                questionsWithOptions = questions,
                currentQuestionIndex = 0,
                selectedAnswers = emptyMap(),
                markedForReview = emptySet(),
                remainingSeconds = totalSecs,
                totalSeconds = totalSecs,
                isExamActive = true,
                isLoading = false
            )

            startTimer(userId)
        }
    }

    private fun startTimer(userId: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_sessionState.value.remainingSeconds > 0 && _sessionState.value.isExamActive) {
                delay(1000)
                val currentSecs = _sessionState.value.remainingSeconds - 1
                _sessionState.value = _sessionState.value.copy(remainingSeconds = currentSecs)
                if (currentSecs <= 0L) {
                    // Auto-submit when timer reaches zero!
                    submitExam(userId)
                    break
                }
            }
        }
    }

    fun selectOption(questionId: String, optionIndex: Int) {
        if (!_sessionState.value.isExamActive) return
        val currentAnswers = _sessionState.value.selectedAnswers.toMutableMap()
        if (currentAnswers[questionId] == optionIndex) {
            // Deselect on second tap
            currentAnswers.remove(questionId)
        } else {
            currentAnswers[questionId] = optionIndex
        }
        _sessionState.value = _sessionState.value.copy(selectedAnswers = currentAnswers)
    }

    fun toggleMarkForReview(questionId: String) {
        val currentMarks = _sessionState.value.markedForReview.toMutableSet()
        if (currentMarks.contains(questionId)) {
            currentMarks.remove(questionId)
        } else {
            currentMarks.add(questionId)
        }
        _sessionState.value = _sessionState.value.copy(markedForReview = currentMarks)
    }

    fun goToQuestion(index: Int) {
        val count = _sessionState.value.questionsWithOptions.size
        if (index in 0 until count) {
            _sessionState.value = _sessionState.value.copy(
                currentQuestionIndex = index,
                showQuestionsPaletteSheet = false
            )
        }
    }

    fun nextQuestion() {
        val nextIdx = _sessionState.value.currentQuestionIndex + 1
        if (nextIdx < _sessionState.value.questionsWithOptions.size) {
            _sessionState.value = _sessionState.value.copy(currentQuestionIndex = nextIdx)
        }
    }

    fun prevQuestion() {
        val prevIdx = _sessionState.value.currentQuestionIndex - 1
        if (prevIdx >= 0) {
            _sessionState.value = _sessionState.value.copy(currentQuestionIndex = prevIdx)
        }
    }

    fun setConfirmDialogVisible(visible: Boolean) {
        _sessionState.value = _sessionState.value.copy(showConfirmSubmitDialog = visible)
    }

    fun setPaletteSheetVisible(visible: Boolean) {
        _sessionState.value = _sessionState.value.copy(showQuestionsPaletteSheet = visible)
    }

    fun submitExam(userId: Long) {
        timerJob?.cancel()
        viewModelScope.launch {
            val exam = _sessionState.value.exam ?: return@launch
            val timeTaken = _sessionState.value.totalSeconds - _sessionState.value.remainingSeconds

            val attemptId = repository.submitExam(
                userId = userId,
                examId = exam.id,
                selectedAnswers = _sessionState.value.selectedAnswers,
                markedForReview = _sessionState.value.markedForReview,
                timeTakenSeconds = timeTaken.coerceAtLeast(1L)
            )

            loadAttemptResult(attemptId)
        }
    }

    fun loadAttemptResult(attemptId: String) {
        viewModelScope.launch {
            _sessionState.value = _sessionState.value.copy(isLoading = true)
            val attempt = repository.getAttemptById(attemptId)
            val reviewData = repository.getAttemptReviewData(attemptId)

            _sessionState.value = _sessionState.value.copy(
                isExamActive = false,
                isSubmitted = true,
                submittedAttemptId = attemptId,
                latestAttempt = attempt,
                reviewItems = reviewData,
                showConfirmSubmitDialog = false,
                isLoading = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
