package com.example.chaining.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SaveButton(
    onSave: () -> Unit, // 이제 일반적인 람다 함수를 받습니다.
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSave, // 이제 정상적으로 연결됩니다.
        modifier = modifier
            .fillMaxWidth() // 버튼이 `fillMaxWidth()`가 되도록 수정 (디자인에 따라 조절)
            .height(45.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}