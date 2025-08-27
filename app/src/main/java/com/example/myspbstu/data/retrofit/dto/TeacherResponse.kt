package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class TeacherResponse(
    @SerializedName("teachers")
    val teachers : List<TeacherNwModel>
)
