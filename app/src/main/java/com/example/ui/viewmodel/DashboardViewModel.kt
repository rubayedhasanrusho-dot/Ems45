package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ExamEntity
import com.example.data.model.LeaderboardItem
import com.example.data.model.StudentPerformanceStats
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val popularExams: List<ExamEntity> = emptyList(),
    val announcements: List<AnnouncementEntity> = emptyList(),
    val topLeaderboard: List<LeaderboardItem> = emptyList(),
    val studentStats: StudentPerformanceStats? = null,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val repository: EduRankRepository,
    private val currentUserId: Long = 1L
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData(currentUserId)
    }

    fun loadDashboardData(userId: Long) {
        viewModelScope.launch {
            repository.getAllCategories().collect { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }

        viewModelScope.launch {
            repository.getPublishedExams().collect { exams ->
                _uiState.value = _uiState.value.copy(popularExams = exams)
            }
        }

        viewModelScope.launch {
            repository.getAnnouncements().collect { ann ->
                _uiState.value = _uiState.value.copy(announcements = ann)
            }
        }

        viewModelScope.launch {
            repository.getLeaderboard("GLOBAL", userId).collect { leaders ->
                _uiState.value = _uiState.value.copy(topLeaderboard = leaders.take(5))
            }
        }

        viewModelScope.launch {
            repository.getPerformanceStats(userId).collect { stats ->
                _uiState.value = _uiState.value.copy(studentStats = stats)
            }
        }
    }
}
