@file:Suppress("ktlint:standard:property-naming")

package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.UserRepository
import com.example.chaining.domain.model.User
import com.example.chaining.domain.model.UserSummary
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        auth: FirebaseAuth,
        private val repo: UserRepository,
    ) : ViewModel() {
        // 내 User 정보 (null일 수도 있음)
        private val _user = MutableStateFlow<User?>(null)
        val user: StateFlow<User?> = _user

        private val _toastEvent = MutableSharedFlow<String>()
        val toastEvent = _toastEvent.asSharedFlow()

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

        fun checkUserExists(
            uid: String,
            callback: (Boolean) -> Unit,
        ) {
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

        fun updateNickname(newNickname: String) {
            viewModelScope.launch {
                try {
                    repo.updateNickname(newNickname)
                    _user.value = _user.value?.copy(nickname = newNickname)
                } catch (e: Exception) {
                    Log.e("UserViewModel", "닉네임 업데이트 실패", e)
                }
            }
        }

        /** Create - 최초 회원가입 시 User 등록 */
        fun addUser(
            user: User,
            onComplete: () -> Unit,
        ) = viewModelScope.launch {
            repo.addUser(user)
            onComplete()
        }

        /** Update - 전체 User 객체 저장 */
        fun updateMyUser(user: User) =
            viewModelScope.launch {
                repo.updateMyUser(user)
            }

        fun toggleLike(postId: String) =
            viewModelScope.launch {
//        _user.value?.id?.let { uid ->
//            repo.toggleLikedPost(uid, postId)
//        }
                val uid = _user.value?.id ?: ""

                // 1. 낙관적 업데이트: UI 먼저 반영
                val currentLiked = _user.value?.likedPosts?.get(postId) == true
                val newLikedPosts = _user.value?.likedPosts?.toMutableMap() ?: mutableMapOf()
                if (currentLiked) newLikedPosts.remove(postId) else newLikedPosts[postId] = true

                _user.value = _user.value?.copy(likedPosts = newLikedPosts)

                // 2. DB 업데이트

                try {
                    repo.toggleLikedPost(uid, postId)
                } catch (e: Exception) {
                    // DB 실패 시, 낙관적 업데이트 되돌리기
                    if (currentLiked) newLikedPosts[postId] = true else newLikedPosts.remove(postId)
                    _user.value = _user.value?.copy(likedPosts = newLikedPosts)
                }
            }

        fun toggleFollow(
            user: UserSummary,
            other: UserSummary,
        ) = viewModelScope.launch {
            val result = repo.toggleFollow(user, other)
            println("호시기 누름")
            result.onFailure { exception ->
                _toastEvent.emit(exception.message ?: "작업에 실패했습니다.")
            }
        }

        /** Delete - Soft Delete */
        fun deleteUser() =
            viewModelScope.launch {
                repo.deleteMyUser()
            }
    }
