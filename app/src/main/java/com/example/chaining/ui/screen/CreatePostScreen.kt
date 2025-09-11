package com.example.chaining.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.domain.model.LanguagePref
import com.example.chaining.domain.model.LocationPref
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary
import com.example.chaining.ui.component.DatePickerFieldToModal
import com.example.chaining.ui.component.SaveButton
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel

@Composable
fun CreatePostScreen(
    postId: String? = null,
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel(),
    type: String // "생성" or "수정"

) {
    val userState by userViewModel.user.collectAsState()
    val postState by postViewModel.post.collectAsState()

    LaunchedEffect(key1 = type, key2 = postId) {
        if (type == "수정" && postId != null) {
            postViewModel.fetchPost(postId)
        }
    }

    var title by remember { mutableStateOf(userState?.nickname ?: "") }
    var content by remember { mutableStateOf("") }
    var preferredDestinations by remember { mutableStateOf("") }
    var preferredLocation by remember { mutableStateOf<LocationPref>(LocationPref()) }
    var preferredLanguages by remember {
        mutableStateOf(
            userState?.preferredLanguages ?: emptyMap()
        )
    }
    var hasCar by remember { mutableStateOf("") }
    var tourAt by remember { mutableStateOf<Long?>(null) }
    var closeAt by remember { mutableStateOf<Long?>(null) }
    var kakaoOpenChatUrl by remember { mutableStateOf("") }

    val languages = listOf("한국어", "영어", "중국어", "일본어")
    val buttonText = if (type == "생성") "작성 완료" else "수정 완료"

    LaunchedEffect(postState) {
        val currentPost = postState
        if (type == "수정" && currentPost != null) {
            title = currentPost.title
            content = currentPost.content
            preferredDestinations = currentPost.preferredDestinations
            preferredLocation = currentPost.preferredLocations
            preferredLanguages = currentPost.preferredLanguages
            hasCar = currentPost.hasCar
            tourAt = currentPost.tourAt
            closeAt = currentPost.closeAt
            kakaoOpenChatUrl = currentPost.kakaoOpenChatUrl
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // 원하는 높이로 직접 설정
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 뒤로가기 아이콘 버튼
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                // 제목 텍스트
                Text(
                    text = "모집글 작성",
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // 스크롤 가능하게 만듦
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("제목을 입력하세요. (50자 이내)") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 여행지 스타일 드롭다운
            PreferenceSelector(
                selectedOption = preferredDestinations,
                onOptionSelected = { preferredDestinations = it }
            )
            Spacer(modifier = Modifier.height(16.dp))


            // 여행지 지역 드롭다운
            PreferenceSelector(
                selectedOption = preferredLocation.location ?: "",
                onOptionSelected = { preferredLocation = preferredLocation.copy(location = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 여행 시작일 선택
            DatePickerFieldToModal(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = tourAt,
                onDateSelected = { tourAt = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 모집 마감일 선택
            DatePickerFieldToModal(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = closeAt,
                onDateSelected = { closeAt = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SingleDropdown(
                label = "자차 여부",
                options = listOf("예(6인승 이상)", "예(4인승)", "예(2인승)", "아니요"),
                selectedOption = hasCar,
                onOptionSelected = { hasCar = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("선호 언어 선택 및 레벨", fontSize = 16.sp)
            languages.forEach { lang ->
                val currentLevel = preferredLanguages[lang]?.level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(lang, modifier = Modifier.width(60.dp))
                    (1..10).forEach { level ->
                        val selected = currentLevel == level
                        Button(
                            onClick = {
                                preferredLanguages =
                                    if (selected) preferredLanguages - lang
                                    else preferredLanguages + (lang to LanguagePref(lang, level))
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .padding(1.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF4285F4) else Color.LightGray
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("$level", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 오픈 채팅 링크 입력창
            OutlinedTextField(
                value = kakaoOpenChatUrl,
                onValueChange = { kakaoOpenChatUrl = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                placeholder = { Text("카카오톡 오픈 채팅방 링크를 입력하세요.") },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(36.dp))

            // 내용 입력창
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // 높이를 200dp로 지정
                placeholder = { Text("내용을 입력하세요. (500자 이내)") },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            SaveButton(
                onSave = {
                    val missingFields = mutableListOf<String>()
                    if (title.isBlank()) missingFields.add("제목")
                    if (content.isBlank()) missingFields.add("내용")
                    if (preferredDestinations.isBlank()) missingFields.add("선호 여행지 스타일")
                    if (preferredLocation.location.isBlank()) missingFields.add("선호 여행지/장소")
                    if (tourAt == null) missingFields.add("여행 시작일")
                    if (closeAt == null) missingFields.add("모집 마감일")
                    if (hasCar.isBlank()) missingFields.add("자차 여부")
                    if (preferredLanguages.isEmpty()) missingFields.add("선호 언어")
                    if (kakaoOpenChatUrl.isBlank()) missingFields.add("카카오톡 오픈채팅 링크")

                    if (missingFields.isNotEmpty()) {
                        // 어떤 항목이 비었는지 Toast 또는 Alert
                        println("다음 항목을 입력해주세요: ${missingFields.joinToString(", ")}")
                    } else {
                        // 모든 항목 유효
                        val newPost = RecruitPost(
                            postId = postId ?: "",
                            title = title,
                            content = content,
                            preferredDestinations = preferredDestinations,
                            preferredLocations = preferredLocation,
                            tourAt = tourAt!!,
                            closeAt = closeAt!!,
                            hasCar = hasCar,
                            preferredLanguages = preferredLanguages,
                            kakaoOpenChatUrl = kakaoOpenChatUrl,
                            createdAt = System.currentTimeMillis(),
                            owner = UserSummary(
                                id = userState?.id ?: "",
                                nickname = userState?.nickname ?: "",
                                profileImageUrl = userState?.profileImageUrl ?: ""
                            )
                        )
                        if (type == "생성") {
                            postViewModel.createPost(newPost)
                        } else {
                            println("키키" + newPost)
                            postViewModel.savePost(newPost)
                        }
                    }
                },
                text = buttonText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var displayText by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // 중요: 메뉴 위치 잡아줌
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        displayText = option // TextField에 바로 반영
                        onOptionSelected(option) // 부모 상태 반영
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    // 드롭다운 메뉴에 표시할 아이템 목록
    val options = listOf("서울", "부산", "제주도", "강릉", "경주")
    // 드롭다운 메뉴가 펼쳐졌는지 여부를 저장하는 상태
    var isExpanded by remember { mutableStateOf(false) }
    val selectedOptionText = selectedOption

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            placeholder = { Text("선호하는 여행지 선택") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.favorite_spot),
                    contentDescription = null,
                    tint = Color(0xFF4285F4)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.down_arrow),
                    contentDescription = "드롭다운 메뉴 열기",
                    modifier = Modifier.size(16.dp),
                    tint = Color.LightGray

                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.LightGray,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            shape = RoundedCornerShape(16.dp)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .exposedDropdownSize()
                .background(Color.White)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        isExpanded = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}