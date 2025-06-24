package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName


data class ScheduleNwModel (
    @SerializedName("week")
    val week: WeekNwModel,
    @SerializedName("days")
    val days : List<DayNwModel>
)