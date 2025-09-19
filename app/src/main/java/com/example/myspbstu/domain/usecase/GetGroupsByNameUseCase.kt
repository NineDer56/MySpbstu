package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsByNameUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(name : String) : Flow<List<Group>> {
        return repository.getGroupsByName(name)
    }
}