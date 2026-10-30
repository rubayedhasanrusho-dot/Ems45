package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.StudentPerformanceStats
import com.example.data.model.SubjectScoreData
import com.example.data.model.WeeklyScoreData
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val stats: StudentPerformanceStats? = null,
    val weeklyData: List<WeeklyScoreData> = emptyList(),
    val subjectScores: List<SubjectScoreData> = emptyList(),
    val isLoading: Boolean = false
)

class AnalyticsViewModel(
    private val repository: EduRankRepository,
    private val currentUserId: Long = 1L
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics(currentUserId)
    }

    fun loadAnalytics(userId: Long) {
        viewModelScope.launch {
            repository.getPerformanceStats(userId).collect { s ->
                _uiState.value = _uiState.value.copy(stats = s)
            }
        }
        viewModelScope.launch {
            repository.getWeeklyPerformanceData(userId).collect { w ->
                _uiState.value = _uiState.value.copy(weeklyData = w)
            }
        }
        viewModelScope.launch {
            repository.getSubjectScoreData(userId).collect { subj ->
                _uiState.value = _uiState.value.copy(subjectScores = subj)
            }
        }
    }
}
