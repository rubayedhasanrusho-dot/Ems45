package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionOptionEntity
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankPurpleDark
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.ExamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    examId: String,
    currentUserId: Long,
    examViewModel: ExamViewModel,
    onBack: () -> Unit,
    onNavigateToResult: (String) -> Unit
) {
    val state by examViewModel.sessionState.collectAsState()

    LaunchedEffect(examId) {
        examViewModel.startExam(examId, currentUserId)
    }

    LaunchedEffect(state.isSubmitted, state.submittedAttemptId) {
        if (state.isSubmitted && state.submittedAttemptId != null) {
            onNavigateToResult(state.submittedAttemptId!!)
        }
    }

    // Intercept back button to prompt confirmation
    BackHandler(enabled = state.isExamActive) {
        examViewModel.setConfirmDialogVisible(true)
    }

    val totalQuestions = state.questionsWithOptions.size
    val currentQ = state.questionsWithOptions.getOrNull(state.currentQuestionIndex)
    val answeredCount = state.selectedAnswers.size
    val isMarked = currentQ != null && state.markedForReview.contains(currentQ.question.id)

    // Format timer
    val minutes = state.remainingSeconds / 60
    val seconds = state.remainingSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)
    val isTimeLow = state.remainingSeconds in 1..120

    if (state.isLoading || state.exam == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = EduRankPurple)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("exam_screen")
    ) {
        // 1. Top Bar with Timer and Progress
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.exam?.title ?: "MCQ Examination",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "Answered: $answeredCount of $totalQuestions",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Countdown Timer Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isTimeLow) Color(0xFFFEE2E2) else EduRankPurpleContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("timer_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (isTimeLow) EduRankError else EduRankPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formattedTime,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isTimeLow) EduRankError else EduRankPurple
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (totalQuestions > 0) answeredCount.toFloat() / totalQuestions.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EduRankPurple,
                    trackColor = EduRankPurpleContainer
                )
            }
        }

        // 2. Question View Area
        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Header Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EduRankPurple)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Question ${state.currentQuestionIndex + 1} / $totalQuestions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Mark for Review toggle button
                    OutlinedButton(
                        onClick = { examViewModel.toggleMarkForReview(currentQ.question.id) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("mark_for_review_btn")
                    ) {
                        Icon(
                            imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Review",
                            tint = if (isMarked) EduRankAccent else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMarked) "Marked" else "Review",
                            fontSize = 11.sp,
                            color = if (isMarked) EduRankAccent else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Text Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQ.question.questionText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Marks: ${currentQ.question.marks.toInt()} | Negative: -${state.exam?.negativeMarkingPerWrong ?: 0.25f}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select one answer:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4 Options
                val selectedIdx = state.selectedAnswers[currentQ.question.id]
                val optionLetters = listOf("A", "B", "C", "D")

                currentQ.options.forEachIndexed { idx, opt ->
                    val isSelected = selectedIdx == opt.optionIndex
                    OptionItem(
                        letter = optionLetters.getOrElse(idx) { "${idx + 1}" },
                        text = opt.optionText,
                        isSelected = isSelected,
                        onClick = { examViewModel.selectOption(currentQ.question.id, opt.optionIndex) },
                        testTag = "option_${currentQ.question.id}_$idx"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 3. Bottom Navigation & Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                OutlinedButton(
                    onClick = { examViewModel.prevQuestion() },
                    enabled = state.currentQuestionIndex > 0,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("prev_question_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev", fontSize = 12.sp)
                }

                // Grid Sheet button
                IconButton(
                    onClick = { examViewModel.setPaletteSheetVisible(true) },
                    modifier = Modifier.testTag("questions_grid_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Palette",
                        tint = EduRankPurple
                    )
                }

                // Next or Submit button
                if (state.currentQuestionIndex < totalQuestions - 1) {
                    Button(
                        onClick = { examViewModel.nextQuestion() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                        modifier = Modifier.testTag("next_question_btn")
                    ) {
                        Text("Next", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = { examViewModel.setConfirmDialogVisible(true) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduRankSuccess),
                        modifier = Modifier.testTag("submit_exam_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Submit", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Questions Palette Grid
    if (state.showQuestionsPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { examViewModel.setPaletteSheetVisible(false) },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Questions Navigation Grid",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaletteLegend(color = EduRankSuccess, label = "Answered")
                    PaletteLegend(color = EduRankAccent, label = "Review")
                    PaletteLegend(color = Color.LightGray, label = "Unanswered")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    itemsIndexed(state.questionsWithOptions) { idx, q ->
                        val isAnswered = state.selectedAnswers.containsKey(q.question.id)
                        val isMarkedForReview = state.markedForReview.contains(q.question.id)
                        val isCurrent = idx == state.currentQuestionIndex

                        val (bgColor, textColor) = when {
                            isCurrent -> EduRankPurple to Color.White
                            isMarkedForReview -> EduRankAccent to Color.Black
                            isAnswered -> EduRankSuccess to Color.White
                            else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .border(
                                    width = if (isCurrent) 2.dp else 0.dp,
                                    color = if (isCurrent) EduRankPurpleDark else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { examViewModel.goToQuestion(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        examViewModel.setPaletteSheetVisible(false)
                        examViewModel.setConfirmDialogVisible(true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankSuccess),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Finish & Submit Exam Now")
                }
            }
        }
    }

    // Confirmation Dialog before submission
    if (state.showConfirmSubmitDialog) {
        val unanswered = totalQuestions - answeredCount
        AlertDialog(
            onDismissRequest = { examViewModel.setConfirmDialogVisible(false) },
            title = { Text("Submit Examination?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Are you sure you want to finalize your exam answers?")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Answered: $answeredCount", color = EduRankSuccess, fontWeight = FontWeight.SemiBold)
                    Text("• Unanswered: $unanswered", color = if (unanswered > 0) EduRankError else Color.Gray)
                    Text("• Marked for Review: ${state.markedForReview.size}", color = EduRankAccent)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Once submitted, your answers will be automatically marked and your ranking points calculated.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { examViewModel.submitExam(currentUserId) },
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankSuccess),
                    modifier = Modifier.testTag("confirm_submit_btn")
                ) {
                    Text("Confirm Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { examViewModel.setConfirmDialogVisible(false) }) {
                    Text("Continue Exam")
                }
            }
        )
    }
}

@Composable
fun OptionItem(
    letter: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) EduRankPurple else Color.LightGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EduRankPurpleContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) EduRankPurple else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = EduRankPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PaletteLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}
