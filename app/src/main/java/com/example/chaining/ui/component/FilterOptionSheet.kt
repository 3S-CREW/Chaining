package com.example.chaining.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaining.data.model.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOptionsSheet(
    currentFilterState: FilterState,
    onApplyFilters: (FilterState) -> Unit,
    onClose: () -> Unit
) {
    var selectedTravelStyle by remember { mutableStateOf(currentFilterState.travelStyle) }
    var selectedTravelLocation by remember { mutableStateOf(currentFilterState.travelLocation) }
    var selectedLanguage by remember { mutableStateOf(currentFilterState.language) }
    var selectedLanguageLevel by remember { mutableStateOf(currentFilterState.languageLevel) }
    var selectedSortBy by remember { mutableStateOf(currentFilterState.sortBy) }

    // 드롭다운 메뉴 상태
    var expandedTravelStyle by remember { mutableStateOf(false) }
    var expandedTravelLocation by remember { mutableStateOf(false) }
    var expandedLanguage by remember { mutableStateOf(false) }
    var expandedLanguageLevel by remember { mutableStateOf(false) }
    var expandedSortBy by remember { mutableStateOf(false) }

    // 드롭다운 옵션 목록
    val travelStyles = listOf("서울", "부산", "제주도", "강릉", "경주")
    val travelLocations = listOf("산", "계곡", "바다", "도심")
    val languages = listOf("한국어", "영어", "중국어", "일본어")
    val languageLevels = (1..10).toList() // 1부터 5까지
    val sortByOptions = mapOf(
        "latest" to "최신순",
        "deadline" to "마감순",
        // "interest" to "관심순"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .background(Color(0xFFF3F6FF))
            .verticalScroll(rememberScrollState()), // 스크롤 가능하도록 추가
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 헤더 및 닫기 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "모집 글 필터 설정",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A526A)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 여행지 스타일 드롭다운
        FilterDropdown(
            label = "여행지 스타일",
            selectedValue = selectedTravelStyle,
            options = travelStyles,
            expanded = expandedTravelStyle,
            onExpandedChange = { expandedTravelStyle = it },
            onValueChange = { value -> selectedTravelStyle = value }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 여행지 드롭다운
        FilterDropdown(
            label = "여행지",
            selectedValue = selectedTravelLocation,
            options = travelLocations,
            expanded = expandedTravelLocation,
            onExpandedChange = { expandedTravelLocation = it },
            onValueChange = { value -> selectedTravelLocation = value }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 언어 드롭다운
        FilterDropdown(
            label = "언어",
            selectedValue = selectedLanguage,
            options = languages,
            expanded = expandedLanguage,
            onExpandedChange = { expandedLanguage = it },
            onValueChange = { value -> selectedLanguage = value }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 언어 레벨 드롭다운
        FilterDropdown(
            label = "언어 레벨",
            selectedValue = selectedLanguageLevel?.toString(), // Int? -> String? 변환
            options = (listOf("상관 없음") + languageLevels.map { it.toString() }), // "상관 없음" 추가
            expanded = expandedLanguageLevel,
            onExpandedChange = { expandedLanguageLevel = it },
            onValueChange = { value ->
                selectedLanguageLevel = if (value == "상관 없음") null else value?.toIntOrNull()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 정렬 방식 드롭다운
        FilterDropdown(
            label = "정렬 방식",
            selectedValue = sortByOptions[selectedSortBy], // 맵에서 값 가져오기
            options = sortByOptions.values.toList(), // 옵션은 표시할 텍스트 리스트
            expanded = expandedSortBy,
            onExpandedChange = { expandedSortBy = it },
            onValueChange = { value ->
                // 표시된 텍스트(value)로 실제 키를 찾아 저장
                selectedSortBy = sortByOptions.entries.find { it.value == value }?.key ?: "latest"
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 필터 적용 버튼
        Button(
            onClick = {
                onApplyFilters(
                    FilterState(
                        travelStyle = selectedTravelStyle,
                        travelLocation = selectedTravelLocation,
                        language = selectedLanguage,
                        languageLevel = selectedLanguageLevel,
                        sortBy = selectedSortBy
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A526A)),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("필터 적용", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ✅ 드롭다운을 위한 재사용 가능한 컴포저블
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    selectedValue: String?,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onValueChange: (String?) -> Unit
) {
    Text(label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF4A526A), modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue ?: "선택 안 함",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF7282B4),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("선택 안 함") },
                onClick = {
                    onValueChange(null)
                    onExpandedChange(false)
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}