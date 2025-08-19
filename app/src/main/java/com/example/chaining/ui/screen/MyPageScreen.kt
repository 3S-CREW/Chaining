package com.example.chaining.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.chaining.R
import com.example.chaining.domain.model.User
import com.example.chaining.viewmodel.UserViewModel

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel = hiltViewModel()
) {
    val userState by userViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileSection(user = userState, onNicknameChange = { newNickname ->
            userViewModel.updateUser(mapOf("nickname" to newNickname))
        })
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "기본 정보",
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        DropDownField(
            items = listOf("한국", "미국", "일본"),
            selectedItem = userState?.country ?: "한국",
            onItemSelected = { country -> userViewModel.updateUser(mapOf("country" to country)) },
            leadingIconRes = R.drawable.airport,
            placeholder = "출신 국가 선택"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DropDownField(
            items = listOf("서울", "부산", "제주"),
            selectedItem = userState?.residence ?: "서울",
            onItemSelected = { residence -> userViewModel.updateUser(mapOf("residence" to residence)) },
            leadingIconRes = R.drawable.country,
            placeholder = "현재 거주지 선택"
        )
        Spacer(modifier = Modifier.height(12.dp))
        DropDownField(
            items = listOf("파리", "도쿄", "뉴욕"),
            selectedItem = userState?.preferredDestinations ?: "파리",
            onItemSelected = { preferredDestinations -> userViewModel.updateUser(mapOf("preferredDestinations" to preferredDestinations)) },
            leadingIconRes = R.drawable.forest_path,
            placeholder = "선호 여행지 선택"
        )

        Spacer(modifier = Modifier.height(24.dp))
        ActionButtons(
            onSave = { userState?.let { userViewModel.saveUser() } }
        )
    }
}

@Composable
fun ProfileSection(
    user: User?,
    onNicknameChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf(user?.nickname ?: "") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = user?.profileImageUrl ?: ""),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        nicknameInput = user?.nickname ?: ""
                        showDialog = true
                    }
            ) {
                Text(
                    text = user?.nickname ?: "닉네임 없음",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.pen_squared),
                    contentDescription = "닉네임 수정",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "팔로워 203 · 팔로우 106",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }



    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("닉네임 변경") },
            text = {
                TextField(
                    value = nicknameInput,
                    onValueChange = { nicknameInput = it },
                    placeholder = { Text("닉네임 입력") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onNicknameChange(nicknameInput)
                        showDialog = false
                    }
                ) {
                    Text("변경")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(
    items: List<String>,                   // 드롭다운 항목
    selectedItem: String,                  // 선택된 값
    onItemSelected: (String) -> Unit,      // 선택 시 콜백
    leadingIconRes: Int,                   // 아이콘 리소스
    placeholder: String,                   // Placeholder 텍스트
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFF637387),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Crossfade(
                targetState = selectedItem,
                animationSpec = tween(300),
                label = "countryCrossfade"
            ) { animatedItem ->
                TextField(
                    value = animatedItem,
                    onValueChange = { },
                    readOnly = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF637387)
                    ),
                    label = {
                        Text(
                            placeholder,
                            fontSize = 10.sp,
                            color = Color(0xD9637387),
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = leadingIconRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.triangle_arrow),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .height(20.dp)
                                .width(20.dp)
                                .rotate(rotation)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF637387),
                        unfocusedTextColor = Color(0xFF637387),
                        disabledTextColor = Color(0xFF637387),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = Color(0xFF637387),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(Color.White)
                ) {
                    items.forEach { c ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = c,
                                    color = Color(0xFF637387),
                                    fontWeight = if (c == selectedItem) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onItemSelected(c)
                                expanded = false
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun ActionButtons(
    onSave: () -> Unit
) {
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
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3578E5))
        ) {
            Text("프로필 저장", color = Color.White)
        }
    }
}