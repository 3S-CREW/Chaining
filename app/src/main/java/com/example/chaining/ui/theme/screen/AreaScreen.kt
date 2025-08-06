package com.example.chaining.ui.theme.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.viewmodel.AreaViewModel

@Composable
fun AreaScreen(viewModel: AreaViewModel = hiltViewModel()) {
    val areaCodes by viewModel.areaCodes

    LazyColumn {
        items(areaCodes) { area ->
            Text(
                text = "${area.lDongRegnNm} (${area.lDongRegnCd})",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
