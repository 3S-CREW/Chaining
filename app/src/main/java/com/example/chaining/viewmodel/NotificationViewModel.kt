@file:Suppress("ktlint:standard:property-naming")

package com.example.chaining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.NotificationRepository
import com.example.chaining.domain.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        private val repository: NotificationRepository,
    ) : ViewModel() {
        private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
        val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        private val _event = MutableSharedFlow<NotificationEvent>()
        val event: SharedFlow<NotificationEvent> = _event

        init {
            fetchNotifications()
        }

        fun onApplicationClick(
            applicationId: String,
            screenType: String,
            introduction: String?,
            closeAt: Long?,
        ) {
            viewModelScope.launch {
                _event.emit(
                    NotificationEvent.NavigateToApplication(
                        applicationId = applicationId,
                        type = screenType,
                        introduction = introduction ?: "",
                        closeAt = closeAt ?: 0L,
                    ),
                )
            }
        }

        /** 알림 실시간 구독 시작 */
        private fun fetchNotifications() {
            viewModelScope.launch {
                _isLoading.value = true
                repository.observeNotifications()
                    .catch { e ->
                        _errorMessage.value = e.message
                        _isLoading.value = false
                    }
                    .collect { list ->
                        _notifications.value = list
                        _isLoading.value = false
                    }
            }
        }

        /** 알림 읽음 상태 업데이트 (옵션) */
        fun markNotificationAsRead(notificationId: String) {
            viewModelScope.launch {
                val updatedList =
                    _notifications.value.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                _notifications.value = updatedList
            }
        }

        /** 에러 메시지 초기화 */
        fun clearError() {
            _errorMessage.value = null
        }
    }

sealed class NotificationEvent {
    data class NavigateToApplication(
        val applicationId: String,
        val type: String,
        val introduction: String,
        val closeAt: Long,
    ) : NotificationEvent()

    data class ShowToast(val message: String) : NotificationEvent()
    object Refresh : NotificationEvent()
}
