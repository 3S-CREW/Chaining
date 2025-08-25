package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.RecruitPostRepository
import com.example.chaining.domain.model.RecruitPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitPostViewModel @Inject constructor(
    private val repo: RecruitPostRepository
) : ViewModel() {
    private val _post = MutableStateFlow<RecruitPost?>(null)
    val post: StateFlow<RecruitPost?> = _post

    private val _posts = MutableStateFlow<List<RecruitPost>>(emptyList())
    val posts: StateFlow<List<RecruitPost>> = _posts

    init {
        fetchAllPosts()
    }

    /** Create - 모집글 등록 */
    fun createPost(post: RecruitPost) = viewModelScope.launch {
        repo.createPost(post)
        fetchAllPosts()
    }

    /** Read - 모집글 새로고침 */
    fun fetchAllPosts() = viewModelScope.launch {
        try {
            _posts.value = repo.getAllPosts()
            Log.d("PostVM", "Fetched posts: ${_posts.value.size}")
        } catch (e: Exception) {
            Log.e("PostVM", "Failed to fetch posts", e)
        }
    }

    /** Update - 전체 User 객체 저장 */
    fun savePost() = viewModelScope.launch {
        try {
            _post.value?.let {
                repo.savePost(it)
                fetchAllPosts()
            }
        } catch (e: Exception) {
            Log.e("PostVM", "Failed to save post", e)
        }
    }

    /** Delete - Soft Delete */
    fun deletePost() = viewModelScope.launch {
        _post.value?.postId?.let { pid ->
            repo.deletePost(pid)
            fetchAllPosts()
        }
    }
}