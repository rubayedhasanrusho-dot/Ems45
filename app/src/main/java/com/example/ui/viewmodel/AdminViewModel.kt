package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryEntity
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.UserEntity
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminUiState(
    val students: List<UserEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val subjects: List<SubjectEntity> = emptyList(),
    val exams: List<ExamEntity> = emptyList(),
    val creativeSubmissions: List<CreativeSubmissionEntity> = emptyList(),
    val selectedTab: Int = 0, // 0: Overview, 1: Exams, 2: Questions, 3: Creative Evaluation, 4: Students & Data
    val message: String? = null,
    val isLoading: Boolean = false
)

class AdminViewModel(private val repository: EduRankRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun loadAllData() {
        viewModelScope.launch {
            repository.getAllUsers().collect { list ->
                _uiState.value = _uiState.value.copy(students = list)
            }
        }
        viewModelScope.launch {
            repository.getAllCategories().collect { list ->
                _uiState.value = _uiState.value.copy(categories = list)
            }
        }
        viewModelScope.launch {
            repository.getAllSubjects().collect { list ->
                _uiState.value = _uiState.value.copy(subjects = list)
            }
        }
        viewModelScope.launch {
            repository.getAllExamsAdmin().collect { list ->
                _uiState.value = _uiState.value.copy(exams = list)
            }
        }
        viewModelScope.launch {
            repository.getAllCreativeSubmissions().collect { list ->
                _uiState.value = _uiState.value.copy(creativeSubmissions = list)
            }
        }
    }

    fun createExam(
        title: String,
        subjectId: String,
        categoryId: String,
        examType: String,
        totalQuestions: Int,
        durationMinutes: Int,
        totalMarks: Float,
        passMarks: Float,
        negativeMarking: Float
    ) {
        viewModelScope.launch {
            val exam = ExamEntity(
                id = "exam_${UUID.randomUUID().toString().take(8)}",
                title = title,
                subjectId = subjectId,
                categoryId = categoryId,
                examType = examType,
                totalQuestions = totalQuestions,
                durationMinutes = durationMinutes,
                totalMarks = totalMarks,
                passMarks = passMarks,
                negativeMarkingPerWrong = negativeMarking,
                isPublished = true
            )
            repository.addExam(exam)
            _uiState.value = _uiState.value.copy(message = "Exam '$title' created successfully!")
        }
    }

    fun togglePublish(exam: ExamEntity) {
        viewModelScope.launch {
            repository.togglePublishExam(exam)
            _uiState.value = _uiState.value.copy(message = "Exam publish status toggled.")
        }
    }

    fun deleteExam(examId: String) {
        viewModelScope.launch {
            repository.deleteExam(examId)
            _uiState.value = _uiState.value.copy(message = "Exam deleted.")
        }
    }

    fun addMcqQuestion(
        examId: String,
        subjectId: String,
        qNumber: Int,
        qText: String,
        opts: List<String>,
        correctIndex: Int,
        explanation: String
    ) {
        viewModelScope.launch {
            repository.addMcqQuestion(examId, subjectId, qNumber, qText, opts, correctIndex, explanation)
            _uiState.value = _uiState.value.copy(message = "Question added to exam!")
        }
    }

    fun evaluateCreative(
        subId: String,
        mA: Float,
        mB: Float,
        mC: Float,
        mD: Float,
        feedback: String
    ) {
        viewModelScope.launch {
            repository.evaluateCreativeSubmission(subId, mA, mB, mC, mD, feedback)
            _uiState.value = _uiState.value.copy(message = "Evaluation and marks published to student!")
        }
    }

    fun importCsv(examId: String, subjectId: String, csvText: String) {
        viewModelScope.launch {
            val count = repository.importQuestionsFromCsv(examId, subjectId, csvText)
            _uiState.value = _uiState.value.copy(message = "Imported $count questions from CSV successfully!")
        }
    }

    fun postAnnouncement(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.addAnnouncement(title, content, category)
            _uiState.value = _uiState.value.copy(message = "Announcement published across platform!")
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
