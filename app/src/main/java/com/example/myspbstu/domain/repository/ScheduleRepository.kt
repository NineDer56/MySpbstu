package com.example.myspbstu.domain.repository

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher

interface ScheduleRepository {

    suspend fun getGroupsByName(name: String): List<Group>

    suspend fun getScheduleByGroupId(groupId: Int, date: String): Schedule

    suspend fun getTeachersByName(name: String): List<Teacher>

    suspend fun getScheduleByTeacherId(teacherId: Int, date: String): Schedule
}