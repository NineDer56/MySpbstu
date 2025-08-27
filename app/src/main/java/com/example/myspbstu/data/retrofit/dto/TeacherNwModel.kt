package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class TeacherNwModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val name : String,
    @SerializedName("chair")
    val chair : String
)