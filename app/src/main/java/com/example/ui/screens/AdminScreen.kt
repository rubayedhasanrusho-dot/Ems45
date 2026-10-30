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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.EduRankPurpleDark
import com.example.ui.theme.EduRankSuccess
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by adminViewModel.uiState.collectAsState()
    val tabs = listOf("Overview", "Manage Exams", "Add MCQ", "Creative Grading", "Bulk Import")

    var showCreateExamDialog by remember { mutableStateOf(false) }
    var showGradeDialog by remember { mutableStateOf<CreativeSubmissionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_screen")
    ) {
        // App bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EduRank Academic Admin Panel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankPurple
                        )
                    }
                    Text(
                        text = "National Board Examination & Evaluation Portal",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EduRankPurple,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { adminViewModel.setSelectedTab(index) },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("admin_tab_$index")
                )
            }
        }

        // Message banner
        state.message?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = EduRankPurpleContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = msg, fontSize = 12.sp, color = EduRankPurpleDark, modifier = Modifier.weight(1f))
                    IconButton(onClick = { adminViewModel.clearMessage() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Check, contentDescription = "Dismiss", tint = EduRankPurple)
                    }
                }
            }
        }

        // Tab Content
        when (state.selectedTab) {
            0 -> AdminOverviewTab(state = state)
            1 -> AdminExamsTab(
                exams = state.exams,
                onTogglePublish = { adminViewModel.togglePublish(it) },
                onDelete = { adminViewModel.deleteExam(it) },
                onCreateNew = { showCreateExamDialog = true }
            )
            2 -> AdminAddMcqTab(
                exams = state.exams,
                onAddQuestion = { eId, sId, num, q, opts, corr, exp ->
                    adminViewModel.addMcqQuestion(eId, sId, num, q, opts, corr, exp)
                }
            )
            3 -> AdminCreativeGradingTab(
                submissions = state.creativeSubmissions,
                onSelectToGrade = { showGradeDialog = it }
            )
            4 -> AdminBulkImportTab(
                exams = state.exams,
                onImport = { examId, subjectId, csv -> adminViewModel.importCsv(examId, subjectId, csv) },
                onAnnouncement = { t, c, cat -> adminViewModel.postAnnouncement(t, c, cat) }
            )
        }
    }

    // Create Exam Dialog
    if (showCreateExamDialog) {
        CreateExamDialog(
            subjects = state.subjects,
            onDismiss = { showCreateExamDialog = false },
            onCreate = { title, sId, cId, type, qCount, duration, marks, pass, neg ->
                adminViewModel.createExam(title, sId, cId, type, qCount, duration, marks, pass, neg)
                showCreateExamDialog = false
            }
        )
    }

    // Creative Grading Dialog
    showGradeDialog?.let { sub ->
        GradeSubmissionDialog(
            submission = sub,
            onDismiss = { showGradeDialog = null },
            onSaveGrade = { mA, mB, mC, mD, fb ->
                adminViewModel.evaluateCreative(sub.id, mA, mB, mC, mD, fb)
                showGradeDialog = null
            }
        )
    }
}

