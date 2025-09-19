package com.example.myspbstu.domain.repository

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    fun getGroupsByName(name: String): Flow<List<Group>>

    fun getScheduleByGroupId(groupId: Int, date: String): Flow<Schedule>

    fun getTeachersByName(name: String): Flow<List<Teacher>>

    fun getScheduleByTeacherId(teacherId: Int, date: String): Flow<Schedule>
}