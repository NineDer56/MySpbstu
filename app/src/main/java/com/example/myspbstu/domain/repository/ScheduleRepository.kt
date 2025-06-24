package com.example.myspbstu.domain.repository

import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule

interface ScheduleRepository {

    fun getGroupsByName(name : String) : List<Group>

    fun getScheduleByGroupId(groupId : Int, date : String) : Schedule
}