package com.example.chaining.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun FeedItem(
    modifier: Modifier = Modifier,
    region: String,
    place: String,
    imageUrl: String,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4285F4) // 파란색 배경
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 지역명과 명소명
            Text(
                text = "${region}에 위치한 $place",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))


            // 사진
            AsyncImage(
                model = imageUrl,
                contentDescription = "$region $place 사진",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // 사진의 높이를 지정
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop // 이미지가 공간을 꽉 채우도록 설정
            )

        }
    }
}