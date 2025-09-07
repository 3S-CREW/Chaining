package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.RecruitPostRepository
import com.example.chaining.domain.model.RecruitPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _posts = MutableStateFlow<List<RecruitPost>>(emptyList())
    val posts: StateFlow<List<RecruitPost>> = _posts

    private var lastFetchTime = 0L
    private val fetchInterval = 10 * 60 * 1000L // 10분(ms)

    init {
        // 앱 실행 시 한 번 초기 데이터 가져오기
        fetchAllPosts(force = true)

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
    fun refreshPosts() = viewModelScope.launch {
        fetchAllPosts(force = true)
    }

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

    /** Read - 모집글 새로고침 */
    fun fetchAllPosts(force: Boolean = false) = viewModelScope.launch {
        val currentTime = System.currentTimeMillis()

        if (force) {
            try {
                _posts.value = repo.getAllPosts()
                lastFetchTime = currentTime
            } catch (e: Exception) {
                Log.e("PostVM", "Failed to fetch posts", e)
            }
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