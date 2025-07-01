package com.example.myspbstu.presentation.adapter

import com.example.myspbstu.domain.model.Schedule

data class ScheduleWithOffset(
    val offset : Int,
    val schedule: Schedule
)
