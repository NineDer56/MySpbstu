package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScheduleByGroupIdUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(groupId: Int, date : String) : Flow<Schedule> {
        return repository.getScheduleByGroupId(groupId, date)
    }
}