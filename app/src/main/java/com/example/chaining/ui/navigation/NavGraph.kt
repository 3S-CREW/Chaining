package com.example.chaining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chaining.ui.login.LoginScreen
import com.example.chaining.ui.screen.AreaScreen
import com.example.chaining.ui.screen.HomeScreen
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
            LoginScreen {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("home") {
            HomeScreen(
                onTableClick = { navController.navigate("area") }
            )
        }
        composable("area") {
            AreaScreen()
        }
    }
}
