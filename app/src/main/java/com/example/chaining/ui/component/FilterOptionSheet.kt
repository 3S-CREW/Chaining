package com.example.chaining.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.data.model.FilterState
import com.example.chaining.ui.screen.SecondaryTextColor
import com.example.chaining.viewmodel.AreaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName")
@Composable
fun FilterOptionsSheet(
    currentFilterState: FilterState,
    onApplyFilters: (FilterState) -> Unit,
    onClose: () -> Unit,
    areaViewModel: AreaViewModel = hiltViewModel(),
) {
    val areaEntities by areaViewModel.areaCodes.collectAsState()
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
    val travelStyles =
        listOf(
            stringResource(id = R.string.travel_style_mountain),
            stringResource(id = R.string.travel_style_sea),
            stringResource(id = R.string.travel_style_city),
            stringResource(id = R.string.travel_style_activity),
            stringResource(id = R.string.travel_style_rest),
            stringResource(id = R.string.travel_style_culture),
        )
    val travelLocations =
        remember(areaEntities) {
            areaEntities
                .map { it.regionName }
        }
    val languages =
        listOf(
            stringResource(id = R.string.language_korean),
            stringResource(id = R.string.language_english),
        )
    val languageLevels = (1..10).toList()
    val sortByOptions =
        mapOf(
            "latest" to stringResource(id = R.string.sort_by_latest),
            "deadline" to stringResource(id = R.string.sort_by_deadline),
            // "interest" to "관심순"
        )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .background(Color(0xFFF3F6FF))
                // 스크롤 가능하도록 추가
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 헤더 및 닫기 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.filter_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A526A),
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 여행지 스타일 드롭다운
        FilterDropdown(
            label = stringResource(id = R.string.filter_placeholder_travel_style),
            selectedValue = selectedTravelStyle,
            options = travelStyles,
            expanded = expandedTravelStyle,
            onExpandedChange = { expandedTravelStyle = it },
            onValueChange = { value -> selectedTravelStyle = value },
            leadingIconRes = R.drawable.global,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 여행지 드롭다운
        FilterDropdown(
            label = stringResource(id = R.string.filter_placeholder_travel_location),
            selectedValue = selectedTravelLocation,
            options = travelLocations,
            expanded = expandedTravelLocation,
            onExpandedChange = { expandedTravelLocation = it },
            onValueChange = { value -> selectedTravelLocation = value },
            leadingIconRes = R.drawable.country,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 언어 드롭다운
        FilterDropdown(
            label = stringResource(id = R.string.filter_placeholder_language),
            selectedValue = selectedLanguage,
            options = languages,
            expanded = expandedLanguage,
            onExpandedChange = { expandedLanguage = it },
            onValueChange = { value -> selectedLanguage = value },
            leadingIconRes = R.drawable.language,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 언어 레벨 드롭다운
        FilterDropdown(
            label = stringResource(id = R.string.filter_placeholder_language_level),
            // Int? -> String? 변환
            selectedValue = selectedLanguageLevel?.toString(),
            // "상관 없음" 추가
            options = (listOf(stringResource(id = R.string.filter_option_any)) + languageLevels.map { it.toString() }),
            expanded = expandedLanguageLevel,
            onExpandedChange = { expandedLanguageLevel = it },
            onValueChange = { value ->
                selectedLanguageLevel = if (value == "상관 없음") null else value?.toIntOrNull()
            },
            leadingIconRes = R.drawable.level,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 정렬 방식 드롭다운
        FilterDropdown(
            label = stringResource(id = R.string.filter_placeholder_sort_by),
            // 맵에서 값 가져오기
            selectedValue = sortByOptions[selectedSortBy],
            // 옵션은 표시할 텍스트 리스트
            options = sortByOptions.values.toList(),
            expanded = expandedSortBy,
            onExpandedChange = { expandedSortBy = it },
            onValueChange = { value ->
                // 표시된 텍스트(value)로 실제 키를 찾아 저장
                selectedSortBy = sortByOptions.entries.find { it.value == value }?.key ?: "latest"
            },
            leadingIconRes = R.drawable.sort,
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
                        sortBy = selectedSortBy,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A526A)),
            contentPadding = PaddingValues(12.dp),
        ) {
            Text(
                stringResource(id = R.string.filter_apply_button),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    selectedValue: String?,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onValueChange: (String?) -> Unit,
    @DrawableRes leadingIconRes: Int,
) {
    Spacer(modifier = Modifier.height(8.dp))
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextField(
            value = selectedValue ?: stringResource(id = R.string.filter_option_none),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = label,
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIconRes),
                    contentDescription = label,
                    tint = SecondaryTextColor,
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            colors =
                ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF7282B4),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier =
                Modifier
                    .exposedDropdownSize()
                    .background(Color.White),
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.filter_option_none)) },
                onClick = {
                    onValueChange(null)
                    onExpandedChange(false)
                },
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    },
                )
            }
        }
    }
}
