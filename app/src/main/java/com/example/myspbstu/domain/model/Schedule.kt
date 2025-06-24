package com.example.myspbstu.domain.model

data class Schedule (
    private val week: Week,
    private val days : List<Day>
)