package com.example.chaining.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Area : Screen("area")
    object MyPage : Screen("myPage")
    object MainHome : Screen("mainHome")
    object CreatePost : Screen("createPost")
    object JoinPost : Screen("joinPost")
    object Community : Screen("community")
    object ViewPost : Screen("viewPost/{postId}") {
        fun createRoute(postId: String) = "viewPost/$postId"
    }
}