package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetTeachersByNameUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(name: String) : List<Teacher>{
        return repository.getTeachersByName(name)
    }
}