package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.StudentAnswerEntity
import com.example.ui.components.AccuracyDonutChart
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankPurpleLight
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun ResultScreen(
    attemptId: String,
    examViewModel: ExamViewModel,
    onNavigateHome: () -> Unit,
    onNavigateLeaderboard: () -> Unit,
    onRetakeExam: (String) -> Unit
) {
    val state by examViewModel.sessionState.collectAsState()

    LaunchedEffect(attemptId) {
        if (state.latestAttempt == null || state.latestAttempt?.id != attemptId) {
            examViewModel.loadAttemptResult(attemptId)
        }
    }

    val attempt = state.latestAttempt

    if (state.isLoading || attempt == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = EduRankPurple)
        }
        return
    }

    val timeMinutes = attempt.timeTakenSeconds / 60
    val timeSecs = attempt.timeTakenSeconds % 60

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("result_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Result Score Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("result_score_banner"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (attempt.isPassed) {
                                    listOf(EduRankPurple, EduRankPurpleLight)
                                } else {
                                    listOf(Color(0xFF991B1B), Color(0xFFDC2626))
                                }
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (attempt.isPassed) Icons.Default.WorkspacePremium else Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (attempt.isPassed) "EXCELLENT PERFORMANCE!" else "NEEDS PRACTICE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "%.2f / %.0f".format(attempt.score, attempt.totalMarks),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "Obtained Net Score (Marks)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (attempt.isPassed) "PASSED (Cutoff: 40%)" else "FAILED (Under Cutoff)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Metrics Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Score & Answer Breakdown",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AccuracyDonutChart(
                        correct = attempt.correctCount,
                        wrong = attempt.wrongCount,
                        unattempted = attempt.unattemptedCount
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResultStatItem("Time Taken", "${timeMinutes}m ${timeSecs}s", EduRankPurple)
                        ResultStatItem("Accuracy", "%.1f%%".format(attempt.accuracyPercentage), EduRankSuccess)
                        ResultStatItem("Rank Points", "+${(attempt.correctCount * 2) + 20}", EduRankAccent)
                    }
                }
            }
        }

        // 3. Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateHome,
                    modifier = Modifier.weight(1f).testTag("result_home_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dashboard", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateLeaderboard,
                    modifier = Modifier.weight(1f).testTag("result_leaderboard_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Leaderboard", fontSize = 12.sp)
                }
            }
        }

        // 4. Detailed Question Review Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question-by-Question Review (${state.reviewItems.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 5. Review Cards
        itemsIndexed(state.reviewItems) { index, (question, options, studentAnswer) ->
            QuestionReviewCard(
                index = index + 1,
                question = question,
                options = options,
                answer = studentAnswer
            )
        }
    }
}

@Composable
fun ResultStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun QuestionReviewCard(
    index: Int,
    question: QuestionEntity,
    options: List<QuestionOptionEntity>,
    answer: StudentAnswerEntity?
) {
    val isAnswered = answer?.selectedOptionIndex != null
    val isCorrect = answer?.isCorrect == true

    Card(
        modifier = Modifier.fillMaxWidth().testTag("review_card_$index"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Status tag & Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    !isAnswered -> Color.Gray
                                    isCorrect -> EduRankSuccess
                                    else -> EduRankError
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            !isAnswered -> "Skipped / Unanswered"
                            isCorrect -> "Correct (+1.0)"
                            else -> "Incorrect (-0.25)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            !isAnswered -> Color.Gray
                            isCorrect -> EduRankSuccess
                            else -> EduRankError
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options with Highlights
            val letters = listOf("A", "B", "C", "D")
            options.forEachIndexed { optIdx, opt ->
                val isThisSelected = answer?.selectedOptionIndex == opt.optionIndex
                val isThisCorrect = opt.isCorrect

                val (bgColor, borderColor, textColor) = when {
                    isThisCorrect -> Triple(Color(0xFFD1FAE5), EduRankSuccess, Color(0xFF065F46))
                    isThisSelected && !isThisCorrect -> Triple(Color(0xFFFEE2E2), EduRankError, Color(0xFF991B1B))
                    else -> Triple(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Color.Transparent, MaterialTheme.colorScheme.onSurface)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${letters.getOrElse(optIdx) { "" }})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = opt.optionText,
                            fontSize = 13.sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )

                        if (isThisCorrect) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = EduRankSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isThisSelected) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Wrong Choice",
                                tint = EduRankError,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Explanation Section
            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EduRankPurpleContainer)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 ব্যাখ্যা / Explanation:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankPurple
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}
