package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import com.example.myspbstu.presentation.worker.ExamWorker
import com.example.myspbstu.presentation.worker.ExamWorker.Companion.CHANNEL_ID
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

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

    private val importantLessonTypes = listOf("Экз", "Зч", "ЗаО")


    private val repository = ScheduleRepositoryImpl()
    private val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(repository)

    fun loadScheduleByPositionAndGroupId(position: Int, groupId : Int){
        val date = WeeksAdapter.getDateByPosition(position)

        viewModelScope.launch{
            Log.d("MyDebug", "position $position, id $groupId, date $date")
            val schedule = getScheduleByGroupIdUseCase(groupId, date)
            _days.postValue(schedule.days)

            checkForNotifications(schedule.days)
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