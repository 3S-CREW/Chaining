package com.example.chaining.ui.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.chaining.R
import com.example.chaining.ui.component.TestButton
import com.example.chaining.viewmodel.AreaViewModel
import com.example.chaining.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val PrimaryBlue = Color(0xFF3387E5)
val SecondaryTextColor = Color(0xFF637387)
val LightGrayBackground = Color(0xFFF3F6FF)
val BorderColor = Color(0xFFE0E0E0)
val White = Color(0xFFFEFEFE)
val Black = Color.Black

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    onKRQuizClick: () -> Unit,
    onENQuizClick: () -> Unit,
    onMyPostsClick: () -> Unit,
    onMyApplicationsClick: () -> Unit,
    onLogout: () -> Unit,
    areaViewModel: AreaViewModel = hiltViewModel()
) {
    val areaEntities by areaViewModel.areaCodes.collectAsState()
    val userState by userViewModel.user.collectAsState()
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(userState?.country ?: "") }
    var residence by remember { mutableStateOf(userState?.residence ?: "") }
    var preferredDestinations by remember {
        mutableStateOf(
            userState?.preferredDestinations ?: ""
        )
    }

    LaunchedEffect(userState) {
        userState?.let {
            nickname = it.nickname
            country = it.country
            residence = it.residence
            preferredDestinations = it.preferredDestinations
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
    ) {
        // --- 상단 고정 영역 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("로그아웃", style = MaterialTheme.typography.labelLarge)
            }
        }

        // --- 중앙 스크롤 영역 ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(
                nickname = nickname,
                onNicknameChanged = { newNickname ->
                    nickname = newNickname
                    userState?.let { currentUser ->
                        userViewModel.updateMyUser(currentUser.copy(nickname = newNickname))
                    }
                },
                profileImageUrl = userState?.profileImageUrl,
                userViewModel = userViewModel
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "기본 정보",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DropDownField(
                        items = listOf("한국", "미국", "일본", "중국", "영국", "독일", "프랑스"),
                        selectedItem = country,
                        leadingIconRes = R.drawable.airport,
                        placeholder = "출신 국가 선택",
                        onItemSelected = { country = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val areaNames = remember(areaEntities) {
                        areaEntities
                            .map { it.regionName }
                    }
                    DropDownField(
                        items = areaNames,
                        selectedItem = residence,
                        leadingIconRes = R.drawable.country,
                        placeholder = "현재 거주지 선택",
                        onItemSelected = { residence = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DropDownField(
                        items = listOf("산", "바다", "도시", "액티비티", "휴양", "문화/예술"),
                        selectedItem = preferredDestinations,
                        leadingIconRes = R.drawable.forest_path,
                        placeholder = "선호 여행지 스타일 선택",
                        onItemSelected = { preferredDestinations = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            userState?.preferredLanguages?.let {
                Text(
                    text = "언어 능력 테스트",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                TestButton(
                    preferredLanguages = it,
                    onTestClick = { language ->
                        when (language) {
                            "한국어" -> onKRQuizClick()
                            "영어" -> onENQuizClick()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 모집 현황, 지원 현황 버튼
            Button(
                onClick = { onMyPostsClick() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = SecondaryTextColor
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(width = 1.dp, color = BorderColor)
            ) {
                Text(
                    text = "모집 현황",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onMyApplicationsClick() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = SecondaryTextColor
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(width = 1.dp, color = BorderColor)
            ) {
                Text(
                    text = "지원 현황",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(modifier = Modifier.height(24.dp)) // 스크롤 영역 하단 여백
        }

        // --- 하단 고정 영역 ---
        Button(
            onClick = {
                val unselectedFields = mutableListOf<String>()
                if (country.isEmpty()) unselectedFields.add("출신 국가")
                if (residence.isEmpty()) unselectedFields.add("현재 거주지")
                if (preferredDestinations.isEmpty()) unselectedFields.add("선호 여행지 스타일")


                if (unselectedFields.isNotEmpty()) {
                    val message = "${unselectedFields.joinToString(", ")} 항목을 선택해주세요."
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    userState?.let { currentUser ->
                        val updatedUser = currentUser.copy(
                            nickname = nickname,
                            country = country,
                            residence = residence,
                            preferredDestinations = preferredDestinations
                        )
                        userViewModel.updateMyUser(updatedUser)
                        Toast.makeText(context, "프로필이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "프로필 저장",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun ProfileSection(
    nickname: String,
    onNicknameChanged: (String) -> Unit,
    profileImageUrl: String?,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempNickname by remember { mutableStateOf(nickname) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val size = getFileSize(context, uri)
            if (size > 2 * 1024 * 1024) {
                Toast.makeText(context, "2MB 이하 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@let
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@let
            }
            val storageRef = Firebase.storage.reference.child("profileImages/$uid.jpg")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        userViewModel.updateProfileImage(downloadUrl.toString())
                        Toast.makeText(context, "프로필 이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "이미지 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = profileImageUrl.takeIf { !it.isNullOrEmpty() }
                        ?: R.drawable.test_profile
                ),
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlue, CircleShape)
            )
            Icon(
                painter = painterResource(id = R.drawable.change),
                contentDescription = "프로필 변경",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable {
                tempNickname = nickname
                showDialog = true
            }
        ) {
            Text(
                text = nickname.ifEmpty { "닉네임 없음" },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.pen_squared),
                contentDescription = "닉네임 수정",
                tint = SecondaryTextColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "팔로워 203 · 팔로우 106",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.Gray,
        )
    }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("닉네임 변경", style = MaterialTheme.typography.titleLarge) },
            text = {
                TextField(
                    value = tempNickname,
                    onValueChange = { tempNickname = it },
                    label = { Text("새 닉네임", color = SecondaryTextColor) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = LightGrayBackground,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onNicknameChanged(tempNickname)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("변경", color = White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightGrayBackground,
                        contentColor = Black
                    )
                ) {
                    Text("취소")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(
    items: List<String>,
    selectedItem: String,
    leadingIconRes: Int,
    placeholder: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = "dropdownArrowRotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LightGrayBackground)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Crossfade(
                targetState = if (selectedItem.isEmpty()) placeholder else selectedItem,
                animationSpec = tween(300),
                label = "dropdownCrossfade"
            ) { animatedItem ->
                TextField(
                    value = animatedItem,
                    onValueChange = { },
                    readOnly = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = SecondaryTextColor
                    ),
                    label = {
                        Text(
                            placeholder,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                            color = Color.Gray,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = leadingIconRes),
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.triangle_arrow),
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(rotation)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        disabledTextColor = Black,
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = LightGrayBackground,
                        disabledContainerColor = LightGrayBackground,
                        cursorColor = PrimaryBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = Color.Gray
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
                        .background(White, RoundedCornerShape(8.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            expanded = false
                            onItemSelected("")
                        }
                    )

                    items.forEach { c ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = c,
                                    color = Black,
                                    fontWeight = if (c == selectedItem) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                expanded = false
                                onItemSelected(c)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getFileSize(context: Context, uri: Uri): Long {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE) ?: -1
    cursor?.moveToFirst()
    val size = if (sizeIndex >= 0) cursor?.getLong(sizeIndex) else 0L
    cursor?.close()
    return size ?: 0L
}