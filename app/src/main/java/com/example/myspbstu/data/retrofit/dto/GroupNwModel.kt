package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class GroupNwModel(
    @SerializedName("id")
    val id : Int,
    @SerializedName("name")
    val name : String,
    @SerializedName("level")
    val level : Int,
    @SerializedName("faculty")
    val faculty: FacultyNwModel
)