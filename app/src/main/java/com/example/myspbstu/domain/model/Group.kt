package com.example.myspbstu.domain.model

data class Group(
    val id : Int,
    val name : String,
    val level : Int,
    val faculty: Faculty
)