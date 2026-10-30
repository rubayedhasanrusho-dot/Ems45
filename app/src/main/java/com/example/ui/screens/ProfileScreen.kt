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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.AchievementEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.UserEntity
import com.example.data.repository.EduRankRepository
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    repository: EduRankRepository,
    onNavigateToResult: (String) -> Unit,
    onLogout: () -> Unit
) {
    val userId = currentUser?.id ?: 1L
    val pastAttempts by repository.getAttemptsForUser(userId).collectAsState(initial = emptyList())
    val achievements by repository.getAchievements().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Student Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(EduRankPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.fullName?.take(1) ?: "T").uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentUser?.fullName ?: "Tanvir Ahmed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = EduRankPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUser?.institution ?: "Notre Dame College, Dhaka",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Badges row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileMetricBadge("Total Points", "${currentUser?.points ?: 2480}", EduRankPurple)
                        ProfileMetricBadge("Class Level", currentUser?.educationLevel ?: "HSC Science", Color(0xFF0284C7))
                        ProfileMetricBadge("National Rank", "#3", EduRankAccent)
                    }
                }
            }
        }

        // 2. Personal & Account Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = currentUser?.email ?: "tanvir@notredame.edu.bd",
                        isVerified = currentUser?.isEmailVerified == true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone Number",
                        value = currentUser?.phone ?: "01712345678",
                        isVerified = currentUser?.isPhoneVerified == true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Account Role",
                        value = currentUser?.role ?: "STUDENT",
                        isVerified = true
                    )
                }
            }
        }

        // 3. Achievements Gallery
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Student Achievements & Medals",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.height(190.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(achievements) { ach ->
                            AchievementItem(achievement = ach)
                        }
                    }
                }
            }
        }

        // 4. Past Examination History
        item {
            Text(
                text = "Past Exam Attempts (${pastAttempts.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(pastAttempts) { attempt ->
            PastAttemptCard(
                attempt = attempt,
                onViewReview = { onNavigateToResult(attempt.id) }
            )
        }

        // 5. Logout Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("profile_logout_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of EduRank", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProfileMetricBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isVerified: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EduRankPurple, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        if (isVerified) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFD1FAE5))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Verified", color = Color(0xFF065F46), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) EduRankPurpleContainer else Color.LightGray.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (achievement.isUnlocked) Icons.Default.MilitaryTech else Icons.Default.Lock,
                contentDescription = achievement.title,
                tint = if (achievement.isUnlocked) EduRankPurple else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (achievement.isUnlocked) EduRankPurple else Color.Gray
            )
            Text(
                text = "+${achievement.pointsReward} pts",
                fontSize = 9.sp,
                color = EduRankAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PastAttemptCard(
    attempt: ExamAttemptEntity,
    onViewReview: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(attempt.endTime))

    Card(
        modifier = Modifier.fillMaxWidth().testTag("past_attempt_${attempt.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attempt.subjectName,
                    fontSize = 11.sp,
                    color = EduRankPurple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = attempt.examTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Score: %.2f / %.0f".format(attempt.score, attempt.totalMarks),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (attempt.isPassed) EduRankSuccess else Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${attempt.accuracyPercentage.toInt()}% Acc)",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = onViewReview,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("review_attempt_btn_${attempt.id}")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Review", fontSize = 11.sp)
                }
            }
        }
    }
}
