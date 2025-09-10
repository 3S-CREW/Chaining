package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.UserRepository
import com.example.chaining.domain.model.User
import com.example.chaining.domain.model.UserSummary
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val repo: UserRepository
) : ViewModel() {

    // 내 User 정보 (null일 수도 있음)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        // 앱 시작 시 내 계정 실시간 구독
        // 로그인 상태를 먼저 확인
        if (auth.currentUser != null) {
            viewModelScope.launch {
                repo.observeMyUser().collect { newUser ->
                    _user.value = newUser
                }
            }
        }

    }

    fun checkUserExists(uid: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = repo.checkUserExists(uid)
            callback(exists)
        }
    }

    fun updateProfileImage(newUrl: String) {
        viewModelScope.launch {
            try {
                repo.updateProfileImage(newUrl)
                _user.value = _user.value?.copy(profileImageUrl = newUrl)
            } catch (e: Exception) {
                Log.e("UserViewModel", "프로필 이미지 업데이트 실패", e)
            }
        }
    }

    /** Create - 최초 회원가입 시 User 등록 */
    fun addUser(user: User) = viewModelScope.launch {
        repo.addUser(user)
    }

    /** Update - 전체 User 객체 저장 */
    fun updateMyUser(user: User) = viewModelScope.launch {
        repo.updateMyUser(user)
    }

    fun toggleLike(postId: String) = viewModelScope.launch {
        _user.value?.id?.let { uid ->
            repo.toggleLikedPost(uid, postId)
        }
    }

    fun toggleFollow(user: UserSummary, other: UserSummary) = viewModelScope.launch {
        repo.toggleFollow(user, other)
    }

    /** Delete - Soft Delete */
    fun deleteUser() = viewModelScope.launch {
        repo.deleteMyUser()
    }
}