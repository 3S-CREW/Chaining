package com.example.chaining.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaining.R
import com.example.chaining.domain.model.LanguagePref
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestButton(
    preferredLanguages: List<LanguagePref>
) {
    val languageText = if (preferredLanguages.isNotEmpty()) {
        preferredLanguages.joinToString(" · ") {
            "${it.language} Lv.${it.level}"
        }
    } else {
        "선호하는 언어 수준 (테스트 미응시)"
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }

    Button(
        onClick = { isSheetOpen = true },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFF637387),
                shape = RoundedCornerShape(12.dp)
            ),
        contentPadding = PaddingValues(
            vertical = 14.dp,
            horizontal = 12.dp
        ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.voice_recognition),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = languageText,
            color = Color(0xFF637387),
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "언어 테스트 현황",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                val supportedLanguages = listOf("한국어", "영어", "일본어", "중국어")

                supportedLanguages.forEach { language ->
                    val pref = preferredLanguages.find { it.language == language }
                    LanguageTestItem(
                        language = language,
                        level = pref?.level,
                        onTestClick = {
                            // TODO: 테스트 화면 이동
                            // navController.navigate("test/$language")
                            coroutineScope.launch {
                                sheetState.hide()
                                isSheetOpen = false
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LanguageTestItem(
    language: String,
    level: Int?,
    onTestClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = language,
                color = Color(0xFF637387),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (level != null && level > 0) {
                LinearProgressIndicator(
                    progress = level / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF637387),
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Lv.$level / 10",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "테스트 미응시",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = onTestClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (level != null && level > 0) Color(0xFF637387) else Color(
                    0xFF4CAF50
                )
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (level != null && level > 0) "재응시" else "응시하기",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
