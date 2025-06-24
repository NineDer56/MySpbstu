package com.example.myspbstu.domain.model

data class Lesson(
    private val subject : String,
    private val timeStart : String,
    private val timeEnd : String,
    private val lessonType: LessonType,
    private val groups : List<Group>,
    private val teachers : List<Teacher>,
    private val auditories: List<Auditory>
)