@Composable
fun AdminOverviewTab(state: com.example.ui.viewmodel.AdminUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard("Registered Students", "${state.students.size}", Icons.Default.People, EduRankPurple, Modifier.weight(1f))
                AdminMetricCard("Curriculum Subjects", "${state.subjects.size}", Icons.Default.School, Color(0xFF0284C7), Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard("Exams Created", "${state.exams.size}", Icons.Default.MenuBook, EduRankAccent, Modifier.weight(1f))
                AdminMetricCard("Creative Papers", "${state.creativeSubmissions.size}", Icons.Default.RateReview, EduRankSuccess, Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = "Registered Students Directory",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(state.students) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EduRankPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.take(1).uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduRankPurple
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = student.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${student.institution} • ${student.educationLevel}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        text = "${student.points} pts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduRankPurple
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AdminExamsTab(
    exams: List<ExamEntity>,
    onTogglePublish: (ExamEntity) -> Unit,
    onDelete: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onCreateNew,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("create_exam_open_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create New Exam (MCQ or Creative)", fontWeight = FontWeight.Bold)
            }
        }

        items(exams) { exam ->
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (exam.isPublished) Color(0xFFD1FAE5) else Color(0xFFFEE2E2))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (exam.isPublished) "PUBLISHED" else "DRAFT / UNPUBLISHED",
                                color = if (exam.isPublished) Color(0xFF065F46) else Color(0xFF991B1B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row {
                            IconButton(onClick = { onTogglePublish(exam) }) {
                                Icon(
                                    imageVector = if (exam.isPublished) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle",
                                    tint = EduRankPurple
                                )
                            }
                            IconButton(onClick = { onDelete(exam.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }

                    Text(text = exam.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${exam.examType} • ${exam.durationMinutes} mins • ${exam.totalQuestions} questions • ${exam.totalMarks.toInt()} Marks (Pass: ${exam.passMarks.toInt()})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAddMcqTab(
    exams: List<ExamEntity>,
    onAddQuestion: (examId: String, subjectId: String, qNumber: Int, qText: String, options: List<String>, correctIndex: Int, explanation: String) -> Unit
) {
    var selectedExamId by remember { mutableStateOf(exams.firstOrNull()?.id ?: "") }
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }

    val selectedExam = exams.find { it.id == selectedExamId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "Add MCQ Question to Exam Bank", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Text(text = "Target Exam: ${selectedExam?.title ?: "Select Exam"}", fontSize = 12.sp, color = EduRankPurple, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question Text (Bengali or English)") },
                modifier = Modifier.fillMaxWidth().testTag("admin_q_text_input")
            )
        }

        item {
            OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
        }
        item {
            OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Correct Option Index: ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                listOf("A (0)", "B (1)", "C (2)", "D (3)").forEachIndexed { idx, label ->
                    Button(
                        onClick = { correctIndex = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (correctIndex == idx) EduRankSuccess else Color.LightGray
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(label, fontSize = 11.sp, color = if (correctIndex == idx) Color.White else Color.Black)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                label = { Text("Detailed Explanation / সূত্র ও ব্যাখ্যা") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (selectedExamId.isNotBlank() && questionText.isNotBlank()) {
                        onAddQuestion(
                            selectedExamId,
                            selectedExam?.subjectId ?: "hsc_phy_1",
                            (selectedExam?.totalQuestions ?: 25) + 1,
                            questionText,
                            listOf(optionA, optionB, optionC, optionD),
                            correctIndex,
                            explanation
                        )
                        questionText = ""
                        optionA = ""
                        optionB = ""
                        optionC = ""
                        optionD = ""
                        explanation = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_save_q_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Question to Bank")
            }
        }
    }
}

@Composable
fun AdminCreativeGradingTab(
    submissions: List<CreativeSubmissionEntity>,
    onSelectToGrade: (CreativeSubmissionEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Student Creative Submissions (${submissions.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(submissions) { sub ->
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
                        Text(text = sub.studentName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (sub.status == "EVALUATED") Color(0xFFD1FAE5) else Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = sub.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sub.status == "EVALUATED") Color(0xFF065F46) else Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${sub.examTitle} • ${sub.subjectName}", fontSize = 12.sp, color = Color.Gray)

                    if (sub.status == "EVALUATED") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Awarded Marks: ${sub.totalAwardedMarks ?: 0f}/10.0 • Remarks: ${sub.teacherFeedback ?: ""}",
                            fontSize = 12.sp,
                            color = EduRankPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onSelectToGrade(sub) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sub.status == "EVALUATED") EduRankPurple else EduRankAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (sub.status == "EVALUATED") "Edit Grade" else "Grade Submission", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBulkImportTab(
    exams: List<ExamEntity>,
    onImport: (examId: String, subjectId: String, csv: String) -> Unit,
    onAnnouncement: (title: String, content: String, category: String) -> Unit
) {
    var selectedExamId by remember { mutableStateOf(exams.firstOrNull()?.id ?: "") }
    var csvText by remember {
        mutableStateOf(
            """Question|OptionA|OptionB|OptionC|OptionD|CorrectIndex|Explanation
বায়ুর আর্দ্রতা বৃদ্ধি পেলে শব্দের বেগের কী পরিবর্তন ঘটে?|হ্রাস পায়|বৃদ্ধি পায়|অপরিবর্তিত থাকে|শূন্য হয়|1|জলীয় বাষ্পের ঘনত্ব শুষ্ক বায়ুর ঘনত্বের চেয়ে কম হওয়ায় শব্দের বেগ বাড়ে।
স্থির তরঙ্গে পরপর দুটি নিস্পন্দ বিন্দুর মধ্যবর্তী দূরত্ব কত?|λ|λ/2|λ/4|2λ|1|পরপর দুটি নিস্পন্দ বিন্দুর দূরত্ব হলো λ/2।
টর্কের মাত্রা সমীকরণ কোনটির সমান?|বলের|কাজের|ক্ষমতার|ভরবেগের|1|টর্কের মাত্রা [ML²T⁻²] যা কাজের মাত্রার সমান।"""
        )
    }

    var annTitle by remember { mutableStateOf("") }
    var annContent by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "📥 Bulk MCQ Question Import", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Format: Question | OptionA | OptionB | OptionC | OptionD | CorrectIndex(0-3) | Explanation",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = csvText,
                        onValueChange = { csvText = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val exam = exams.find { it.id == selectedExamId } ?: exams.firstOrNull()
                            if (exam != null) {
                                onImport(exam.id, exam.subjectId, csvText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import Questions into Question Bank")
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "📢 Post Announcement to Students", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = annTitle, onValueChange = { annTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = annContent, onValueChange = { annContent = it }, label = { Text("Announcement Details") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (annTitle.isNotBlank()) {
                                onAnnouncement(annTitle, annContent, "Exam Alert")
                                annTitle = ""
                                annContent = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EduRankSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish Announcement")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateExamDialog(
    subjects: List<com.example.data.model.SubjectEntity>,
    onDismiss: () -> Unit,
    onCreate: (title: String, subjectId: String, categoryId: String, type: String, qCount: Int, duration: Int, marks: Float, passMarks: Float, neg: Float) -> Unit
) {
    var title by remember { mutableStateOf("HSC Physics 2nd Paper - Final Model Test") }
    var duration by remember { mutableStateOf("25") }
    var questionsCount by remember { mutableStateOf("25") }
    var passMarks by remember { mutableStateOf("10") }
    var negativeMarking by remember { mutableStateOf("0.25") }
    var examType by remember { mutableStateOf("MCQ") }
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "hsc_phy_1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Exam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Exam Title") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { examType = "MCQ" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (examType == "MCQ") EduRankPurple else Color.LightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("MCQ", color = if (examType == "MCQ") Color.White else Color.Black)
                    }
                    Button(
                        onClick = { examType = "CREATIVE" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (examType == "CREATIVE") EduRankPurple else Color.LightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Creative", color = if (examType == "CREATIVE") Color.White else Color.Black)
                    }
                }
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (Minutes)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = questionsCount, onValueChange = { questionsCount = it }, label = { Text("Total Questions") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = passMarks, onValueChange = { passMarks = it }, label = { Text("Passing Marks") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val d = duration.toIntOrNull() ?: 25
                    val q = questionsCount.toIntOrNull() ?: 25
                    val p = passMarks.toFloatOrNull() ?: 10f
                    val neg = negativeMarking.toFloatOrNull() ?: 0.25f
                    onCreate(title, selectedSubjectId, "HSC", examType, q, d, q.toFloat(), p, neg)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EduRankPurple)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GradeSubmissionDialog(
    submission: CreativeSubmissionEntity,
    onDismiss: () -> Unit,
    onSaveGrade: (mA: Float, mB: Float, mC: Float, mD: Float, feedback: String) -> Unit
) {
    var marksA by remember { mutableStateOf(submission.marksA?.toString() ?: "1.0") }
    var marksB by remember { mutableStateOf(submission.marksB?.toString() ?: "2.0") }
    var marksC by remember { mutableStateOf(submission.marksC?.toString() ?: "3.0") }
    var marksD by remember { mutableStateOf(submission.marksD?.toString() ?: "3.5") }
    var feedback by remember { mutableStateOf(submission.teacherFeedback ?: "চমৎকার ও স্পষ্ট উপস্থাপন!") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Grade Student Written Answers") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Student: ${submission.studentName}", fontWeight = FontWeight.Bold, color = EduRankPurple)
                Text("Answer A: ${submission.answerA.take(60)}...", fontSize = 11.sp, color = Color.Gray)
                OutlinedTextField(value = marksA, onValueChange = { marksA = it }, label = { Text("Marks for 'ক' (Out of 1.0)") })
                OutlinedTextField(value = marksB, onValueChange = { marksB = it }, label = { Text("Marks for 'খ' (Out of 2.0)") })
                OutlinedTextField(value = marksC, onValueChange = { marksC = it }, label = { Text("Marks for 'গ' (Out of 3.0)") })
                OutlinedTextField(value = marksD, onValueChange = { marksD = it }, label = { Text("Marks for 'ঘ' (Out of 4.0)") })
                OutlinedTextField(value = feedback, onValueChange = { feedback = it }, label = { Text("Teacher Remarks / Feedback") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mA = marksA.toFloatOrNull() ?: 1f
                    val mB = marksB.toFloatOrNull() ?: 2f
                    val mC = marksC.toFloatOrNull() ?: 3f
                    val mD = marksD.toFloatOrNull() ?: 3.5f
                    onSaveGrade(mA, mB, mC, mD, feedback)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EduRankSuccess)
            ) {
                Text("Publish Grade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
