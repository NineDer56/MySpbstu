package com.example.myspbstu.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myspbstu.domain.model.Group


class GroupsDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}