package com.example.chaining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chaining.ui.login.LoginScreen
import com.example.chaining.ui.screen.AreaScreen
import com.example.chaining.ui.screen.CommunityScreen
import com.example.chaining.ui.screen.CreatePostScreen
import com.example.chaining.ui.screen.HomeScreen
import com.example.chaining.ui.screen.JoinPostScreen
import com.example.chaining.ui.screen.MainHomeScreen
import com.example.chaining.ui.screen.MyPageScreen
import com.example.chaining.ui.screen.SplashScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onTableClick = { navController.navigate(Screen.Area.route) },
                onMyPageClick = { navController.navigate(Screen.MyPage.route) },
                onMainHomeClick = { navController.navigate(Screen.MainHome.route) },
                onCreatePostClick = { navController.navigate(Screen.CreatePost.route) },
                onJoinPostClick = { navController.navigate(Screen.JoinPost.route) },
                onCommunityClick = { navController.navigate(Screen.Community.route) }
            )
        }
        composable("area") {
            AreaScreen()
        }
        composable("myPage") {
            MyPageScreen()
        }
        composable("mainHome") {
            MainHomeScreen()
        }
        composable("createPost") {
            CreatePostScreen()
        }
        composable("joinPost") {
            JoinPostScreen()
        }
        composable("community") {
            CommunityScreen(
                onBackClick = { navController.popBackStack() },
                onViewPostClick = { navController.navigate(Screen.ViewPost.route) }
            )
        }
    }
}
