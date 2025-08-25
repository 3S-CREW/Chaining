package com.example.chaining.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommunityScreen() {
    Column(
        modifier = Modifier
            .height(20.dp)
    ) {
        Text(text = "Hello World")
    }
}