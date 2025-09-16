package com.example.chaining.ui.navigation

import android.net.Uri
import com.example.chaining.domain.model.RecruitPost
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AdminLogin : Screen("adminLogin")
    object MyPage : Screen("myPage")
    object MainHome : Screen("mainHome")
    object CreatePost : Screen("createPost?type={type}&postId={postId}") {
        fun createRoute(
            type: String,
            postId: String? = "",
        ): String {
            return "createPost?type=$type&postId=${postId ?: ""}"
        }
    }

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

    object MyPosts : Screen("myPosts")
    object Applications : Screen("applications?type={type}&postId={postId}") {
        fun createRoute(
            type: String,
            postId: String? = "",
        ): String {
            return "applications?type=$type&postId=${postId ?: ""}"
        }
    }

    object KRQuiz : Screen("krQuiz")
    object ENQuiz : Screen("enQuiz")
    object QuizResult : Screen("quizResult")
    object Apply :
        Screen("apply?type={type}&applicationId={applicationId}&closeAt={closeAt}&introduction={introduction}") {
        fun createRoute(
            type: String,
            closeAt: Long,
            introduction: String,
            applicationId: String? = "",
        ): String {
            return "apply?type=$type&applicationId=${applicationId ?: ""}&closeAt=$closeAt&introduction=${
                Uri.encode(
                    introduction,
                )
            }"
        }
    }

    object Feed : Screen("feed")
    object Term : Screen("terms/{uid}/{nickname}")

    object Notification : Screen("notification")
}
