package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ExamEntity
import com.example.data.model.PerformanceStatus
import com.example.data.model.UserEntity
import com.example.ui.components.PerformanceStatusBadge
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankPurpleDark
import com.example.ui.theme.EduRankPurpleLight
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    currentUser: UserEntity?,
    dashboardViewModel: DashboardViewModel,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToExam: (String) -> Unit,
    onNavigateToCreativeExam: (String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val stats = uiState.studentStats

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("welcome_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(EduRankPurple, EduRankPurpleLight)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser?.fullName?.take(1) ?: "T").uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome back,",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = currentUser?.fullName ?: "Tanvir Ahmed",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentUser?.institution ?: "Notre Dame College, Dhaka",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            // Points Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Points",
                                        tint = EduRankAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${currentUser?.points ?: 2480} pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EduRankPurple
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Level: ${currentUser?.educationLevel ?: "HSC - Science"}",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            PerformanceStatusBadge(
                                status = stats?.status ?: PerformanceStatus.EXCELLENT
                            )
                        }
                    }
                }
            }
        }

        // 2. Announcements Carousel
        if (uiState.announcements.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EduRankPurpleContainer)
                ) {
                    val ann = uiState.announcements.first()
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EduRankPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ann.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EduRankPurpleDark
                            )
                            Text(
                                text = ann.content,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // 3. Performance Overview Header & Grid
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Performance Overview",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "View Analytics →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EduRankPurple,
                        modifier = Modifier
                            .clickable { onNavigateToAnalytics() }
                            .testTag("dashboard_view_analytics_btn")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2x3 Grid of Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Exams",
                        value = "${stats?.totalExams ?: 3}",
                        icon = Icons.Default.MenuBook,
                        tint = EduRankPurple,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Questions Answered",
                        value = "${stats?.totalQuestionsAnswered ?: 74}",
                        icon = Icons.Default.CheckCircle,
                        tint = EduRankSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Average Score",
                        value = "%.1f".format(stats?.averageScore ?: 21.3f),
                        icon = Icons.Default.Speed,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Highest Score",
                        value = "%.1f".format(stats?.highestScore ?: 23.8f),
                        icon = Icons.Default.WorkspacePremium,
                        tint = EduRankAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Correct / Wrong",
                        value = "${stats?.totalCorrect ?: 66} / ${stats?.totalWrong ?: 8}",
                        icon = Icons.Default.Star,
                        tint = EduRankSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Overall Accuracy",
                        value = "%.1f%%".format(stats?.overallAccuracyPercentage ?: 89.2f),
                        icon = Icons.Default.TrendingUp,
                        tint = EduRankPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Strongest & Weakest Subjects Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💪 Strongest Subject",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stats?.strongestSubject ?: "Physics 1st Paper",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankSuccess
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "🎯 Needs Attention",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stats?.weakestSubject ?: "Chemistry 1st Paper",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankError
                        )
                    }
                }
            }
        }

        // 5. Education Categories Cards
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Education Categories",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "See All Subjects →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EduRankPurple,
                        modifier = Modifier.clickable { onNavigateToCategory("HSC") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onNavigateToCategory(category.id) }
                        )
                    }
                }
            }
        }

        // 6. Featured Model Tests
        item {
            Column {
                Text(
                    text = "Live Model Examinations",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                uiState.popularExams.take(3).forEach { exam ->
                    ExamItemCard(
                        exam = exam,
                        onStartExam = {
                            if (exam.examType == "CREATIVE") {
                                onNavigateToCreativeExam(exam.id)
                            } else {
                                onNavigateToExam(exam.id)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 7. National Leaderboard Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLeaderboard() }
                    .testTag("dashboard_leaderboard_card"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = EduRankAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "National EduRank Leaderboard",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View",
                            tint = EduRankPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.topLeaderboard.take(3).forEach { leader ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${leader.rank}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (leader.rank == 1) EduRankAccent else EduRankPurple,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = leader.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${leader.points} pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduRankPurple
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EduRankPurpleContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = category.name,
                    tint = EduRankPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = category.id,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EduRankPurple
            )
            Text(
                text = category.name.substringAfter("(").substringBefore(")").ifEmpty { category.name },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun ExamItemCard(
    exam: ExamEntity,
    onStartExam: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("exam_item_${exam.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (exam.examType == "CREATIVE") Color(0xFFFEF3C7) else EduRankPurpleContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (exam.examType == "CREATIVE") "সৃজনশীল (Creative)" else "MCQ Examination",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (exam.examType == "CREATIVE") Color(0xFFB45309) else EduRankPurple
                    )
                }

                Text(
                    text = "${exam.durationMinutes} mins • ${exam.totalQuestions} questions",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exam.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${exam.totalMarks.toInt()} Marks (Pass: ${exam.passMarks.toInt()})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Button(
                    onClick = onStartExam,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Start Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
