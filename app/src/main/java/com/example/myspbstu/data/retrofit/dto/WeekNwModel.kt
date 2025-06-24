package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class WeekNwModel(
    @SerializedName("date_start")
    val dateStart : String,
    @SerializedName("date_end")
    val dateEnd : String,
    @SerializedName("is_odd")
    val idOdd : Boolean
)