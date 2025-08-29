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
import com.example.chaining.ui.screen.MainHomeScreen
import com.example.chaining.ui.screen.MyPageScreen
import com.example.chaining.ui.screen.SplashScreen
import com.example.chaining.ui.screen.ViewPostScreen

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
                onTableClick = { navController.navigate(Screen.Area.route) },
                onMyPageClick = { navController.navigate(Screen.MyPage.route) },
                onMainHomeClick = { navController.navigate(Screen.MainHome.route) },
                onCreatePostClick = { navController.navigate(Screen.CreatePost.route) },
                onJoinPostClick = { navController.navigate(Screen.JoinPost.route) },
                onCommunityClick = { navController.navigate(Screen.Community.route) }
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
//        composable(Screen.JoinPost.route) {
//            JoinPostScreen()
//        }
        composable(Screen.Community.route) {
            CommunityScreen(
                onBackClick = { navController.popBackStack() },
                onViewPostClick = { navController.navigate(Screen.ViewPost.route) }
            )
        }
        
        composable(Screen.ViewPost.route) {
            ViewPostScreen(post = )
        }
    }
}
