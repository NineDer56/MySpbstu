package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.presentation.adapter.ScheduleWithOffset
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleFragmentViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> get() = _lessons

    private var _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> get() = _schedules

    private var _currentMonth = MutableLiveData<String>()
    val currentMonth : LiveData<String>
        get() = _currentMonth

    private var _currentYear = MutableLiveData<String>()
    val currentYear : LiveData<String>
        get() = _currentYear

    private val scheduleList = mutableListOf<ScheduleWithOffset>()
    private val scheduleSet = mutableSetOf<Int>() // offset values
    private var currentWeekOffset = 0

    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)

    suspend fun loadInitial(groupId: Int) {
        val offsets = listOf(-1, 0, 1)
        offsets.forEach { offset ->
            if (scheduleSet.add(offset)) {
                val schedule = loadScheduleByOffset(groupId, offset)
                scheduleList.add(ScheduleWithOffset(offset, schedule))
            }
        }
        updateYearAndMonthByPosition(0)
        scheduleList.sortBy { it.offset }
        _schedules.value = scheduleList.map { it.schedule }
    }

    private suspend fun loadScheduleByOffset(groupId: Int, offset: Int): Schedule {
        val monday = LocalDate.now()
            .with(DayOfWeek.MONDAY)
            .plusWeeks(offset.toLong())

        return getScheduleByGroupIdUseCase(groupId, monday.toString())
    }

    fun isWeekLoaded(offset: Int): Boolean {
        return scheduleSet.contains(offset)
    }

    suspend fun loadWeekByOffset(groupId: Int, offset: Int) {
        if (scheduleSet.add(offset)) {
            val schedule = loadScheduleByOffset(groupId, offset)
            if (offset < scheduleList.firstOrNull()?.offset ?: 0) {
                scheduleList.add(0, ScheduleWithOffset(offset, schedule))
            } else {
                scheduleList.add(ScheduleWithOffset(offset, schedule))
            }
            _schedules.postValue(scheduleList.sortedBy { it.offset }.map { it.schedule })
        }
    }

    fun getOffsetByPosition(position: Int): Int {
        return scheduleList.getOrNull(position)?.offset ?: 0
    }

    fun updateYearAndMonthByPosition(position: Int){
        val date = scheduleList[position].schedule.week.dateStart
        _currentYear.value = getFormattedYear(date)
        _currentMonth.value = getFormattedMonth(date)

    }

    fun setCurrentOffset(offset: Int) {
        currentWeekOffset = offset
    }

    private fun getFormattedMonth(date: String) : String{
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val outputFormatter = DateTimeFormatter.ofPattern("LLLL", Locale("ru"))

        val parsedDate = LocalDate.parse(date, inputFormatter)
        return parsedDate.format(outputFormatter).replaceFirstChar { it.titlecaseChar() }
    }

    private fun getFormattedYear(date: String) : String{
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy", Locale("ru"))

        val parsedDate = LocalDate.parse(date, inputFormatter)
        return parsedDate.format(outputFormatter)
    }

    data class ScheduleWithOffset(
        val offset: Int,
        val schedule: Schedule
    )
}