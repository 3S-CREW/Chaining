package com.example.chaining.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chaining.R

@Suppress("FunctionName")
@Composable
fun FeedItem(
    modifier: Modifier = Modifier,
    region: String,
    place: String,
    address: String,
    imageUrl: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                // 파란색 배경
                containerColor = Color(0xFF4285F4),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            // 사진
            AsyncImage(
                model = imageUrl,
                contentDescription = "$region $place 사진",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        // 사진의 높이를 지정
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                // 이미지가 공간을 꽉 채우도록 설정
                contentScale = ContentScale.Crop,
            )
            // 2. 하단 텍스트 영역 (파란색 배경)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF4285F4))
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        // 텍스트와 배경 사이의 내부 여백
                        .padding(16.dp),
            ) {
                // 지역명과 명소명
                Text(
                    text = stringResource(id = R.string.feed_item_region_place, region, place),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 주소
                Text(
                    text = stringResource(id = R.string.feed_item_address, address),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
