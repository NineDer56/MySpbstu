package com.example.myspbstu.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.usecase.GetGroupsByNameUseCase
import com.example.myspbstu.domain.usecase.GetTeachersByNameUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChooseScheduleViewModel @Inject constructor(
    private val getGroupsByNameUseCase: GetGroupsByNameUseCase,
    private val getTeachersByNameUseCase: GetTeachersByNameUseCase
) : ViewModel() {

    private var _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow()

    private var _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers = _teachers.asStateFlow()

    private var _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()

    private var _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private var groupsJob : Job? = null
    private var teachersJob : Job? = null

    fun getGroupsByName(name: String) {
        groupsJob?.cancel()
        groupsJob = viewModelScope.launch {
            getGroupsByNameUseCase.invoke(name)
                .onStart { _loading.value = true }
                .catch { cause: Throwable ->
                    _uiEvent.tryEmit(UiEvent.Error(cause.message ?: "Unknown error"))
                }
                .onCompletion { _loading.value = false }
                .collect {_groups.value = it}
        }
    }

    fun getTeachersByName(name: String) {
        teachersJob?.cancel()
        teachersJob = viewModelScope.launch {
            getTeachersByNameUseCase.invoke(name)
                .onStart { _loading.value = true }
                .catch { cause: Throwable ->
                    _uiEvent.tryEmit(UiEvent.Error(cause.message ?: "UnknownError"))
                }.onCompletion { _loading.value = false }
                .collect {
                    _teachers.value = it
                }
        }
    }

    sealed interface UiEvent {
        data class Error(val message: String) : UiEvent
    }
}