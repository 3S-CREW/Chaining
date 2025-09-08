package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.model.TourItem
import com.example.chaining.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository // TourRepository를 주입받음
) : ViewModel() {

    // API로 가져온 전체 관광지 리스트 (비공개)
    private val _tourItems = MutableStateFlow<List<TourItem>>(emptyList())
    val tourItems: StateFlow<List<TourItem>> = _tourItems

    // UI에 보여줄 랜덤 3개의 관광지 리스트 (공개)
    private val _randomizedFeedItems = MutableStateFlow<List<TourItem>>(emptyList())
    val randomizedFeedItems: StateFlow<List<TourItem>> = _randomizedFeedItems

    fun fetchTourItems(areaCode: Int? = null) {
        viewModelScope.launch {
            try {
                val items = repository.getTourItems(areaCode)
                _tourItems.value = items
                // 데이터를 성공적으로 불러온 후, 바로 랜덤 아이템 선택 함수 호출
                randomizeFeedItems()
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Failed to fetch tour items", e)
            }
        }
    }

    // 전체 리스트에서 3개의 아이템을 랜덤으로 선택하여 상태를 업데이트하는 함수
    fun randomizeFeedItems() {
        if (_tourItems.value.isNotEmpty()) {
            _randomizedFeedItems.value = _tourItems.value.shuffled().take(3)
        }
    }
}