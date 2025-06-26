package com.example.myspbstu.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myspbstu.domain.model.Schedule

class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.week.dateStart == newItem.week.dateStart
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }
}