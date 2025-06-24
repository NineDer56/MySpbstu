package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class DayNwModel(
    @SerializedName("weekday")
    val weekday : Int,
    @SerializedName("date")
    val date : String,
    @SerializedName("lessons")
    val lessons : List<LessonNwModel>
)