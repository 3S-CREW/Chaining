package com.example.chaining.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chaining.R
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary

@Suppress("FunctionName")
@Composable
fun CardItem(
    onClick: () -> Unit,
    // "모집글" or "지원서" or "결과"
    type: String,
    recruitPost: RecruitPost? = null,
    application: Application? = null,
    remainingTime: String? = null,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {},
    currentUserId: String? = "",
    isLiked: Boolean? = false,
    hasApplied: Boolean = false,
    hasStatus: Boolean = false,
) {
    val title =
        when (type) {
            "모집글" -> recruitPost?.title ?: stringResource(id = R.string.community_no_title)
            "지원서" ->
                application?.recruitPostTitle
                    ?: stringResource(id = R.string.community_no_title)

            "결과" ->
                application?.recruitPostTitle
                    ?: stringResource(id = R.string.community_no_title)

            else -> stringResource(id = R.string.community_no_title)
        }.replace("+", " ")

    val remainingTimeText = remainingTime ?: stringResource(id = R.string.community_unknown)
    val isAuthor =
        (recruitPost?.owner?.id == currentUserId) || (application?.applicant?.id == currentUserId)

    val timeText =
        if (type == "결과") {
            stringResource(id = R.string.application_result_available)
        } else {
            when (type) {
                "모집글" -> {
                    if (remainingTimeText == stringResource(id = R.string.time_closed)) {
                        stringResource(id = R.string.time_closed)
                    } else {
                        stringResource(id = R.string.time_left_recruit, remainingTimeText)
                    }
                }

                "지원서" -> {
                    if (remainingTimeText == stringResource(id = R.string.time_closed)) {
                        stringResource(id = R.string.time_closed)
                    } else {
                        stringResource(id = R.string.time_left_application, remainingTimeText)
                    }
                }

                else -> ""
            }
        }

//    val profile = when (type) {
//        "모집글" -> recruitPost?.owner ?: UserSummary()
//        "지원서" -> application?.applicant ?: UserSummary()
//        else -> UserSummary()
//    }

    val leftButtonText =
        if (type == "모집글") {
            if (hasApplied == true) {
                stringResource(id = R.string.community_application_complete)
            } else {
                stringResource(id = R.string.community_apply_button)
            }
        } else {
            stringResource(id = R.string.application_yes)
        }
    val rightButtonText =
        if (type == "모집글") {
            stringResource(id = R.string.community_interest_button)
        } else {
            stringResource(
                id = R.string.application_no,
            )
        }

    val rightText =
        if (type == "모집글") stringResource(id = R.string.community_see_post) else stringResource(id = R.string.view_application_arrow)

    val profile =
        if (type == "모집글") {
            UserSummary(
                id = recruitPost?.owner?.id ?: "",
                nickname =
                    recruitPost?.owner?.nickname
                        ?: stringResource(id = R.string.community_unknown),
                profileImageUrl = recruitPost?.owner?.profileImageUrl ?: "",
                country =
                    recruitPost?.owner?.country
                        ?: stringResource(id = R.string.community_unknown),
            )
        } else {
            UserSummary(
                id = application?.applicant?.id ?: "",
                nickname =
                    application?.applicant?.nickname
                        ?: stringResource(id = R.string.community_unknown),
                profileImageUrl = application?.applicant?.profileImageUrl ?: "",
                country =
                    application?.applicant?.country
                        ?: stringResource(id = R.string.community_unknown),
            )
        }

    val buttonColor by animateColorAsState(
        targetValue = if (isLiked == true) Color(0xFFFF4D4D) else Color(0xFFEBEFFA),
        animationSpec = tween(durationMillis = 300),
        label = "likeColor",
    )

    val scale = remember { Animatable(1f) }

    LaunchedEffect(isLiked) {
        if (isLiked == true) {
            scale.animateTo(
                targetValue = 1.05f,
                animationSpec = spring(stiffness = 500f),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(stiffness = 500f),
            )
        }
    }

    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF4285F4),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "남은 시간",
                    tint = Color.White,
                    // 아이콘 크기 조절
                    modifier = Modifier.size(20.dp),
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text =
                        if (type == "결과") {
                            stringResource(id = R.string.application_result_available)
                        } else {
                            timeText
                        },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 프로필 사진
                        AsyncImage(
                            model = if (profile.profileImageUrl.isNotBlank()) profile.profileImageUrl else R.drawable.test_profile,
                            contentDescription = "모집자/신청자 프로필 사진",
                            contentScale = ContentScale.Crop,
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(15.dp)),
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = profile.nickname,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                            Text(
                                text = profile.country,
                                fontSize = 12.sp,
                                color = Color.Gray,
                            )
                        }

                        // 지원서 보기 텍스트
                        Text(
                            text = rightText,
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (type == "결과") {
                            Text(
                                text = stringResource(id = R.string.application_check_result),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier =
                                    if (type == "결과") {
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    } else {
                                        Modifier.padding(12.dp)
                                    },
                                textAlign = if (type == "결과") TextAlign.Center else TextAlign.Start,
                            )
                        } else {
                            // 왼쪽 버튼
                            Button(
                                onClick = onLeftButtonClick,
                                modifier = Modifier.weight(3f),
                                shape = RoundedCornerShape(20.dp),
                                enabled = !isAuthor && !hasApplied && !hasStatus,
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4285F4),
                                        contentColor = Color.White,
                                    ),
                            ) {
                                Text(text = leftButtonText)
                            }
                            // 오른쪽 버튼
                            if (type == "모집글") {
                                Button(
                                    onClick = onRightButtonClick,
                                    modifier =
                                        Modifier
                                            .weight(2f)
                                            .scale(scale.value),
                                    shape = RoundedCornerShape(20.dp),
                                    enabled = !isAuthor,
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = buttonColor,
                                            contentColor = if (isLiked == true) Color.White else Color.Gray,
                                        ),
                                ) {
                                    Text(text = rightButtonText)
                                }
                            } else {
                                Button(
                                    onClick = onRightButtonClick,
                                    modifier = Modifier.weight(2f),
                                    shape = RoundedCornerShape(20.dp),
                                    enabled = !isAuthor && !hasStatus,
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFEBEFFA),
                                            contentColor = Color.Gray,
                                        ),
                                ) {
                                    Text(text = rightButtonText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
