package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetGroupsByNameUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(name : String) : List<Group>{
        return repository.getGroupsByName(name)
    }
}