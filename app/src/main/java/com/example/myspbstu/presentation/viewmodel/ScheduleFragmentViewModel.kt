package com.example.myspbstu.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import com.example.myspbstu.domain.usecase.GetScheduleByTeacherIdUseCase
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import com.example.myspbstu.presentation.worker.ExamWorker
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
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleFragmentViewModel @Inject constructor(
    private val application: Application,
    private val getScheduleByGroupIdUseCase: GetScheduleByGroupIdUseCase,
    private val getScheduleByTeacherIdUseCase: GetScheduleByTeacherIdUseCase
) : ViewModel() {

    private var _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons = _lessons.asStateFlow()

    private var _days = MutableStateFlow<List<Day>>(emptyList())
    val days = _days.asStateFlow()

    private var _currentYear = MutableStateFlow<String>("")
    val currentYear = _currentYear.asStateFlow()

    private var _currentMonth = MutableStateFlow<String>("")
    val currentMonth = _currentMonth.asStateFlow()

    private var _currentDay = MutableStateFlow<String>("")
    val currentDay = _currentDay.asStateFlow()

    private var _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()

    private var _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private val importantLessonTypes = setOf("Экз", "Зч", "ЗаО")

    private var jobLoadSchedule: Job? = null


    fun onWeekScrolled(position: Int, groupId: Int, teacherId: Int) {
        loadMonthAndYear(position)
        if (teacherId == -1) {
            loadScheduleByPositionAndGroupId(position, groupId)
        } else {
            loadScheduleByPositionAndTeacherId(position, teacherId)
        }

    }

    fun onDaySelected(position: Int, dayOfWeek: Int) {
        val curLessons = days.value.find { it.weekday == dayOfWeek + 1 }?.lessons
        _lessons.value = curLessons.orEmpty()

        val date = WeeksAdapter.getDateOfMondayByPosition(position)
        val newDate = date.plusDays(dayOfWeek.toLong())
        val formatter = DateTimeFormatter.ofPattern("dd LLLL")
        _currentDay.value = newDate.format(formatter).toString()
    }

    fun clearLessons() {
        _lessons.value = emptyList()
    }

    private fun loadScheduleByPositionAndGroupId(position: Int, groupId: Int) {
        val date = WeeksAdapter.getDateOfMondayByPosition(position).toString()
        jobLoadSchedule?.cancel()
        jobLoadSchedule = viewModelScope.launch {
            getScheduleByGroupIdUseCase(groupId, date)
                .onStart { _loading.value = true }
                .catch { cause: Throwable ->
                    _days.value = emptyList()
                    _uiEvent.tryEmit(UiEvent.Error(cause.message ?: "Unknown error"))
                }
                .onCompletion { _loading.value = false }
                .collect {
                    val days = it.days
                    _days.value = days
                    checkForNotifications(days)
                }
        }
    }

    private fun loadScheduleByPositionAndTeacherId(position: Int, teacherId: Int) {
        val date = WeeksAdapter.getDateOfMondayByPosition(position).toString()
        jobLoadSchedule?.cancel()
        jobLoadSchedule = viewModelScope.launch {
            getScheduleByTeacherIdUseCase(teacherId, date)
                .onStart { _loading.value = true }
                .catch { cause: Throwable ->
                    _days.value = emptyList()
                    _uiEvent.tryEmit(UiEvent.Error(cause.message ?: "Unknown error"))
                }
                .onCompletion { _loading.value = false }
                .collect {
                    val days = it.days
                    _days.value = days
                    checkForNotifications(days)
                }
        }
    }

    private fun checkForNotifications(days: List<Day>) {
        for (day in days) {
            val date = day.date
            for (lesson in day.lessons) {
                Log.d("MyDebug", "${lesson.lessonType.abbr}, list: $importantLessonTypes")
                if (lesson.lessonType.abbr in importantLessonTypes) {
                    makeNotification(lesson, date)
                    Log.d("MyDebug", "notif: lesson $lesson, date $date")
                }
            }
        }
    }

    private fun makeNotification(lesson: Lesson, date: String) {
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

        val examDate = LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        val now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        Log.d("MyDebug", "$examDate, now: $now")
        if (examDate < now)
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
        Log.d(
            "MyDebug",
            "Создано уведомление $typeOfExam, $subject, $time, через $delay, сейчас ${now}"
        )
    }

    private fun loadMonthAndYear(position: Int) {
        _currentYear.value = WeeksAdapter.getYearByPosition(position)
        _currentMonth.value = WeeksAdapter.getMonthByPosition(position)
    }

    sealed interface UiEvent {
        data class Error(val message: String) : UiEvent
    }
}