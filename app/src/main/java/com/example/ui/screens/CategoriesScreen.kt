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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectEntity
import com.example.data.repository.EduRankRepository
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer

@Composable
fun CategoriesScreen(
    repository: EduRankRepository,
    initialCategoryId: String = "HSC",
    onSelectSubject: (String) -> Unit
) {
    val categories by repository.getAllCategories().collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf(initialCategoryId) }
    var selectedGroupIndex by remember { mutableIntStateOf(0) } // 0: Science, 1: Business, 2: Humanities, 3: All

    val subjects by repository.getSubjectsByCategory(selectedCategory).collectAsState(initial = emptyList())

    val groups = if (selectedCategory == "HSC") {
        listOf("Science", "Business Studies", "Humanities", "All Subjects")
    } else {
        listOf("All Subjects")
    }

    val filteredSubjects = if (selectedCategory == "HSC" && selectedGroupIndex < 3) {
        val targetGroup = groups[selectedGroupIndex]
        subjects.filter { it.groupName == targetGroup }
    } else {
        subjects
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("categories_screen")
    ) {
        // Education Levels Chip Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat.id
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedCategory = cat.id
                        selectedGroupIndex = 0
                    },
                    label = { Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EduRankPurple,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_${cat.id}")
                )
            }
        }

        // Sub-group Tab Row for HSC
        if (selectedCategory == "HSC") {
            ScrollableTabRow(
                selectedTabIndex = selectedGroupIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EduRankPurple,
                edgePadding = 16.dp
            ) {
                groups.forEachIndexed { index, groupName ->
                    Tab(
                        selected = selectedGroupIndex == index,
                        onClick = { selectedGroupIndex = index },
                        text = { Text(groupName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }

        // Subject List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "$selectedCategory Curriculum Subjects (${filteredSubjects.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(filteredSubjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onSelectSubject(subject.id) }
                )
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(subject.colorHex))
    } catch (e: Exception) {
        EduRankPurple
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = subject.name,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subject.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${subject.code} • ${subject.groupName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${subject.totalChapters} Chapters",
                        fontSize = 11.sp,
                        color = EduRankPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = EduRankPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
