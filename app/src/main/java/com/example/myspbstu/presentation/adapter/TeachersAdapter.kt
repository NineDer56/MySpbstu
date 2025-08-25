package com.example.myspbstu.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.R
import com.example.myspbstu.domain.model.Teacher

class TeachersAdapter : ListAdapter<Teacher, TeachersAdapter.TeacherViewHolder>(TeachersDiffCallback()) {

    var onTeacherClickListener : OnTeacherClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.teacher_item,
            parent,
            false
        )
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = getItem(position)

        with(holder){
            tvTeacherName.text = teacher.name
            tvTeacherChair.text = teacher.chair

            itemView.setOnClickListener {
                onTeacherClickListener?.onTeacherClick(teacher)
            }
        }
    }

    class TeacherViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvTeacherName = itemView.findViewById<TextView>(R.id.tv_teacher_name)
        val tvTeacherChair = itemView.findViewById<TextView>(R.id.tv_teacher_chair)
    }

    interface OnTeacherClickListener{
        fun onTeacherClick(teacher : Teacher)
    }

}