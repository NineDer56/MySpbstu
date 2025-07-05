package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.repository.ScheduleRepository

class GetScheduleByGroupIdUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(groupId: Int, date : String) : Schedule{
        return repository.getScheduleByGroupId(groupId, date)
    }
}