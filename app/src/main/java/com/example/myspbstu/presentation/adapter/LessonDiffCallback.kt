package com.example.myspbstu.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myspbstu.domain.model.Lesson

class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem.subject == newItem.subject &&
                oldItem.timeStart == newItem.timeStart &&
                oldItem.timeEnd == newItem.timeEnd
    }

    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem == newItem
    }
}