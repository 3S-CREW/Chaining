package com.example.chaining.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chaining.R
import com.example.chaining.domain.model.UserSummary
import com.example.chaining.viewmodel.UserViewModel

@Suppress("FunctionName")
@Composable
fun OwnerProfile(
    owner: UserSummary,
    // 카드뷰, 모집글 상세보기, 지원서
    where: String,
    userViewModel: UserViewModel = hiltViewModel(),
    // "상세 보기"
    type: String? = "",
    showFollowButton: Boolean = false,
) {
    val userState by userViewModel.user.collectAsState()
    val nicknameInfo =
        when (where) {
            "카드뷰" -> 18.sp to 0xFF4A526A
            "모집글 상세보기" -> {
                if (showFollowButton) 14.sp to 0xFFFFFFFF else 16.sp to 0xFFFFFFFF // 버튼 없으면 폰트 크게
            }
            "지원서" -> 14.sp to 0xFF4A526A
            else -> 14.sp to 0xFF4A526A
        }

    val countryInfo =
        when (where) {
            "카드뷰" -> 14.sp to 0xFF7282B4
            "모집글 상세보기" -> 12.sp to 0xCCFFFFFF
            "지원서" -> 12.sp to 0xFF4A526A
            else -> 12.sp to 0xFF4A526A
        }

    val imageSize =
        when (where) {
            "카드뷰" -> 50.dp
            else -> 40.dp
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = if (owner.profileImageUrl.isNotBlank()) owner.profileImageUrl else R.drawable.test_profile,
            contentDescription = "작성자 프로필 사진",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(16.dp)),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = owner.nickname,
                fontWeight = FontWeight.SemiBold,
                fontSize = nicknameInfo.first,
                color = Color(nicknameInfo.second),
            )
            if (where != "모집글 상세보기") {
                Text(
                    text = owner.country,
                    fontSize = countryInfo.first,
                    color = Color(countryInfo.second),
                )
            }
        }
        if (showFollowButton) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier =
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3ECDFF))
                        .border(2.dp, Color.White, CircleShape)
                        .padding(3.dp)
                        .clickable {
                            val currentUserSummary =
                                UserSummary(
                                    id = userState?.id ?: "",
                                    nickname = userState?.nickname ?: "",
                                    profileImageUrl = userState?.profileImageUrl ?: "",
                                    country = userState?.country ?: "",
                                )
                            userViewModel.toggleFollow(currentUserSummary, owner)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.follow),
                    contentDescription = "친구 추가",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
