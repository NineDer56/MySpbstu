package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.repository.ScheduleRepository

class GetGroupsByNameUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(name : String) : List<Group>{
        return repository.getGroupsByName(name)
    }
}