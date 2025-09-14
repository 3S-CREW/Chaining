package com.example.chaining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.ApplicationRepository
import com.example.chaining.domain.model.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val repo: ApplicationRepository
) : ViewModel() {
    private val _application = MutableStateFlow<Application?>(null)
    val application: StateFlow<Application?> = _application

    private val _applications = MutableStateFlow<List<Application>>(emptyList())
    val applications: StateFlow<List<Application>> = _applications

    private val _statusUpdates = MutableStateFlow<List<Application>>(emptyList())
    val statusUpdates: StateFlow<List<Application>> = _statusUpdates

    // ✅ 1. 신청 완료 이벤트를 UI에 알리기 위한 StateFlow 추가
    private val _isSubmitSuccess = MutableStateFlow(false)
    val isSubmitSuccess: StateFlow<Boolean> = _isSubmitSuccess

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        // 기존 목록 조회
//        fetchAllApplications()
        // 상태 변경 구독
//        observeStatusUpdates()
    }

    private fun observeStatusUpdates() = viewModelScope.launch {
        repo.observeMyApplicationStatus().collect { apps ->
            _statusUpdates.value = apps
        }
    }

    fun submitApplication(application: Application) = viewModelScope.launch {
        val result = repo.submitApplication(application)
        result.onSuccess { returnedApplicationId ->
            val updatedList = _applications.value.toMutableList()
            val newApplicationForUi = application.copy(
                applicationId = returnedApplicationId,
                createdAt = System.currentTimeMillis()
            )
            updatedList.add(newApplicationForUi)
            _applications.value = updatedList
            _toastEvent.emit("application_success")
            // ✅ 2. 신청 성공 시, isSubmitSuccess 상태를 true로 변경
            _isSubmitSuccess.value = true
        }.onFailure { exception ->
            _toastEvent.emit("application_failed")
        }
//        val updatedList = _applications.value.toMutableList()
//        val newApplication = application.copy(
//            applicationId = applicationId,
//            createdAt = System.currentTimeMillis()
//        )
//        updatedList.add(newApplication)
//        _applications.value = updatedList
    }
    fun resetSubmitStatus() {
        _isSubmitSuccess.value = false
    }

    fun fetchApplication(applicationId: String) = viewModelScope.launch {
        _application.value = repo.getApplication(applicationId)
    }

    /** Update - 전체 User 객체 저장 */
    fun updateStatus(application: Application, value: String) = viewModelScope.launch {
        repo.updateStatus(application, value)
    }

    fun fetchAllApplications() = viewModelScope.launch {
        _applications.value = repo.getMyApplications()
    }

    fun deleteApply() = viewModelScope.launch {
        _application.value?.applicationId?.let { aid ->
            repo.deleteApplication(aid)
        }
    }
}