package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.model.FilterState
import com.example.chaining.data.repository.RecruitPostRepository
import com.example.chaining.domain.model.RecruitPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitPostViewModel @Inject constructor(
    private val repo: RecruitPostRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _post = MutableStateFlow<RecruitPost?>(null)
    val post: StateFlow<RecruitPost?> = _post

    // ✅ 1. 원본 게시글 전체 목록 (비공개)
    private val _allPosts = MutableStateFlow<List<RecruitPost>>(emptyList())

    // ✅ 2. 현재 필터 상태를 저장하는 StateFlow
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    // ✅ 3. UI에 보여줄 최종 필터링된 게시글 목록 (공개)
    val posts: StateFlow<List<RecruitPost>> =
        combine(_allPosts, _filterState) { allPosts, filter ->
            applyFiltering(allPosts, filter)
        }.stateIn(
            scope = viewModelScope,
            // ✅ Eagerly: ViewModel이 살아있는 동안 즉시 Flow를 시작하고 공유
            started = SharingStarted.Eagerly,
            initialValue = emptyList() // 초기값
        )

    private var lastFetchTime = 0L
    private val fetchInterval = 10 * 60 * 1000L // 10분(ms)

    init {
        // 앱 실행 시 한 번 초기 데이터 가져오기
        fetchAllPosts()

        savedStateHandle.get<String>("postId")?.let { postId ->
            fetchPost(postId)
        }

        // 10분마다 자동 갱신
        viewModelScope.launch {
            while (isActive) {
                delay(fetchInterval)
                fetchAllPosts(force = true)
            }
        }
    }

    // 새로고침 버튼용 함수
    // refreshPosts 함수는 fetchAllPosts()를 호출하도록 유지
    fun refreshPosts() = fetchAllPosts()

    /** Create - 모집글 등록 */
    fun createPost(post: RecruitPost) = viewModelScope.launch {
        Log.d("PostPost", post.toString())
        repo.createPost(post)
        fetchAllPosts(force = true)
    }
//            || currentTime - lastFetchTime >= fetchInterval

    /** Read - 모집글 상세보기 */
    fun fetchPost(postId: String) = viewModelScope.launch {
        try {
            _post.value = repo.getPost(postId)
        } catch (e: Exception) {
            Log.e("PostVM", "Failed to fetch post", e)
        }
    }

    /**
     * ✅ 4. UI에서 필터 상태를 업데이트하기 위해 호출하는 함수
     */
    fun applyFilters(newFilterState: FilterState) {
        _filterState.value = newFilterState
    }

    /**
     * ✅ 5. 실제 필터링 및 정렬 로직을 수행하는 함수
     */
    private fun applyFiltering(posts: List<RecruitPost>, filter: FilterState): List<RecruitPost> {
        Log.d("FilterDebug", "--- 필터링 시작 ---")
        Log.d("FilterDebug", "1. 원본 게시글 개수: ${posts.size}")
        Log.d("FilterDebug", "적용할 필터: $filter")

        var filteredList = posts.filter { !it.isDeleted }
        Log.d("FilterDebug", "2. isDeleted 필터 후: ${filteredList.size} 개")

        filter.travelStyle?.let { style ->
            filteredList = filteredList.filter { it.preferredDestinations == style }
            Log.d("FilterDebug", "3. 여행 스타일(${style}) 필터 후: ${filteredList.size} 개")
        }
        filter.travelLocation?.let { location ->
            filteredList = filteredList.filter {
                it.preferredLocations.location.contains(
                    location,
                    ignoreCase = true
                )
            }
            Log.d("FilterDebug", "4. 여행지(${location}) 필터 후: ${filteredList.size} 개")
        }
        filter.language?.let { lang ->
            filteredList =
                filteredList.filter { p -> p.preferredLanguages.any { it.value.language == lang } }
            Log.d("FilterDebug", "5. 언어(${lang}) 필터 후: ${filteredList.size} 개")
        }
        filter.languageLevel?.let { level ->
            filteredList =
                filteredList.filter { p -> p.preferredLanguages.any { it.value.level >= level } }
            Log.d("FilterDebug", "6. 언어 레벨(>=${level}) 필터 후: ${filteredList.size} 개")
        }
        Log.d("FilterDebug", "--- 필터링 종료, 최종 개수: ${filteredList.size} 개 ---")

        return when (filter.sortBy) {
            "latest" -> filteredList.sortedByDescending { it.createdAt }
            "deadline" -> filteredList.sortedBy { it.closeAt }
            // "interest" -> filteredList.sortedByDescending { it.whoLiked.size }
            else -> filteredList
        }
    }

    /** Read - 모집글 새로고침 */
    fun fetchAllPosts(force: Boolean = false) = viewModelScope.launch {
        val currentTime = System.currentTimeMillis()
        try {
            _allPosts.value = repo.getAllPosts()
            lastFetchTime = currentTime
        } catch (e: Exception) {
            Log.e("PostVM", "Failed to fetch posts", e)
        }
    }

    /** Update - 전체 User 객체 저장 */
    fun savePost(post: RecruitPost) = viewModelScope.launch {
        try {
            repo.savePost(post)
            fetchAllPosts(force = true)
//            _post.value?.let {
//                repo.savePost(it)
//                fetchAllPosts(force = true)
//            }
        } catch (e: Exception) {
            Log.e("PostVM", "Failed to save post", e)
        }
    }

    /** Delete - Soft Delete */
    fun deletePost() = viewModelScope.launch {
        _post.value?.postId?.let { pid ->
            repo.deletePost(pid)
            fetchAllPosts(force = true)
        }
    }
}