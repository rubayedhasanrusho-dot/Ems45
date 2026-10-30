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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.UserEntity
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.CreativeExamViewModel

@Composable
fun CreativeExamScreen(
    examId: String,
    currentUser: UserEntity?,
    creativeViewModel: CreativeExamViewModel,
    onBack: () -> Unit
) {
    val state by creativeViewModel.uiState.collectAsState()

    LaunchedEffect(examId) {
        creativeViewModel.loadCreativeExam(examId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("creative_exam_screen")
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("creative_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "সৃজনশীল পরীক্ষা (Creative Examination)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.exam?.title ?: "Model Test",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduRankPurple)
            }
            return
        }

        if (state.isSubmitted) {
            // Submitted State View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = EduRankSuccess,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Creative Paper Submitted!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduRankPurple
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.message ?: "Your answers have been queued for evaluation by our panel of lecturers.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to Subject")
                }
            }
            return
        }

        val q = state.question
        if (q != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stimulus / উদ্দীপক Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF59E0B))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("উদ্দীপক (Stimulus)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("পূর্ণমান: ১০", fontSize = 12.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = q.stimulus,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Part A (Knowledge - 1 mark)
                item {
                    CreativeAnswerSection(
                        title = q.partA.ifEmpty { "ক) জ্ঞানমূলক প্রশ্ন (১ নম্বর)" },
                        value = state.answerA,
                        onValueChange = { creativeViewModel.updateAnswerA(it) },
                        placeholder = "আপনার উত্তর লিখুন...",
                        marks = "১",
                        testTag = "answer_a_input"
                    )
                }

                // Part B (Comprehension - 2 marks)
                item {
                    CreativeAnswerSection(
                        title = q.partB.ifEmpty { "খ) অনুধাবনমূলক প্রশ্ন (২ নম্বর)" },
                        value = state.answerB,
                        onValueChange = { creativeViewModel.updateAnswerB(it) },
                        placeholder = "ব্যাখ্যাসহ উত্তর লিখুন...",
                        marks = "২",
                        testTag = "answer_b_input"
                    )
                }

                // Part C (Application - 3 marks)
                item {
                    CreativeAnswerSection(
                        title = q.partC.ifEmpty { "গ) প্রয়োগমূলক প্রশ্ন (৩ নম্বর)" },
                        value = state.answerC,
                        onValueChange = { creativeViewModel.updateAnswerC(it) },
                        placeholder = "গাণিতিক সূত্র ও ধাপসমূহ বিস্তারিত লিখুন...",
                        marks = "৩",
                        testTag = "answer_c_input"
                    )
                }

                // Part D (Higher Ability - 4 marks)
                item {
                    CreativeAnswerSection(
                        title = q.partD.ifEmpty { "ঘ) উচ্চতর দক্ষতামূলক প্রশ্ন (৪ নম্বর)" },
                        value = state.answerD,
                        onValueChange = { creativeViewModel.updateAnswerD(it) },
                        placeholder = "গাণিতিক বিশ্লেষণ ও যুক্তিপূর্ণ মতামত লিখুন...",
                        marks = "৪",
                        testTag = "answer_d_input"
                    )
                }

                // Submit Button
                item {
                    Button(
                        onClick = {
                            creativeViewModel.submitCreativeAnswers(
                                userId = currentUser?.id ?: 1L,
                                studentName = currentUser?.fullName ?: "Tanvir Ahmed"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_creative_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Written Answers for Evaluation", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun CreativeAnswerSection(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    marks: String,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduRankPurple,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${value.length} chars",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag(testTag),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}
