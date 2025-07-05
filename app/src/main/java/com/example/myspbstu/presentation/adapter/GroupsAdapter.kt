package com.example.myspbstu.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.R
import com.example.myspbstu.domain.model.Group

class GroupsAdapter : ListAdapter<Group, GroupsAdapter.GroupViewHolder>(GroupsDiffCallback()) {

    var onGroupClickListener : OnGroupClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.group_item,
            parent,
            false
        )
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)

        with(holder){
            tvGroupName.text = group.name
            tvFacultyName.text = group.faculty.name

            itemView.setOnClickListener {
                onGroupClickListener?.onGroupClick(group)
            }
        }
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvGroupName = itemView.findViewById<TextView>(R.id.tv_group_name)
        val tvFacultyName = itemView.findViewById<TextView>(R.id.tv_faculty_name)
    }

    interface OnGroupClickListener{
        fun onGroupClick (group : Group)
    }
}