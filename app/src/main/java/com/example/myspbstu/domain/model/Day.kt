package com.example.myspbstu.domain.model

data class Day(
    val weekday : Int,
    val date : String,
    val lessons : List<Lesson>
)