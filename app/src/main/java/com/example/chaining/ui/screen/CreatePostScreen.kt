package com.example.chaining.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.chaining.R

@Composable
fun CreatePostScreen() {
    var title by remember { mutableStateOf("") } // 제목을 저장할 상태 변수
    var content by remember { mutableStateOf("") } // 내용을 위한 상태 변수

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // 원하는 높이로 직접 설정
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                // 내부 요소들을 세로 중앙에 정렬
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
                    modifier = Modifier.weight(1f), // 3. 남는 공간을 모두 차지
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center // 4. 텍스트를 가운데 정렬
                )

                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        // 전체 화면 배경색 설정
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        // 스크롤 가능한 Column으로 콘텐츠 영역 설정
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp) // 좌우, 상하 여백
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

            // '선호하는 여행지 선택' 드롭다운 메뉴 추가
            PreferenceSelector()
            Spacer(modifier = Modifier.height(16.dp))
            PreferenceSelector()
            Spacer(modifier = Modifier.height(16.dp))
            PreferenceSelector()
            Spacer(modifier = Modifier.height(16.dp))
            PreferenceSelector()

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

            Button(
                onClick = { /* TODO: 작성 완료 로직 구현 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                    //.padding(bottom = 16.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4) // 파란색 배경
                )
            ) {
                Text(text = "작성 완료", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSelector() {
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