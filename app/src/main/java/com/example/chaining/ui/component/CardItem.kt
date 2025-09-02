package com.example.chaining.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chaining.R
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary

@Composable
fun CardItem(
    onClick: () -> Unit,
    type: String, // "모집글" or "지원서"
    recruitPost: RecruitPost? = null,
    application: String? = null,
    remainingTime: String? = null,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    val title = when (type) {
        "모집글" -> recruitPost?.title ?: "제목 없음"
        "지원서" -> application ?: "지원서 제목 없음"
        else -> "제목 없음"
    }

    val timeText = when (type) {
        "모집글" -> "모집 마감까지 ${remainingTime ?: "알 수 없음"} 남음"
        "지원서" -> "수락/거절까지 ${remainingTime ?: "알 수 없음"} 남음"
        else -> ""
    }

    val leftButtonText = if (type == "모집글") "신청" else "수락"
    val rightButtonText = if (type == "모집글") "관심" else "거절"

    val rightText = if (type == "모집글") "모집글 보기 >" else "지원서 보기 >"

    val profile = if (type == "모집글") {
        recruitPost?.owner ?: UserSummary(
            id = "unknown",
            nickname = "알 수 없음",
            profileImageUrl = "",
            country = ""
        )
    } else {
        UserSummary(
            id = "1234",
            nickname = "차무식",
            profileImageUrl = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg",
            country = "필리핀"
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4285F4)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "남은 시간",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp) // 아이콘 크기 조절
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 프로필 사진
                        AsyncImage(
                            model = if (profile.profileImageUrl.isNotBlank()) profile.profileImageUrl else R.drawable.test_profile,
                            contentDescription = "모집자/신청자 프로필 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                        )


                        Spacer(modifier = Modifier.width(12.dp))


                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = profile.nickname,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = profile.country,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // 지원서 보기 텍스트
                        Text(
                            text = rightText,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 왼쪽 버튼
                        Button(
                            onClick = onLeftButtonClick,
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = leftButtonText)
                        }

                        // 오른쪽 버튼
                        Button(
                            onClick = onRightButtonClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEBEFFA),
                                contentColor = Color.Gray
                            )
                        ) {
                            Text(text = rightButtonText)
                        }
                    }
                }

            }
        }
    }
}