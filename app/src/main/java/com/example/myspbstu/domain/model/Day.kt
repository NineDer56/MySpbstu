package com.example.myspbstu.domain.model

data class Day(
    private val weekday : Int,
    private val date : String,
    private val lessons : List<Lesson>
)