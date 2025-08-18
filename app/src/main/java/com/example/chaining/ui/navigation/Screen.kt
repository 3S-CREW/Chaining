package com.example.chaining.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Area : Screen("area")
    object MyPage : Screen("myPage")
    object MainHome : Screen("mainHome")
}