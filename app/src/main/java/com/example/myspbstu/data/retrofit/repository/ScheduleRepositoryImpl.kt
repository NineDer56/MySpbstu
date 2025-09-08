package com.example.myspbstu.data.retrofit.repository

import com.example.myspbstu.data.retrofit.ScheduleApiService
import com.example.myspbstu.data.retrofit.ScheduleNwMapper
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val nwMapper : ScheduleNwMapper,
    private val apiService : ScheduleApiService
) : ScheduleRepository {

    override suspend fun getGroupsByName(name: String): List<Group> {
        return apiService.getGroupsByName(name).groups
            .map { nwMapper.mapGroupNwModelToEntity(it) }
    }

    override suspend fun getScheduleByGroupId(groupId: Int, date: String): Schedule {
        return nwMapper.mapScheduleNwModelToEntity(
            apiService.getScheduleByGroupId(groupId, date)
        )
    }

    override suspend fun getTeachersByName(name: String): List<Teacher> {
        return apiService.getTeachersByName(name).teachers
            .map { nwMapper.mapTeacherNwModelToEntity(it) }
    }

    override suspend fun getScheduleByTeacherId(teacherId: Int, date: String): Schedule {
        return nwMapper.mapScheduleNwModelToEntity(
            apiService.getScheduleByTeacherId(teacherId, date)
        )
    }
}