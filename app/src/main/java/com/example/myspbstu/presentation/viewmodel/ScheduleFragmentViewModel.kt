package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleFragmentViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var _lessons = MutableLiveData<List<Lesson>>()
    val lessons : LiveData<List<Lesson>>
        get() = _lessons

    private var _schedule = MutableLiveData<Schedule>()
    val schedule : LiveData<Schedule>
        get() = _schedule

    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)

    fun getScheduleByGroupId(groupId : Int){
        viewModelScope.launch{
            val schedule = withContext(Dispatchers.IO) {
                getScheduleByGroupIdUseCase(groupId, "2025-5-2")
            }
            Log.d("ScheduleFragmentViewModel", schedule.toString())
            _schedule.value = schedule
            _lessons.value = schedule.days[0].lessons
        }
    }
}