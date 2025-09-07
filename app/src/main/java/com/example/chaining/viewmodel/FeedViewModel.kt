package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.model.TourItem
import com.example.chaining.data.repository.FeedRepository
import com.example.chaining.network.FeedApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val repository: FeedRepository // ✅ TourRepository를 주입받음
) : ViewModel() {

    private val _tourItems = MutableStateFlow<List<TourItem>>(emptyList())
    val tourItems: StateFlow<List<TourItem>> = _tourItems

    fun fetchTourItems(areaCode: Int? = null) {
        viewModelScope.launch {
            try {
                // ✅ Repository에 데이터 요청
                _tourItems.value = repository.getTourItems(areaCode)
            } catch (e: Exception) {
                // 에러 처리
                Log.e("TourViewModel", "Failed to fetch tour items", e)
            }
        }
    }
}