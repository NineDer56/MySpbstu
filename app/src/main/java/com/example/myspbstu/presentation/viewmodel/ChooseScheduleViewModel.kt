package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.usecase.GetGroupsByNameUseCase
import com.example.myspbstu.domain.usecase.GetTeachersByNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseScheduleViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var _groups = MutableLiveData<List<Group>>()
    val groups : LiveData<List<Group>>
        get() = _groups

    private var _teachers = MutableLiveData<List<Teacher>>()
    val teachers : LiveData<List<Teacher>>
        get() = _teachers

    private val repository = ScheduleRepositoryImpl()
    private val getGroupsByNameUseCase = GetGroupsByNameUseCase(repository)
    private val getTeachersByNameUseCase = GetTeachersByNameUseCase(repository)

    fun getGroupsByName(name : String){
        viewModelScope.launch {
            try {
                val groups = withContext(Dispatchers.IO){
                    getGroupsByNameUseCase(name)
                }
                _groups.value = groups
            } catch (e : Exception){
                Toast.makeText(application.applicationContext, "Ошибка: ${e.message ?: "неизвестно"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTeachersByName(name : String){
        viewModelScope.launch {
            try {
                val teachers = withContext(Dispatchers.IO){
                    getTeachersByNameUseCase(name)
                }
                _teachers.value = teachers
            } catch (e : Exception){
                Toast.makeText(application.applicationContext, "Ошибка: ${e.message ?: "неизвестно"}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}