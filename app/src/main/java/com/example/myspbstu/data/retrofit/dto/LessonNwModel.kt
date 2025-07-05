package com.example.myspbstu.data.retrofit.dto

import com.google.gson.annotations.SerializedName


data class LessonNwModel(
    @SerializedName("subject")
    val subject : String,
    @SerializedName("time_start")
    val timeStart : String,
    @SerializedName("time_end")
    val timeEnd : String,
    @SerializedName("typeObj")
    val lessonType: LessonTypeNwModel,
    @SerializedName("groups")
    val groups : List<GroupNwModel>,
    @SerializedName("teachers")
    val teachers : List<TeacherNwModel>?,
    @SerializedName("auditories")
    val auditories: List<AuditoryNwModel>
)