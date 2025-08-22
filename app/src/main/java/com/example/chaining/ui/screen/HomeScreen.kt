package com.example.chaining.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomeScreen(
    onTableClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onMainHomeClick: () -> Unit,
    onCreatePostClick: () -> Unit
) {
    val user = Firebase.auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("환영합니다, ${user?.displayName}")

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onMainHomeClick) {
            Text("메인 홈")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTableClick) {
            Text("테이블 보기")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onMyPageClick) {
            Text("마이페이지")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { Firebase.auth.signOut() }) {
            Text("로그아웃")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreatePostClick) {
            Text(text = "모집글 작성")
        }
    }
}