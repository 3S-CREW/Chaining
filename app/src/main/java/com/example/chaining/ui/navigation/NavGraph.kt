package com.example.chaining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.ui.login.LoginScreen
import com.example.chaining.ui.screen.AreaScreen
import com.example.chaining.ui.screen.CommunityScreen
import com.example.chaining.ui.screen.CreatePostScreen
import com.example.chaining.ui.screen.ENQuizScreen
import com.example.chaining.ui.screen.HomeScreen
import com.example.chaining.ui.screen.JoinPostScreen
import com.example.chaining.ui.screen.KRQuizScreen
import com.example.chaining.ui.screen.MainHomeScreen
import com.example.chaining.ui.screen.MyPageScreen
import com.example.chaining.ui.screen.QuizResultScreen
import com.example.chaining.ui.screen.SplashScreen
import com.example.chaining.ui.screen.ViewPostScreen
import com.google.gson.Gson

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onTableClick = { navController.navigate("area") },
                onMyPageClick = { navController.navigate("myPage") },
                onMainHomeClick = { navController.navigate("mainHome") },
                onCreatePostClick = { navController.navigate("createPost") },
                onJoinPostClick = { navController.navigate("joinPost") },
                onCommunityClick = { navController.navigate("community") },
                onKRQuizClick = { navController.navigate("krQuiz") },
                onENQuizClick = { navController.navigate("enQuiz") }
            )
        }
        composable(Screen.Area.route) {
            AreaScreen()
        }
        composable(Screen.MyPage.route) {
            MyPageScreen()
        }
        composable(Screen.MainHome.route) {
            MainHomeScreen()
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen()
        }
        composable(Screen.Community.route) {
            CommunityScreen(
                onBackClick = { navController.popBackStack() },
                onViewPostClick = { postId ->
                    navController.navigate(Screen.ViewPost.createRoute(postId))
                }
            )
        }

        composable(
            route = Screen.ViewPost.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            ViewPostScreen(onJoinPostClick = { post ->
                navController.navigate(Screen.JoinPost.createRoute(post))
            })
        }

        composable(
            route = Screen.JoinPost.route,
            arguments = listOf(navArgument("post") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("post")
            json?.let {
                val post = remember(it) { Gson().fromJson(it, RecruitPost::class.java) }
                JoinPostScreen(post = post)
            }
        }

        composable("enQuiz") {
            ENQuizScreen(
                // 퀴즈 종료 시 "quiz_result" 경로로 이동
                onNavigateToResult = {
                    navController.navigate("quizResult") {
                        // 퀴즈 화면은 뒤로가기 스택에서 제거
                        popUpTo("enQuiz") { inclusive = true }
                    }
                }
            )
        }

        composable("krquiz") {
            KRQuizScreen(
                onNavigateToResult = {
                    navController.navigate("quizResult") {
                        // 퀴즈 화면은 뒤로가기 스택에서 제거
                        popUpTo("krQuiz") { inclusive = true }
                    }
                }
            )
        }

        composable("quizResult") {
            QuizResultScreen()
        }
    }
}
