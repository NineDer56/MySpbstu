package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName

data class GroupResponse(
    @SerializedName("groups")
    val groups : List<GroupNwModel>
)
