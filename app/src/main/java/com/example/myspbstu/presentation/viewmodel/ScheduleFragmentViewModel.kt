package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleFragmentViewModel(
    application: Application
) : AndroidViewModel(application) {

    var initialScrollDone = false

    private var _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> get() = _lessons

    private var _days = MutableLiveData<List<Day>>()
    val days : LiveData<List<Day>>
        get() = _days

    private var _currentYear = MutableLiveData<String>()
    val currentYear : LiveData<String>
        get() = _currentYear

    private var _currentMonth = MutableLiveData<String>()
    val currentMonth : LiveData<String>
        get() = _currentMonth


    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)

    fun loadScheduleByPositionAndGroupId(position: Int, groupId : Int){
        val date = WeeksAdapter.getDateByPosition(position)

        viewModelScope.launch{
            Log.d("MyDebug", "position $position, id $groupId, date $date")
            val schedule = getScheduleByGroupIdUseCase(groupId, date)
            _days.postValue(schedule.days)
        }
    }

    fun clearLessons(){
        _lessons.value = emptyList()
    }

    fun onWeekScrolled(position: Int, groupId: Int){
        loadMonthAndYear(position)
        loadScheduleByPositionAndGroupId(position, groupId)
    }

    fun onDaySelected(dayOfWeek: Int){
        val curLessons = days.value?.find { it.weekday == dayOfWeek + 1 }?.lessons
        _lessons.value = curLessons.orEmpty()
    }

    fun loadMonthAndYear(position: Int){
        _currentYear.value = WeeksAdapter.getYearByPosition(position)
        _currentMonth.value = WeeksAdapter.getMonthByPosition(position)
    }
}