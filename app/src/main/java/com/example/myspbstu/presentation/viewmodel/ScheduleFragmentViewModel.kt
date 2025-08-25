package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.impl.utils.tryDelegateRemoteListenableWorker
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.domain.usecase.GetScheduleByTeacherIdUseCase
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import com.example.myspbstu.presentation.worker.ExamWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScheduleFragmentViewModel(
    application: Application
) : AndroidViewModel(application) {

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

    private var _currentDay = MutableLiveData<String>()
    val currentDay : LiveData<String>
        get() = _currentDay

    private val importantLessonTypes = listOf("Экз", "Зч", "ЗаО")


    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)
    private val getScheduleByTeacherIdUseCase = GetScheduleByTeacherIdUseCase(repository)

    fun loadScheduleByPositionAndGroupId(position: Int, groupId : Int){
        val date = WeeksAdapter.getDateOfMondayByPosition(position).toString()

        viewModelScope.launch{
            Log.d("MyDebug", "position $position, id $groupId, date $date")

            try {
                val schedule = getScheduleByGroupIdUseCase(groupId, date)
                _days.postValue(schedule.days)
                checkForNotifications(schedule.days)
            } catch (e : Exception){
                _days.value = emptyList()
                Toast.makeText(application.applicationContext, "Ошибка: ${e.message ?: "неизвестно"}", Toast.LENGTH_SHORT).show()
                Log.d("ScheduleFragment", e.message.toString())
            }

        }
    }

    fun loadScheduleByPositionAndTeacherId(position: Int, teacherId : Int){
        val date = WeeksAdapter.getDateOfMondayByPosition(position).toString()

        viewModelScope.launch{
            Log.d("MyDebug", "position $position, id $teacherId, date $date")

            try {
                val schedule = getScheduleByTeacherIdUseCase(teacherId, date)
                _days.postValue(schedule.days)
                checkForNotifications(schedule.days)
            } catch (e : Exception){
                _days.value = emptyList()
                Toast.makeText(application.applicationContext, "Ошибка: ${e.message ?: "неизвестно"}", Toast.LENGTH_SHORT).show()
                Log.d("ScheduleFragment", e.message.toString())
            }

        }
    }

    private fun checkForNotifications(days : List<Day>){
        for(day in days){
            val date = day.date
            for(lesson in day.lessons){
                Log.d("MyDebug", "${lesson.lessonType.abbr}, list: $importantLessonTypes")
                if(lesson.lessonType.abbr in importantLessonTypes){
                    makeNotification(lesson, date)
                    Log.d("MyDebug", "notif: lesson $lesson, date $date")
                }
            }
        }
    }


    private fun makeNotification(lesson: Lesson, date : String){
        val typeOfExam = lesson.lessonType.name
        val subject = lesson.subject
        val time = lesson.timeStart

        val splitDate = date.split("-")
        val year = splitDate[0].toInt()
        val month = splitDate[1].toInt()
        val day = splitDate[2].toInt()

        val splitTime = time.split(":")
        val hour = splitTime[0].toInt()
        val minute = splitTime[1].toInt()

        val examDate = LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC).toEpochMilli()
        val now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        Log.d("MyDebug", "$examDate, now: $now")
        if(examDate < now)
            return

        val delay = examDate - now - (24 * 60 * 60 * 1000) - 3 * 60 * 60 * 1000
        //val delay = 10_000
        if (delay <= 0)
            return

        val name = "$typeOfExam/$subject/$time"

        val workManager = WorkManager.getInstance(application.applicationContext)
        workManager.enqueueUniqueWork(
            name,
            ExistingWorkPolicy.KEEP,
            ExamWorker.makeRequest(typeOfExam, subject, time, delay.toLong())
        )
        Log.d("MyDebug", "Создано уведомление $typeOfExam, $subject, $time, через $delay, сейчас ${now}")
    }

    fun clearLessons(){
        _lessons.value = emptyList()
    }

    fun onWeekScrolled(position: Int, groupId: Int, teacherId: Int){
        loadMonthAndYear(position)
        if(teacherId == 0){
            loadScheduleByPositionAndGroupId(position, groupId)
        } else {
            loadScheduleByPositionAndTeacherId(position, teacherId)
        }

    }

    fun onDaySelected(position : Int, dayOfWeek: Int){
        val curLessons = days.value?.find { it.weekday == dayOfWeek + 1 }?.lessons
        _lessons.value = curLessons.orEmpty()

        val date = WeeksAdapter.getDateOfMondayByPosition(position)
        val newDate = date.plusDays(dayOfWeek.toLong())
        val formatter = DateTimeFormatter.ofPattern("dd LLLL")
        _currentDay.value = newDate.format(formatter).toString()
    }

    fun loadMonthAndYear(position: Int){
        _currentYear.value = WeeksAdapter.getYearByPosition(position)
        _currentMonth.value = WeeksAdapter.getMonthByPosition(position)
    }
}