package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.repository.EduRankRepository
import com.example.ui.components.EduRankBottomNav
import com.example.ui.components.EduRankTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CreativeExamScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.theme.EduRankTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AnalyticsViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CreativeExamViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.ExamViewModel
import com.example.ui.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = EduRankRepository(database)

        // Seed initial Bangladeshi curriculum database in background
        lifecycleScope.launch {
            DatabaseInitializer.populateInitialData(database)
        }

        setContent {
            EduRankTheme {
                EduRankApp(repository = repository)
            }
        }
    }
}

@Composable
fun EduRankApp(repository: EduRankRepository) {
    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current
    val firebaseAuthService = remember { com.example.data.remote.FirebaseAuthService(context) }
    val authViewModel = remember { AuthViewModel(repository, firebaseAuthService) }
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    val currentUserId = currentUser?.id ?: 1L

    val dashboardViewModel = remember(currentUserId) { DashboardViewModel(repository, currentUserId) }
    val examViewModel = remember { ExamViewModel(repository) }
    val creativeViewModel = remember { CreativeExamViewModel(repository) }
    val leaderboardViewModel = remember(currentUserId) { LeaderboardViewModel(repository, currentUserId) }
    val analyticsViewModel = remember(currentUserId) { AnalyticsViewModel(repository, currentUserId) }
    val adminViewModel = remember { AdminViewModel(repository) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation & top bar in Exam, CreativeExam, and Auth
    val isExamRoute = currentRoute?.startsWith("exam/") == true || currentRoute?.startsWith("creative_exam/") == true
    val isAuthRoute = currentRoute == Screen.Auth.route
    val showNavigationBars = !isExamRoute && !isAuthRoute

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showNavigationBars && currentUser != null) {
                EduRankTopBar(
                    currentUser = currentUser,
                    onOpenAdmin = { navController.navigate(Screen.Admin.route) },
                    onSwitchUser = { newUserId ->
                        authViewModel.switchUser(newUserId)
                        dashboardViewModel.loadDashboardData(newUserId)
                        analyticsViewModel.loadAnalytics(newUserId)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showNavigationBars && currentUser != null) {
                EduRankBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (currentUser != null) Screen.Dashboard.route else Screen.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    authViewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    currentUser = currentUser,
                    dashboardViewModel = dashboardViewModel,
                    onNavigateToCategory = { catId ->
                        navController.navigate(Screen.Categories.route)
                    },
                    onNavigateToExam = { examId ->
                        navController.navigate(Screen.Exam.createRoute(examId))
                    },
                    onNavigateToCreativeExam = { examId ->
                        navController.navigate(Screen.CreativeExam.createRoute(examId))
                    },
                    onNavigateToLeaderboard = {
                        navController.navigate(Screen.Leaderboard.route)
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route)
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    repository = repository,
                    initialCategoryId = "HSC",
                    onSelectSubject = { subjectId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                    }
                )
            }

            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStack ->
                val subjectId = backStack.arguments?.getString("subjectId") ?: "hsc_phy_1"
                SubjectDetailScreen(
                    subjectId = subjectId,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onStartExam = { examId ->
                        navController.navigate(Screen.Exam.createRoute(examId))
                    },
                    onStartCreativeExam = { examId ->
                        navController.navigate(Screen.CreativeExam.createRoute(examId))
                    }
                )
            }

            composable(
                route = Screen.Exam.route,
                arguments = listOf(navArgument("examId") { type = NavType.StringType })
            ) { backStack ->
                val examId = backStack.arguments?.getString("examId") ?: "exam_phy1_mcq_board"
                ExamScreen(
                    examId = examId,
                    currentUserId = currentUserId,
                    examViewModel = examViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToResult = { attemptId ->
                        navController.navigate(Screen.Result.createRoute(attemptId)) {
                            popUpTo(Screen.Exam.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.CreativeExam.route,
                arguments = listOf(navArgument("examId") { type = NavType.StringType })
            ) { backStack ->
                val examId = backStack.arguments?.getString("examId") ?: "exam_phy1_creative_1"
                CreativeExamScreen(
                    examId = examId,
                    currentUser = currentUser,
                    creativeViewModel = creativeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Result.route,
                arguments = listOf(navArgument("attemptId") { type = NavType.StringType })
            ) { backStack ->
                val attemptId = backStack.arguments?.getString("attemptId") ?: ""
                ResultScreen(
                    attemptId = attemptId,
                    examViewModel = examViewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    },
                    onNavigateLeaderboard = {
                        navController.navigate(Screen.Leaderboard.route)
                    },
                    onRetakeExam = { examId ->
                        navController.navigate(Screen.Exam.createRoute(examId)) {
                            popUpTo(Screen.Result.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    currentUserId = currentUserId,
                    leaderboardViewModel = leaderboardViewModel
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    currentUserId = currentUserId,
                    analyticsViewModel = analyticsViewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    repository = repository,
                    onNavigateToResult = { attemptId ->
                        navController.navigate(Screen.Result.createRoute(attemptId))
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Admin.route) {
                AdminScreen(
                    adminViewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
