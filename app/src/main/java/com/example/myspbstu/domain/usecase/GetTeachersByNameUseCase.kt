package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTeachersByNameUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
     operator fun invoke(name: String) : Flow<List<Teacher>> {
        return repository.getTeachersByName(name)
    }
}