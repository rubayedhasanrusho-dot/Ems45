package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Dashboard : Screen("dashboard")
    object Categories : Screen("categories")
    object SubjectDetail : Screen("subject_detail/{subjectId}") {
        fun createRoute(subjectId: String) = "subject_detail/$subjectId"
    }
    object Exam : Screen("exam/{examId}") {
        fun createRoute(examId: String) = "exam/$examId"
    }
    object CreativeExam : Screen("creative_exam/{examId}") {
        fun createRoute(examId: String) = "creative_exam/$examId"
    }
    object Result : Screen("result/{attemptId}") {
        fun createRoute(attemptId: String) = "result/$attemptId"
    }
    object Leaderboard : Screen("leaderboard")
    object Analytics : Screen("analytics")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
}
