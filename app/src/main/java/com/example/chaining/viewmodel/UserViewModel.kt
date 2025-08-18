package com.example.chaining.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.UserRepository
import com.example.chaining.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    // 내 User 정보 (null일 수도 있음)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        // 앱 시작 시 내 계정 실시간 구독
        viewModelScope.launch {
            repo.observeMyUsers().collect { newUser ->
                _user.value = newUser
            }
        }
    }

    /** Create - 최초 회원가입 시 User 등록 */
    fun addUser(user: User) = viewModelScope.launch {
        repo.addUser(user)
    }

    /** Update - 전체 User 객체 저장 */
    fun saveUser() = viewModelScope.launch {
        try {
            _user.value?.let {
                repo.saveUser(it)
                Log.d("UserVM", "User saved: $it")
            } ?: Log.w("UserVM", "_user.value is null, skipping save")
        } catch (e: Exception) {
            Log.e("UserVM", "Failed to save user", e)
        }
    }

    /** Update - 일부 필드 수정 */
    fun updateUser(updates: Map<String, Any?>) = viewModelScope.launch {
        _user.value?.id?.let { uid ->
            repo.updateUser(uid, updates)
            Log.d("UserVM", "$uid, $updates")
        }
    }

    /** Delete - Soft Delete */
    fun deleteUser() = viewModelScope.launch {
        _user.value?.id?.let { uid ->
            repo.deleteUser(uid)
        }
    }
}