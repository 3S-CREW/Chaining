package com.example.chaining.ui.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Suppress("FunctionName")
@Composable
fun MyPageScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    onKRQuizClick: () -> Unit,
    onENQuizClick: () -> Unit,
    onMyPostsClick: () -> Unit,
    onMyApplicationsClick: () -> Unit,
    onLogout: () -> Unit,
    areaViewModel: AreaViewModel = hiltViewModel(),
) {
    val areaEntities by areaViewModel.areaCodes.collectAsState()
    val userState by userViewModel.user.collectAsState()
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(userState?.country ?: "") }
    var residence by remember { mutableStateOf(userState?.residence ?: "") }
    var preferredDestinations by remember {
        mutableStateOf(
            userState?.preferredDestinations ?: "",
        )
    }

    val koreanText = stringResource(id = R.string.language_korean)
    val englishText = stringResource(id = R.string.language_english)
    val countryFieldText = stringResource(id = R.string.mypage_country)
    val locationFieldText = stringResource(id = R.string.mypage_location)
    val prefstyleFieldText = stringResource(id = R.string.mypage_prefstyle)
    val validationMessageFormat = stringResource(id = R.string.validation_select_fields)
    val saveSuccessMessage = stringResource(id = R.string.mypage_profile_save_success)

    LaunchedEffect(userState) {
        userState?.let {
            nickname = it.nickname
            country = it.country
            residence = it.residence
            preferredDestinations = it.preferredDestinations
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(LightGrayBackground),
    ) {
        // --- 상단 고정 영역 ---
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = White,
                    ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    stringResource(id = R.string.mypage_logout),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        // --- 중앙 스크롤 영역 ---
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
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
                userViewModel = userViewModel,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.mypage_info),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Black,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = White),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DropDownField(
                        items =
                            listOf(
                                stringResource(id = R.string.mypage_kr),
                                stringResource(id = R.string.mypage_us),
                                stringResource(id = R.string.mypage_jp),
                                stringResource(id = R.string.mypage_cn),
                                stringResource(id = R.string.mypage_uk),
                                stringResource(id = R.string.mypage_gm),
                                stringResource(id = R.string.mypage_fr),
                            ),
                        selectedItem = country,
                        leadingIconRes = R.drawable.airport,
                        placeholder = stringResource(id = R.string.mypage_country),
                        onItemSelected = { country = it },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val areaNames =
                        remember(areaEntities) {
                            areaEntities
                                .map { it.regionName }
                        }
                    DropDownField(
                        items = areaNames,
                        selectedItem = residence,
                        leadingIconRes = R.drawable.country,
                        placeholder = stringResource(id = R.string.mypage_location),
                        onItemSelected = { residence = it },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DropDownField(
                        items =
                            listOf(
                                stringResource(id = R.string.travel_style_mountain),
                                stringResource(id = R.string.travel_style_sea),
                                stringResource(id = R.string.travel_style_city),
                                stringResource(id = R.string.travel_style_activity),
                                stringResource(id = R.string.travel_style_rest),
                                stringResource(id = R.string.travel_style_culture),
                            ),
                        selectedItem = preferredDestinations,
                        leadingIconRes = R.drawable.forest_path,
                        placeholder = stringResource(id = R.string.mypage_prefstyle),
                        onItemSelected = { preferredDestinations = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            userState?.preferredLanguages?.let {
                Text(
                    text = stringResource(id = R.string.mypage_quiz),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Black,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                TestButton(
                    preferredLanguages = it,
                    onTestClick = { language ->
                        when (language) {
                            koreanText -> onKRQuizClick()
                            englishText -> onENQuizClick()
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 모집 현황, 지원 현황 버튼
            Button(
                onClick = { onMyPostsClick() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = SecondaryTextColor,
                    ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(width = 1.dp, color = BorderColor),
            ) {
                Text(
                    text = stringResource(id = R.string.mypage_post),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onMyApplicationsClick() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = SecondaryTextColor,
                    ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(width = 1.dp, color = BorderColor),
            ) {
                Text(
                    text = stringResource(id = R.string.mypage_apply),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(modifier = Modifier.height(24.dp)) // 스크롤 영역 하단 여백
        }

        // --- 하단 고정 영역 ---
        Button(
            onClick = {
                val unselectedFields = mutableListOf<String>()
                if (country.isEmpty()) unselectedFields.add(countryFieldText)
                if (residence.isEmpty()) unselectedFields.add(locationFieldText)
                if (preferredDestinations.isEmpty()) unselectedFields.add(prefstyleFieldText)

                if (unselectedFields.isNotEmpty()) {
                    val message =
                        context.getString(
                            R.string.validation_select_fields,
                            unselectedFields.joinToString(", "),
                        )
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    userState?.let { currentUser ->
                        val updatedUser =
                            currentUser.copy(
                                nickname = nickname,
                                country = country,
                                residence = residence,
                                preferredDestinations = preferredDestinations,
                            )
                        userViewModel.updateMyUser(updatedUser)
                        Toast.makeText(
                            context,
                            context.getString(R.string.mypage_profile_save_success),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = White,
                ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                stringResource(id = R.string.mypage_profile_save),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Suppress("FunctionName")
@Composable
fun ProfileSection(
    nickname: String,
    onNicknameChanged: (String) -> Unit,
    profileImageUrl: String?,
    userViewModel: UserViewModel,
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempNickname by remember { mutableStateOf(nickname) }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let {
                val size = getFileSize(context, uri)
                if (size > 2 * 1024 * 1024) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.mypage_select_image_under_2mb),
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@let
                }

                val uid =
                    FirebaseAuth.getInstance().currentUser?.uid ?: run {
                        Toast.makeText(
                            context,
                            context.getString(R.string.mypage_login_required),
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@let
                    }
                val storageRef = Firebase.storage.reference.child("profileImages/$uid.jpg")

                Toast.makeText(context,
                    context.getString((R.string.mypage_profile_image_upload)),
                    Toast.LENGTH_SHORT
                ).show()

                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            userViewModel.updateProfileImage(downloadUrl.toString())
                            Toast.makeText(
                                context,
                                context.getString(R.string.mypage_profile_image_changed),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            context.getString(R.string.mypage_image_upload_failed, e.message),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
            Image(
                painter =
                    rememberAsyncImagePainter(
                        model =
                            profileImageUrl.takeIf { !it.isNullOrEmpty() }
                                ?: R.drawable.test_profile,
                    ),
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, PrimaryBlue, CircleShape),
            )
            Icon(
                painter = painterResource(id = R.drawable.change),
                contentDescription = "프로필 변경",
                tint = Color.Unspecified,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier.clickable {
                    tempNickname = nickname
                    showDialog = true
                },
        ) {
            Text(
                text = nickname.ifEmpty { stringResource(id = R.string.mypage_no_nickname) },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Black,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.pen_squared),
                contentDescription = stringResource(id = R.string.mypage_edit_nickname),
                tint = SecondaryTextColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.mypage_follower_info, "203", "106"),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.Gray,
        )
    }

    if (showDialog) {
        var tempNickname by remember(nickname) { mutableStateOf(nickname) }
        var nicknameErrorResId by remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(tempNickname) {
            // validateNickname으로부터 이제 String이 아닌 Int? (리소스 ID)를 받음
            nicknameErrorResId = validateNickname(tempNickname)
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    stringResource(id = R.string.mypage_nick_change),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Column {
                    TextField(
                        value = tempNickname,
                        onValueChange = {
                            tempNickname = it
                        },
                        label = {
                            Text(
                                stringResource(id = R.string.mypage_new_nick),
                                color = SecondaryTextColor,
                            )
                        },
                        singleLine = true,
                        isError = nicknameErrorResId != null,
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = LightGrayBackground,
                                unfocusedContainerColor = LightGrayBackground,
                                focusedIndicatorColor = PrimaryBlue,
                                unfocusedIndicatorColor = BorderColor,
                                errorIndicatorColor = MaterialTheme.colorScheme.error,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    nicknameErrorResId?.let { resId ->
                        Text(
                            // stringResource를 사용해 ID를 실제 텍스트로 변환
                            text = stringResource(id = resId),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (validateNickname(tempNickname) == null) {
                            onNicknameChanged(tempNickname)
                            showDialog = false
                        }
                    },
                    enabled = validateNickname(tempNickname) == null,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                ) {
                    Text(stringResource(id = R.string.mypage_change), color = White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = LightGrayBackground,
                            contentColor = Black,
                        ),
                ) {
                    Text(stringResource(id = R.string.mypage_cancel))
                }
            },
        )
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(
    items: List<String>,
    selectedItem: String,
    leadingIconRes: Int,
    placeholder: String,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    // FilterDropdown의 구조와 로직을 가져와 스타일만 MyPage에 맞게 수정
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White),
    ) {
        OutlinedTextField(
            value = if (selectedItem.isEmpty()) placeholder else selectedItem,
            onValueChange = {},
            readOnly = true,
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (selectedItem.isEmpty()) Color.Gray else SecondaryTextColor,
                ),
            label = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIconRes),
                    contentDescription = null,
                    tint = SecondaryTextColor,
                    modifier = Modifier.size(24.dp),
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors =
                ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    // 배경색은 투명하게 유지
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    // 테두리 색상 지정
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderColor,
                ),
            shape = RoundedCornerShape(4.dp),
            modifier =
                Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(White),
        ) {
            // "선택 안 함" 옵션 추가 (값을 비우는 기능)
            DropdownMenuItem(
                text = { Text(placeholder, color = Color.Gray) },
                onClick = {
                    onItemSelected("") // 빈 문자열을 전달하여 선택 해제
                    expanded = false
                },
            )
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontWeight = if (item == selectedItem) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun getFileSize(
    context: Context,
    uri: Uri,
): Long {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE) ?: -1
    cursor?.moveToFirst()
    val size = if (sizeIndex >= 0) cursor?.getLong(sizeIndex) else 0L
    cursor?.close()
    return size ?: 0L
}

fun validateNickname(nickname: String): Int? {
    if (nickname.isBlank()) {
        return R.string.error_nickname_blank
    }
    val pattern = Regex("^[a-zA-Z0-9가-힣]*$")
    if (!pattern.matches(nickname)) {
        return R.string.error_nickname_pattern
    }
    var weightedLength = 0
    for (char in nickname) {
        weightedLength += if (char in '가'..'힣') 2 else 1
    }
    if (weightedLength > 12) {
        return R.string.error_nickname_length
    }
    return null
}

fun generateRandomNickname(): String {
    val adjectives = listOf("행복한", "즐거운", "용감한", "신나는", "총명한", "빛나는")
    val nouns = listOf("여행가", "탐험가", "모험가", "항해사", "개척자", "방랑자", "별빛")
    // 공백 없이 두 단어를 조합
    return "${adjectives.random()}${nouns.random()}"
}
