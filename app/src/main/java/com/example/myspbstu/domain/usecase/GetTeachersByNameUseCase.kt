package com.example.myspbstu.domain.usecase

import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository

class GetTeachersByNameUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(name: String) : List<Teacher>{
        return repository.getTeachersByName(name)
    }
}