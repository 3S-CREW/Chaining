package com.example.chaining.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.viewmodel.AreaViewModel

@Composable
fun AreaScreen(viewModel: AreaViewModel = hiltViewModel()) {
    val areaCodes by viewModel.areaCodes

    Column(modifier = Modifier.padding(16.dp)) {
        // 테이블 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
                .padding(8.dp)
        ) {
            TableCell("No.", Modifier.weight(0.5f))
            TableCell("시/도", Modifier.weight(1f))
            TableCell("구 이름", Modifier.weight(1f))
            TableCell("구 코드", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 테이블 내용
        LazyColumn {
            items(areaCodes) { area ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    TableCell(area.rnum.toString(), Modifier.weight(0.5f))
                    TableCell(area.lDongRegnNm, Modifier.weight(1f))
                    TableCell(area.lDongSignguNm, Modifier.weight(1f))
                    TableCell(area.lDongSignguCd.toString(), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(4.dp)
    )
}
