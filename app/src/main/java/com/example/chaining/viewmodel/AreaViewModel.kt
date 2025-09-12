package com.example.chaining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.local.entity.AreaEntity
import com.example.chaining.data.repository.AreaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AreaViewModel @Inject constructor(
    private val repository: AreaRepository
) : ViewModel() {

    val areaCodes: StateFlow<List<AreaEntity>> = repository.allAreas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.refreshAreasIfNeeded()
        }
    }
}
