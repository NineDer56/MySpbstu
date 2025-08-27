package com.example.myspbstu.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myspbstu.domain.model.Teacher

class TeachersDiffCallback : DiffUtil.ItemCallback<Teacher>() {
    override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        return oldItem == newItem
    }
}