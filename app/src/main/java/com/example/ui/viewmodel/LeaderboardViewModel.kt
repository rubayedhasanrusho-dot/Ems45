package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LeaderboardItem
import com.example.data.repository.EduRankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val selectedFilter: String = "GLOBAL", // "GLOBAL", "INSTITUTION", "HSC", "SSC", "WEEKLY", "MONTHLY"
    val leaders: List<LeaderboardItem> = emptyList(),
    val currentUserItem: LeaderboardItem? = null,
    val isLoading: Boolean = false
)

class LeaderboardViewModel(
    private val repository: EduRankRepository,
    private val currentUserId: Long = 1L
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard(_uiState.value.selectedFilter, currentUserId)
    }

    fun setFilter(filter: String, userId: Long) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        loadLeaderboard(filter, userId)
    }

    private fun loadLeaderboard(filter: String, userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getLeaderboard(filter, userId).collect { list ->
                val userItem = list.find { it.userId == userId }
                _uiState.value = _uiState.value.copy(
                    leaders = list,
                    currentUserItem = userItem,
                    isLoading = false
                )
            }
        }
    }
}
