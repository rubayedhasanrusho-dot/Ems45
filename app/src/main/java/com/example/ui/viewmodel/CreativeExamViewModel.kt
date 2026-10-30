package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreativeExamUiState(
    val exam: ExamEntity? = null,
    val question: QuestionEntity? = null,
    val answerA: String = "",
    val answerB: String = "",
    val answerC: String = "",
    val answerD: String = "",
    val isSubmitted: Boolean = false,
    val submission: CreativeSubmissionEntity? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

class CreativeExamViewModel(private val repository: EduRankRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CreativeExamUiState())
    val uiState: StateFlow<CreativeExamUiState> = _uiState.asStateFlow()

    fun loadCreativeExam(examId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val exam = repository.getExamById(examId)
            val questions = repository.getExamWithQuestions(examId)
            val firstCreative = questions.firstOrNull()?.question

            _uiState.value = CreativeExamUiState(
                exam = exam,
                question = firstCreative,
                isLoading = false
            )
        }
    }

    fun updateAnswerA(text: String) { _uiState.value = _uiState.value.copy(answerA = text) }
    fun updateAnswerB(text: String) { _uiState.value = _uiState.value.copy(answerB = text) }
    fun updateAnswerC(text: String) { _uiState.value = _uiState.value.copy(answerC = text) }
    fun updateAnswerD(text: String) { _uiState.value = _uiState.value.copy(answerD = text) }

    fun submitCreativeAnswers(userId: Long, studentName: String) {
        val q = _uiState.value.question ?: return
        val exam = _uiState.value.exam ?: return

        if (_uiState.value.answerA.isBlank() && _uiState.value.answerB.isBlank() &&
            _uiState.value.answerC.isBlank() && _uiState.value.answerD.isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Please write your answer before submitting.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val subId = repository.submitCreativeExam(
                userId = userId,
                studentName = studentName,
                examId = exam.id,
                questionId = q.id,
                answerA = _uiState.value.answerA,
                answerB = _uiState.value.answerB,
                answerC = _uiState.value.answerC,
                answerD = _uiState.value.answerD
            )

            _uiState.value = _uiState.value.copy(
                isSubmitted = true,
                isLoading = false,
                message = "Your creative answers have been submitted to EduRank lecturers for evaluation!"
            )
        }
    }
}
