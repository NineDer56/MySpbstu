package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetScheduleByTeacherIdUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(teacherId: Int, date : String) : Schedule{
        return repository.getScheduleByTeacherId(teacherId, date)
    }
}