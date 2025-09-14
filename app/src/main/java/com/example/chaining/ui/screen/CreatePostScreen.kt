package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary
import com.example.chaining.ui.component.DatePickerFieldToModal
import com.example.chaining.ui.component.SaveButton
import com.example.chaining.viewmodel.AreaViewModel
import com.example.chaining.viewmodel.PostCreationEvent
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

private const val MAX_TITLE_LENGTH = 20
private const val MAX_CONTENT_LENGTH = 300

@Composable
fun CreatePostScreen(
    postId: String? = null,
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onPostCreated: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    type: String, // "생성" or "수정"
    areaViewModel: AreaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val areaEntities by areaViewModel.areaCodes.collectAsState()
    val userState by userViewModel.user.collectAsState()
    val postState by postViewModel.post.collectAsState()
    val postCreationSuccess by postViewModel.postCreationSuccess.collectAsState()

    LaunchedEffect(key1 = type, key2 = postId) {
        if (type == "수정" && postId != null) {
            postViewModel.fetchPost(postId)
        }
    }


    // ✅ ViewModel의 이벤트를 구독하고 Toast 메시지를 표시
    LaunchedEffect(Unit) {
        postViewModel.postCreationEvent.collectLatest { event ->
            val message = when (event) {
                is PostCreationEvent.Success -> {
                    // 성공 시 strings.xml에서 성공 메시지를 가져옴
                    context.getString(R.string.post_creation_success)
                }

                is PostCreationEvent.Failure -> {
                    // 실패 시 strings.xml에서 실패 메시지 형식을 가져와 조합
                    val errorMessage = event.message ?: context.getString(R.string.unknown_error)
                    context.getString(R.string.post_creation_failed, errorMessage)
                }
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    var title by remember { mutableStateOf(userState?.nickname ?: "") }
    var content by remember { mutableStateOf("") }
    var preferredDestinations by remember { mutableStateOf("") }
    var preferredLocations by remember { mutableStateOf("") }
    var preferredLanguages by remember {
        mutableStateOf(
            userState?.preferredLanguages ?: emptyMap()
        )
    }
    var hasCar by remember { mutableStateOf("") }
    var tourAt by remember { mutableStateOf<Long?>(null) }
    var closeAt by remember { mutableStateOf<Long?>(null) }
    var kakaoOpenChatUrl by remember { mutableStateOf("") }

    val buttonText = if (type == "생성") stringResource(id = R.string.post_write_button)
    else stringResource(id = R.string.post_edit_button)

    val fieldTitleText = stringResource(id = R.string.post_title)
    val fieldContentText = stringResource(id = R.string.post_content)
    val fieldTravelStyleText = stringResource(id = R.string.post_travel_style)
    val fieldLocationText = stringResource(id = R.string.post_location)
    val fieldTourDateText = stringResource(id = R.string.post_tour_date)
    val fieldCloseDateText = stringResource(id = R.string.post_close_date)
    val fieldCarText = stringResource(id = R.string.post_car)
    val fieldKakaoLinkText = stringResource(id = R.string.post_kakao_link)
    val validationPleaseEnterText = stringResource(id = R.string.post_please_enter)
    val validationInvalidKakaoLinkText = stringResource(id = R.string.post_invalid_kakao_link)

    LaunchedEffect(postCreationSuccess) {
        if (postCreationSuccess) {
            onPostCreated() // NavGraph에 정의된 화면 이동 로직 실행
            postViewModel.onPostCreationHandled() // 상태 초기화
        }
    }

    LaunchedEffect(userState) {
        if (type == "생성") {
            userState?.let { user ->
                preferredDestinations = user.preferredDestinations
                preferredLanguages = user.preferredLanguages
            }
        }
    }

    LaunchedEffect(postState) {
        val currentPost = postState
        if (type == "수정" && currentPost != null) {
            title = currentPost.title
            content = currentPost.content
            preferredDestinations = currentPost.preferredDestinations
            preferredLocations = currentPost.preferredLocations
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
                    text = stringResource(id = R.string.post_write_title),
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
                onValueChange = { if (it.length <= MAX_TITLE_LENGTH) title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.post_write_enter_title)) },
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
                ),
                supportingText = {
                    Text(
                        text = "${title.length} / $MAX_TITLE_LENGTH",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 여행지 스타일 드롭다운
            val travelStyles = listOf(
                stringResource(id = R.string.travel_style_mountain),
                stringResource(id = R.string.travel_style_sea),
                stringResource(id = R.string.travel_style_city),
                stringResource(id = R.string.travel_style_activity),
                stringResource(id = R.string.travel_style_rest),
                stringResource(id = R.string.travel_style_culture)
            )
            PreferenceSelector(
                options = travelStyles,
                placeholderText = stringResource(id = R.string.post_write_style),
                selectedOption = preferredDestinations,
                onOptionSelected = { preferredDestinations = it }
            )
            Spacer(modifier = Modifier.height(16.dp))


            // 여행 지역 드롭다운
            val areaNames = remember(areaEntities) {
                areaEntities
                    .map { it.regionName }
            }
            PreferenceSelector(
                options = areaNames,
                placeholderText = stringResource(id = R.string.post_write_location),
                selectedOption = preferredLocations,
                onOptionSelected = { selectedName ->
                    preferredLocations = selectedName
                }
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
                label = stringResource(id = R.string.post_write_car),
                options = listOf(
                    stringResource(id = R.string.post_write_car_six),
                    stringResource(id = R.string.post_write_car_four),
                    stringResource(id = R.string.post_write_car_two),
                    stringResource(id = R.string.post_write_no)
                ),
                selectedOption = hasCar,
                onOptionSelected = { hasCar = it }
            )

            Spacer(modifier = Modifier.height(20.dp))
            // 오픈 채팅 링크 입력창
            OutlinedTextField(
                value = kakaoOpenChatUrl,
                onValueChange = { kakaoOpenChatUrl = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                placeholder = { Text(stringResource(id = R.string.post_write_kakao)) },
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
                onValueChange = { if (it.length <= MAX_CONTENT_LENGTH) content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text(stringResource(id = R.string.post_write)) },
                shape = RoundedCornerShape(16.dp),
                supportingText = {
                    Text(
                        text = "${content.length} / $MAX_CONTENT_LENGTH",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
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
                    if (title.isBlank()) missingFields.add(fieldTitleText)
                    if (content.isBlank()) missingFields.add(fieldContentText)
                    if (preferredDestinations.isBlank()) missingFields.add(fieldTravelStyleText)
                    if (preferredLocations.isBlank()) missingFields.add(fieldLocationText)
                    if (tourAt == null) missingFields.add(fieldTourDateText)
                    if (closeAt == null) missingFields.add(fieldCloseDateText)
                    if (hasCar.isBlank()) missingFields.add(fieldCarText)
                    val kakaoUrl = kakaoOpenChatUrl.trim()
                    if (kakaoUrl.isBlank()) {
                        missingFields.add(fieldKakaoLinkText)
                    } else if (!kakaoUrl.startsWith("https://open.kakao.com/o/")) {
                        Toast.makeText(
                            context,
                            validationInvalidKakaoLinkText,
                            Toast.LENGTH_LONG
                        ).show()
                        return@SaveButton
                    }

                    if (missingFields.isNotEmpty()) {
                        // 어떤 항목이 비었는지 Toast 또는 Alert
                        val missingFieldsString = missingFields.joinToString(", ")
                        Toast.makeText(
                            context,
                            String.format(validationPleaseEnterText, missingFieldsString),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // 모든 항목 유효
                        val newPost = RecruitPost(
                            postId = postId ?: "",
                            title = title,
                            content = content,
                            preferredDestinations = preferredDestinations,
                            preferredLocations = preferredLocations,
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
    options: List<String>,
    placeholderText: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            placeholder = { Text(placeholderText) },
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