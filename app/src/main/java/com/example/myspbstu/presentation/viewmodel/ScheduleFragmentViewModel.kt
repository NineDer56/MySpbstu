package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.presentation.adapter.WeeksAdapter

class ScheduleFragmentViewModel(
    application: Application
) : AndroidViewModel(application) {

    var initialScrollDone = false

    private var _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> get() = _lessons

    private var _currentYear = MutableLiveData<String>()
    val currentYear : LiveData<String>
        get() = _currentYear

    private var _currentMonth = MutableLiveData<String>()
    val currentMonth : LiveData<String>
        get() = _currentMonth


    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)

    fun loadMonthAndYear(position: Int){
        _currentYear.value = WeeksAdapter.getYearByPosition(position)
        _currentMonth.value = WeeksAdapter.getMonthByPosition(position)
    }
}