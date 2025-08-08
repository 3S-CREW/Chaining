package com.example.chaining.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.model.AreaCodeResponse.AreaCodeResponse.AreaCodeBody.AreaCodeItems.AreaCodeItem
import com.example.chaining.data.repository.AreaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AreaViewModel @Inject constructor(
    private val repository: AreaRepository
) : ViewModel() {

    // 상태 보관 (외부에는 읽기 전용으로 제공)
    private val _areaCodes = mutableStateOf<List<AreaCodeItem>>(emptyList())
    val areaCodes: State<List<AreaCodeItem>> = _areaCodes

    init {
        viewModelScope.launch {
            try {
                // Repository에서 데이터 가져옴
                _areaCodes.value = repository.fetchAreaCodes()
            } catch (e: Exception) {
                Log.e("AreaViewModel", "지역 코드 로드 실패", e)
            }
        }
    }
}
