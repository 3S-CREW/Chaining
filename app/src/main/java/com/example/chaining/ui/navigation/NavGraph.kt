package com.example.chaining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.ui.login.LoginScreen
import com.example.chaining.ui.notification.NotificationScreen
import com.example.chaining.ui.screen.AdminLoginScreen
import com.example.chaining.ui.screen.ApplicationsScreen
import com.example.chaining.ui.screen.ApplyScreen
import com.example.chaining.ui.screen.CommunityScreen
import com.example.chaining.ui.screen.CreatePostScreen
import com.example.chaining.ui.screen.ENQuizScreen
import com.example.chaining.ui.screen.FeedScreen
import com.example.chaining.ui.screen.HomeScreen
import com.example.chaining.ui.screen.JoinPostScreen
import com.example.chaining.ui.screen.KRQuizScreen
import com.example.chaining.ui.screen.MainHomeScreen
import com.example.chaining.ui.screen.MyPageScreen
import com.example.chaining.ui.screen.MyPostsScreen
import com.example.chaining.ui.screen.QuizResultScreen
import com.example.chaining.ui.screen.SplashScreen
import com.example.chaining.ui.screen.ViewPostScreen
import com.example.chaining.viewmodel.QuizViewModel
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
                },
                // 관리자 로그인 버튼 클릭 시
                onAdminLoginClick = {
                    navController.navigate("adminLogin")
                }
            )
        }
        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                onBackClick = { navController.popBackStack() },
                onAdminLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onMainHomeClick = { navController.navigate("mainHome") }
            )
        }

        composable(Screen.MyPage.route) {
            MyPageScreen(
                onKRQuizClick = { navController.navigate("krQuiz") },
                onENQuizClick = { navController.navigate("enQuiz") },
                onMyPostsClick = { navController.navigate(route = Screen.MyPosts.route) },
                onMyApplicationsClick = {
                    navController.navigate(Screen.Applications.createRoute(type = "My"))
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                })
        }

        composable(Screen.MainHome.route) {
            MainHomeScreen(
                onBackClick = { navController.navigate("mainHome") },
                onMainHomeClick = { navController.navigate("mainHome") },
                onMyPageClick = { navController.navigate("myPage") },
                onCommunityClick = { navController.navigate("community") },
                onFeedClick = { navController.navigate("feed") },
                onNotificationClick = { navController.navigate(route = Screen.Notification.route) },
                onViewApplyClick = { applicationId ->
                    navController.navigate(
                        Screen.Apply.createRoute(
                            type = "Owner",
                            applicationId = applicationId
                        )
                    )
                },
            )
        }
        composable(
            route = Screen.CreatePost.route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType; defaultValue = "생성" },
                navArgument("postId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "생성"
            val postId = backStackEntry.arguments?.getString("postId")

            CreatePostScreen(
                type = type,
                postId = postId,
                onBackClick = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                onBackClick = { navController.navigate("mainHome") },
                onViewPostClick = { postId ->
                    navController.navigate(Screen.ViewPost.createRoute(postId))
                },
                onCreatePostClick = {
                    navController.navigate(
                        Screen.CreatePost.createRoute(type = "생성")
                    )
                },
            )
        }

        composable(
            route = Screen.ViewPost.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            ViewPostScreen(
                onJoinPostClick = { post ->
                    navController.navigate(Screen.JoinPost.createRoute(post))
                },
                onBackClick = { navController.popBackStack() },
                onEditClick = { postId ->
                    navController.navigate(
                        Screen.CreatePost.createRoute(
                            type = "수정",
                            postId = postId
                        )
                    )
                },
                onApplicationListClick = { postId ->
                    navController.navigate(
                        Screen.Applications.createRoute(
                            type = "Owner",
                            postId = postId
                        )
                    )
                },
                onMainHomeClick = { navController.navigate("mainHome") },
                onCommunityClick = { navController.navigate("community") },
                onFeedClick = { navController.navigate("feed") },
                onNotificationClick = { navController.navigate(route = Screen.Notification.route) }
            )
        }

        composable(
            route = Screen.JoinPost.route,
            arguments = listOf(navArgument("post") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("post")
            json?.let {
                val post = remember(it) { Gson().fromJson(it, RecruitPost::class.java) }
                JoinPostScreen(
                    onBackClick = { navController.popBackStack() },
                    post = post,
                    onSubmitSuccess = {
                        navController.navigate("mainHome")
                    },
                    onViewMyApplications = {
                        navController.navigate(Screen.Applications.createRoute(type = "My"))
                    }
                )
            }
        }

        navigation(
            startDestination = "enQuiz", // 이 그룹의 시작 화면
            route = "quiz_flow"           // 이 그룹의 고유한 이름(경로)
        ) {
            composable("enQuiz") {
                // 부모 그래프("quiz_flow")의 BackStackEntry를 가져옵니다.
                val parentEntry = remember(it) { navController.getBackStackEntry("quiz_flow") }
                // 부모의 ViewModel을 가져와 사용합니다.
                val quizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                ENQuizScreen(
                    quizViewModel = quizViewModel,
                    onNavigateToResult = {
                        navController.navigate("quizResult") {
                            popUpTo("enQuiz") { inclusive = true }
                        }
                    }
                )
            }

            // kr_quiz도 동일하게 수정
            composable("krQuiz") {
                val parentEntry = remember(it) { navController.getBackStackEntry("quiz_flow") }
                val quizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                KRQuizScreen(
                    quizViewModel = quizViewModel,
                    onNavigateToResult = {
                        navController.navigate("quizResult") {
                            // 퀴즈 화면은 뒤로가기 기록에서 제거
                            popUpTo("krQuiz") { inclusive = true }
                        }
                    }
                )
            }

            composable("quizResult") {
                // 퀴즈 화면과 동일한 부모의 ViewModel 인스턴스를 가져옵니다.
                val parentEntry = remember(it) { navController.getBackStackEntry("quiz_flow") }
                val quizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                QuizResultScreen(
                    quizViewModel = quizViewModel,
                    onNavigateToMyPage = {
                        navController.navigate(Screen.MyPage.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo("quiz_flow") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(
            route = Screen.Apply.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = "My"
                })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "My"
            val applicationId =
                backStackEntry.arguments?.getString("applicationId") ?: return@composable


            ApplyScreen(
                onBackClick = { navController.popBackStack() },
                type = type,
                applicationId = applicationId
            )
        }

        composable("feed") {
            FeedScreen(
                onBackClick = { navController.navigate("mainHome") },
                onMainHomeClick = { navController.navigate("mainHome") },
                onCommunityClick = { navController.navigate("community") },
                onFeedClick = { navController.navigate("feed") },
                onNotificationClick = { navController.navigate(route = Screen.Notification.route) }

            )
        }

        composable(route = Screen.MyPosts.route) {
            MyPostsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Applications.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = "My"
                },
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = true // postId는 선택사항이므로 nullable
                    defaultValue = null // 기본값은 null
                }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "My"
            val postId = backStackEntry.arguments?.getString("postId")

            ApplicationsScreen(
                type = type,
                postId = postId,
                onBackClick = { navController.popBackStack() },
                onViewApplyClick = { applicationId ->
                    navController.navigate(
                        Screen.Apply.createRoute(
                            type = type,
                            applicationId = applicationId
                        )
                    )
                },
            )
        }

        composable(route = Screen.Notification.route) {
            NotificationScreen(
                onViewApplyClick = { applicationId ->
                    navController.navigate(
                        Screen.Apply.createRoute(
                            type = "Owner",
                            applicationId = applicationId
                        )
                    )
                }
            )
        }
    }
}
