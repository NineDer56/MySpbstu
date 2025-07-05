package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.usecase.GetGroupsByNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseGroupViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var _groups = MutableLiveData<List<Group>>()
    val groups : LiveData<List<Group>>
        get() = _groups

    private val repository = ScheduleRepositoryImpl()
    private val getGroupsByNameUseCase = GetGroupsByNameUseCase(repository)

    fun getGroupsByName(name : String){
        viewModelScope.launch {
            val groups = withContext(Dispatchers.IO){
                getGroupsByNameUseCase(name)
            }
            _groups.value = groups
        }
    }
}