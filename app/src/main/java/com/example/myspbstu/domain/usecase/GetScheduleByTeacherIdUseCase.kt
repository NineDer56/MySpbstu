package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.repository.ScheduleRepository

class GetScheduleByTeacherIdUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(teacherId: Int, date : String) : Schedule{
        return repository.getScheduleByTeacherId(teacherId, date)
    }
}