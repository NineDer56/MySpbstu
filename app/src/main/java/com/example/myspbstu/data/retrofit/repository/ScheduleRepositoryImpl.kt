package com.example.myspbstu.data.retrofit.repository

import com.example.myspbstu.data.retrofit.ScheduleApiFactory
import com.example.myspbstu.data.retrofit.ScheduleNwMapper
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl : ScheduleRepository {

    private val nwMapper = ScheduleNwMapper()

    override suspend fun getGroupsByName(name: String): List<Group> {
        return ScheduleApiFactory.scheduleApiService.getGroupsByName(name).groups
            .map { nwMapper.mapGroupNwModelToEntity(it) }
    }

    override suspend fun getScheduleByGroupId(groupId: Int, date: String): Schedule {
        return nwMapper.mapScheduleNwModelToEntity(
            ScheduleApiFactory.scheduleApiService.getScheduleByGroupId(groupId, date)
        )
    }

    override suspend fun getTeachersByName(name: String): List<Teacher> {
        return ScheduleApiFactory.scheduleApiService.getTeachersByName(name).teachers
            .map { nwMapper.mapTeacherNwModelToEntity(it) }
    }
}