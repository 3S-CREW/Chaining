package com.example.chaining.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import com.example.chaining.domain.model.LocationPref
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary
import com.example.chaining.ui.component.DatePickerFieldToModal
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel

@Composable
fun CreatePostScreen(
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()

) {
    val postState by postViewModel.post.collectAsState()
    val userState by userViewModel.user.collectAsState()

    var title by remember { mutableStateOf(userState?.nickname ?: "") }
    var content by remember { mutableStateOf("") }
    var preferredDestinations by remember { mutableStateOf("") }
    var preferredLocation by remember { mutableStateOf<LocationPref>(LocationPref()) }
    var preferredLanguages by remember {
        mutableStateOf(
            userState?.preferredLanguages ?: emptyList()
        )
    }
    var hasCar by remember { mutableStateOf("") }
    var tourAt by remember { mutableStateOf<Long?>(null) }
    var closeAt by remember { mutableStateOf<Long?>(null) }
    var kakaoOpenChatUrl by remember { mutableStateOf("") }

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
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
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

            // 내용 입력창
            Spacer(modifier = Modifier.height(16.dp))
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

            Spacer(modifier = Modifier.height(36.dp))
            // 여행 시작일 선택
            DatePickerFieldToModal(
                modifier = Modifier.fillMaxWidth(),
                // 현재 상태 값
                selectedDate = tourAt,
                // 날짜 선택 시 업데이트
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
            SaveButton(
                onSave = {
                    val newPost = RecruitPost(
                        postId = "",
                        title = title,
                        content = content,
                        preferredDestinations = preferredDestinations,
                        preferredLocations = LocationPref(location = preferredLocation.location),
                        tourAt = tourAt ?: System.currentTimeMillis(),
                        hasCar = hasCar,
                        closeAt = closeAt ?: System.currentTimeMillis(),
                        preferredLanguages = preferredLanguages,
                        kakaoOpenChatUrl = kakaoOpenChatUrl,
                        createdAt = System.currentTimeMillis(),
                        owner = UserSummary(
                            id = userState?.id ?: "",
                            nickname = userState?.nickname ?: "",
                            profileImageUrl = userState?.profileImageUrl ?: ""
                        )
                    )
                    postViewModel.createPost(newPost)
                }
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

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val dateFormatter = remember { java.text.SimpleDateFormat("yyyy-MM-dd") }

    // 선택된 날짜를 yyyy-MM-dd 형식으로 변환
    val formattedDate = selectedDate?.let { dateFormatter.format(it) } ?: ""

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog.value = true }
    )

    if (showDialog.value) {
        // Material3 DatePickerDialog
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            onDateSelected(selectedMillis)
                        }
                        showDialog.value = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
    // 현재 선택된 항목을 저장하는 상태
    var selectedOptionText by remember { mutableStateOf("") }

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
                        selectedOptionText = selectionOption
                        isExpanded = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}

@Composable
fun SaveButton(onSave: () -> Unit) {
    Button(
        onClick = onSave,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4)
        )
    ) {
        Text(text = "작성 완료", fontSize = 16.sp)
    }
}