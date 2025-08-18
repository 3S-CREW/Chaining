package com.example.chaining.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun MyPageScreen(uid: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileSection()
        Spacer(modifier = Modifier.height(24.dp))
        BasicInfoSection()
        Spacer(modifier = Modifier.height(24.dp))
        ActionButtons()
    }
}

@Composable
fun ProfileSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(model = "프로필 URL"),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "프로필 변경",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("안녕안녕안녕", fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Edit, contentDescription = "닉네임 수정")
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("팔로워 203 · 팔로우 106", color = Color.Gray, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoSection() {
    Column {
        Text("기본 정보", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        val countries = listOf("한국", "미국", "일본")
        var selectedCountry by remember { mutableStateOf(countries[0]) }

        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}
        ) {
            TextField(
                value = selectedCountry,
                onValueChange = {},
                label = { Text("출신 국가 선택") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(false) }
            )
            DropdownMenu(
                expanded = false,
                onDismissRequest = {}
            ) {

            }
        }
    }
}

@Composable
fun ActionButtons() {
    Column {
        Button(
            onClick = { /* 모집 현황 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("모집 현황")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* 지원 현황 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("지원 현황")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {/* 프로필 저장 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3578E5))
        ) {
            Text("프로필 저장", color = Color.White)
        }
    }
}