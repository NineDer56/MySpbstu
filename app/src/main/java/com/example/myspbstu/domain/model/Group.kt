package com.example.myspbstu.domain.model

data class Group(
    private val id : Int,
    private val name : String,
    private val level : Int,
    private val faculty: Faculty
)