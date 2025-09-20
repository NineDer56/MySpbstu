package com.example.myspbstu.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.R
import com.example.myspbstu.domain.model.Lesson

class LessonsAdapter : ListAdapter<Lesson, LessonsAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.schedule_item,
            parent,
            false
        )
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = getItem(position)

        with(holder){
            tvLessonOrder.text = (position + 1).toString()
            tvLessonTeacher.text = lesson.teachers[0].name
            tvLessonName.text = lesson.subject
            tvLessonTime.text = "${lesson.timeStart} - ${lesson.timeEnd}"
            tvLessonType.text = lesson.lessonType.name
            tvBuilding.text = "${lesson.auditories[0].building.name} ауд ${lesson.auditories[0].name}"
        }

    }


    class LessonViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvLessonOrder : TextView = itemView.findViewById(R.id.tv_lesson_order)
        val tvLessonTime : TextView = itemView.findViewById(R.id.tv_lesson_time)
        val tvBuilding : TextView = itemView.findViewById(R.id.tv_building)
        val tvLessonName : TextView = itemView.findViewById(R.id.tv_lesson_name)
        val tvLessonType : TextView = itemView.findViewById(R.id.tv_lesson_type)
        val tvLessonTeacher : TextView = itemView.findViewById(R.id.tv_lesson_teacher)
    }
}