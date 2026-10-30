package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceStatus
import com.example.ui.components.AccuracyDonutChart
import com.example.ui.components.PerformanceStatusBadge
import com.example.ui.components.SubjectProgressBar
import com.example.ui.components.WeeklyPerformanceBarChart
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    currentUserId: Long,
    analyticsViewModel: AnalyticsViewModel
) {
    val state by analyticsViewModel.uiState.collectAsState()
    val stats = state.stats

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Overall Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Academic Performance Status",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        PerformanceStatusBadge(status = stats?.status ?: PerformanceStatus.EXCELLENT)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Based on continuous comparison with previous model tests, your board exam preparation is in the top tier nationwide.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 2. Weekly Performance Graph Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Performance Graph (%)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Average score trend across past 7 days",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    WeeklyPerformanceBarChart(weeklyData = state.weeklyData)
                }
            }
        }

        // 3. Correct vs Wrong Answers Chart Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Correct vs Wrong Answer Ratio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AccuracyDonutChart(
                        correct = stats?.totalCorrect ?: 66,
                        wrong = stats?.totalWrong ?: 8,
                        unattempted = 1
                    )
                }
            }
        }

        // 4. Strongest & Weakest Subject Callout
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🌟 Strongest Subject", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = stats?.strongestSubject ?: "Physics 1st Paper", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        Text(text = "96% Accuracy", fontSize = 11.sp, color = Color(0xFF065F46).copy(alpha = 0.8f))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🎯 Weakest Subject", fontSize = 11.sp, color = Color(0xFF991B1B), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = stats?.weakestSubject ?: "Chemistry 1st Paper", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text(text = "Needs more review", fontSize = 11.sp, color = Color(0xFF991B1B).copy(alpha = 0.8f))
                    }
                }
            }
        }

        // 5. Subject-wise Performance Meters
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Subject-Wise Performance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    state.subjectScores.forEach { subj ->
                        SubjectProgressBar(
                            name = subj.subjectName,
                            scorePercentage = subj.averagePercentage,
                            attemptsCount = subj.totalAttempts,
                            color = try {
                                Color(android.graphics.Color.parseColor(subj.colorHex))
                            } catch (e: Exception) {
                                EduRankPurple
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
