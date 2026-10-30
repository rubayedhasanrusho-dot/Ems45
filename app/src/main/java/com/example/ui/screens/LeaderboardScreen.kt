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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardItem
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankPurpleDark
import com.example.ui.theme.EduRankPurpleLight
import com.example.ui.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    currentUserId: Long,
    leaderboardViewModel: LeaderboardViewModel
) {
    val state by leaderboardViewModel.uiState.collectAsState()

    val filters = listOf(
        "GLOBAL" to "National (All)",
        "INSTITUTION" to "My College (NDC)",
        "HSC" to "HSC Candidates",
        "SSC" to "SSC Candidates",
        "WEEKLY" to "This Week",
        "MONTHLY" to "This Month"
    )

    val top1 = state.leaders.getOrNull(0)
    val top2 = state.leaders.getOrNull(1)
    val top3 = state.leaders.getOrNull(2)
    val remaining = if (state.leaders.size > 3) state.leaders.drop(3) else emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("leaderboard_screen")
    ) {
        // Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { (key, label) ->
                val isSelected = state.selectedFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { leaderboardViewModel.setFilter(key, currentUserId) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EduRankPurple,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("leaderboard_filter_$key")
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Top 3 Podium Card
            if (top1 != null) {
                item {
                    PodiumCard(top1 = top1, top2 = top2, top3 = top3)
                }
            }

            // 2. Rankings Header
            item {
                Text(
                    text = "Rankings List",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 3. Ranked List Items
            items(remaining) { item ->
                LeaderboardRow(item = item)
            }
        }

        // Persistent My Rank Bottom Bar
        state.currentUserItem?.let { me ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EduRankPurpleContainer,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(EduRankPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${me.rank}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${me.name} (You)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankPurpleDark
                        )
                        Text(
                            text = "${me.institution} • Accuracy: ${me.accuracy.toInt()}%",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "${me.points} pts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EduRankPurple
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumCard(
    top1: LeaderboardItem,
    top2: LeaderboardItem?,
    top3: LeaderboardItem?
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("podium_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Top Achievers",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EduRankPurple
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                if (top2 != null) {
                    PodiumPillar(
                        item = top2,
                        rank = 2,
                        pillarHeight = 90.dp,
                        color = Color(0xFF94A3B8),
                        badgeText = "2nd"
                    )
                }

                // 1st Place (Gold)
                PodiumPillar(
                    item = top1,
                    rank = 1,
                    pillarHeight = 120.dp,
                    color = EduRankAccent,
                    badgeText = "1st"
                )

                // 3rd Place (Bronze)
                if (top3 != null) {
                    PodiumPillar(
                        item = top3,
                        rank = 3,
                        pillarHeight = 75.dp,
                        color = Color(0xFFD97706),
                        badgeText = "3rd"
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumPillar(
    item: LeaderboardItem,
    rank: Int,
    pillarHeight: androidx.compose.ui.unit.Dp,
    color: Color,
    badgeText: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        // Avatar with crown / medal
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(if (rank == 1) 54.dp else 46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.take(1).uppercase(),
                    fontSize = if (rank == 1) 20.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.name.split(" ").firstOrNull() ?: item.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${item.points} pts",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = EduRankPurple
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pillar block
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(pillarHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun LeaderboardRow(item: LeaderboardItem) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("leaderboard_row_${item.rank}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCurrentUser) EduRankPurpleContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${item.rank}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.rank <= 3) EduRankAccent else Color.Gray,
                modifier = Modifier.width(36.dp)
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EduRankPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.take(1).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduRankPurple
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name + if (item.isCurrentUser) " (You)" else "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.institution,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.points} pts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EduRankPurple
                )
                Text(
                    text = "${item.examsCompleted} exams",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
