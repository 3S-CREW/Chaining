package com.example.chaining.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chaining.R

// OptIn annotation for using experimental Material 3 APIs
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onMyPageClick: () -> Unit
) {
    Scaffold(
        topBar = {
            // 상단 앱 바 구현
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFFF3F6FF)) // 배경색 지정
                    .padding(top = 4.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                // 제목 (앱 이름)
                Text(
                    text = "Chaining",
                    modifier = Modifier.weight(1f), // 남는 공간을 모두 차지
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center // 텍스트를 할당된 공간 중앙에 정렬
                )

                // 프로필 사진 (추후 마이페이지 버튼)
                ProfileImageWithStatus(
                    model = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg",
                    isOnline = true,
                    onMyPageClick = onMyPageClick
                )
            }
        },
        bottomBar = {
            // 하단 네비게이션 바 구현
            AppBottomNavigation(selectedTab = "HOME")
        },
    ) { innerPadding ->
        // 중앙 콘텐츠 구현 (환영 메시지, 매칭 카드, 팔로우 목록 등)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F6FF))
        ) {
            Text(
                "오팔만님 반갑습니다.", // TODO: 실제 사용자 닉네임으로 변경
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 32.dp)
            )
            Text(
                text = "최근 접수된 지원서",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp)
            )
            MatchingRequestCard()

            Text(
                text = "최근 팔로우 목록",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp)
            )
            FollowerListItem(
                name = "강호동",
                timestamp = "23분 전",
                imageUrl = "https://spnimage.edaily.co.kr/images/photo/files/NP/S/2015/03/PS15032500193.jpg"
            )
            FollowerListItem(
                name = "차무식",
                timestamp = "1시간 전",
                imageUrl = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg"
            )
        }
    }
}

@Composable
fun AppBottomNavigation(selectedTab: String) { // "selectedTab" 파라미터 추가
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shadowElevation = 12.dp,
        color = Color(0xFFF3F6FF)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // if문을 사용해 선택된 탭에 따라 다른 아이콘을 표시
            val homeIcon = if (selectedTab == "HOME") R.drawable.selected_home else R.drawable.home
            val peopleIcon =
                if (selectedTab == "PEOPLE") R.drawable.selected_people else R.drawable.people
            val searchIcon =
                if (selectedTab == "SEARCH") R.drawable.selected_search else R.drawable.search
            val alarmIcon =
                if (selectedTab == "ALARM") R.drawable.selected_alarm else R.drawable.alarm

            CustomIconButton(
                onClick = { /*TODO: 홈 화면으로 이동*/ },
                iconRes = homeIcon,
                description = "메인 홈"
            )
            CustomIconButton(
                onClick = { /*TODO: 매칭 화면으로 이동*/ },
                iconRes = peopleIcon,
                description = "매칭"
            )
            CustomIconButton(
                onClick = { /*TODO: 검색 화면으로 이동*/ },
                iconRes = searchIcon,
                description = "검색"
            )
            CustomIconButton(
                onClick = { /*TODO: 알림 화면으로 이동*/ },
                iconRes = alarmIcon,
                description = "알림"
            )
        }
    }
}

@Composable
private fun CustomIconButton(
    onClick: () -> Unit,
    iconRes: Int,
    description: String
) {
    // 1. 버튼의 상호작용 상태를 추적하기 위한 interactionSource
    val interactionSource = remember { MutableInteractionSource() }

    // 2. interactionSource를 통해 현재 '눌려있는지' 여부를 Boolean 값으로 가져옴
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3. isPressed 값에 따라 scale 값을 0.9f 또는 1f로 애니메이션
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f)
    Box(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                // 리플 효과를 없애기 위한 핵심 코드
                indication = null,
                interactionSource = interactionSource
            )
            // 버튼의 터치 영역을 적절히 확보하기 위한 패딩
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            modifier = Modifier
                .size(30.dp)
                // 4. 애니메이션으로 변경되는 scale 값을 아이콘에 적용
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun ProfileImageWithStatus(
    model: Any,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onMyPageClick: () -> Unit
) {
    // Box를 사용해 이미지와 상태 점을 겹치게 만듭니다.
    Box(
        modifier = modifier
            .size(40.dp)
            .clickable { onMyPageClick() }
    ) {
        // 프로필 이미지
        AsyncImage(
            model = model,
            contentDescription = "프로필 사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize() // 부모(Box) 크기에 맞춤
                .clip(RoundedCornerShape(15.dp)) // 이미지를 원형으로 자름
        )

        // 온라인 상태를 표시하는 점
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd) // 오른쪽 아래에 배치
                    .background(Color(0xFF00C853), CircleShape) // 초록색 배경
                    .border(width = 1.5.dp, color = Color.White, shape = CircleShape) // 흰색 테두리
            )
        }
    }
}

@Composable
fun MatchingRequestCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 28.dp), // 소제목과의 간격
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4285F4) // 파란색 배경
        )
    ) {
        Column(
            // 카드 내부 콘텐츠들을 위한 여백
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "제주도 하이킹 함께 하실 분!",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically // 아이콘과 텍스트를 세로 중앙 정렬
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "남은 시간",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp) // 아이콘 크기 조절
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "수락/거절까지 12시간 30분 남음",
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
                            model = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg",
                            contentDescription = "신청자 프로필 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                        )

                        // 사진과 이름 사이 간격
                        Spacer(modifier = Modifier.width(12.dp))

                        // 이름과 태그
                        Column(
                            modifier = Modifier.weight(1f) // 남는 공간을 모두 차지
                        ) {
                            Text(
                                text = "차무식",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "필리핀",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // 지원서 보기 텍스트
                        Text(
                            text = "지원서 보기 >",
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
                        // 수락 버튼
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.weight(2f), // 남은 공간의 절반 차지
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4), // 더 진한 파란색
                                contentColor = Color.White
                            )
                        ) {
                            Text("수락")
                        }

                        // 거절 버튼
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.weight(1f), // 남은 공간의 절반 차지
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEBEFFA),
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("거절")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun FollowerListItem(name: String, timestamp: String, imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageWithStatus(model = imageUrl, onMyPageClick = {}, isOnline = true)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "${name}님께서 팔로우를 하셨습니다.",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}