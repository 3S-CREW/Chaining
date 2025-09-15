package com.example.chaining.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chaining.ui.component.SplashAnimation
import com.example.chaining.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("FunctionName")
@Composable
fun SplashScreen(navController: NavController) {
    // X축 슬라이드인 용도
    val offsetX = remember { Animatable(300f) }
    // 페이드 인
    val alpha = remember { Animatable(0f) }
    // Y축 슬라이드인 용도
    val offsetY = remember { Animatable(0f) }
    // 체인 이미지 용도
    val chainVisible = remember { mutableStateOf(false) }
    // 2단계 시작 용도
    val textSlideDownStart = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 1단계: 오른쪽 -> 중앙 슬라이드 + 페이드인
        launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            )
        }

        delay(800)

        // 2단계: 중앙 -> 아래쪽 슬라이드
        textSlideDownStart.value = true
        launch {
            offsetY.animateTo(
                70f,
                animationSpec = tween(400, easing = LinearOutSlowInEasing),
            )
        }
        delay(50)
        chainVisible.value = true

        delay(1500)

        val isLoggedIn = Firebase.auth.currentUser != null
        if (isLoggedIn) {
            navController.navigate(route = Screen.MainHome.route) {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SplashAnimation(startAnimation = chainVisible.value)
            Text(
                text = "Chaining",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier =
                    Modifier
                        .graphicsLayer {
                            translationX = offsetX.value
                            translationY = offsetY.value
                            this.alpha = alpha.value
                        },
            )
        }
    }
}
