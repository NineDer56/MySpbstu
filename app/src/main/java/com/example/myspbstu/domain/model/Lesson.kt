package com.example.myspbstu.domain.model

data class Lesson(
    val subject : String,
    val timeStart : String,
    val timeEnd : String,
    val lessonType: LessonType,
    val groups : List<Group>,
    val teachers : List<Teacher>,
    val auditories: List<Auditory>
)
