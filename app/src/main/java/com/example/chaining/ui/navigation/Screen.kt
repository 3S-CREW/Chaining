package com.example.chaining.ui.navigation

import com.example.chaining.domain.model.RecruitPost
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AdminLogin : Screen("adminLogin")
    object Home : Screen("home")
    object Area : Screen("area")
    object MyPage : Screen("myPage")
    object MainHome : Screen("mainHome")
    object CreatePost : Screen("createPost")
    object JoinPost : Screen("joinPost?post={post}") {
        fun createRoute(post: RecruitPost): String {
            val json = Gson().toJson(post)
            val encodedJson = java.net.URLEncoder.encode(json, "UTF-8")
            return "joinPost?post=$encodedJson"
        }
    }

    object Community : Screen("community")
    object ViewPost : Screen("viewPost/{postId}") {
        fun createRoute(postId: String) = "viewPost/$postId"
    }

    object KRQuiz : Screen("krQuiz")
    object ENQuiz : Screen("enQuiz")
    object QuizResult : Screen("quizResult")
    object MyApply : Screen("myApply")
    object Feed : Screen("feed")
}