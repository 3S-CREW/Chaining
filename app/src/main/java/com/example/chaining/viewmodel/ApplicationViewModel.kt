package com.example.chaining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.ApplicationRepository
import com.example.chaining.domain.model.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        // 기존 목록 조회
        fetchAllApplications()
        // 상태 변경 구독
        observeStatusUpdates()
    }

    private fun observeStatusUpdates() = viewModelScope.launch {
        repo.observeMyApplicationStatus().collect { apps ->
            _statusUpdates.value = apps
        }
    }

    fun submitApplication(application: Application) = viewModelScope.launch {
        val applicationId = repo.submitApplication(application)

        val updatedList = _applications.value.toMutableList()
        val newApplication = application.copy(
            applicationId = applicationId,
            createdAt = System.currentTimeMillis()
        )
        updatedList.add(newApplication)
        _applications.value = updatedList
    }

    fun fetchApplication(applicationId: String) = viewModelScope.launch {
        _application.value = repo.getApplication(applicationId)
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