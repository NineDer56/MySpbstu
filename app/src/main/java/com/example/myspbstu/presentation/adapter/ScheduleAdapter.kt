package com.example.myspbstu.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.R
import com.example.myspbstu.domain.model.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleAdapter : ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {

    var onDayClickListener : OnDayClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.week_item,
            parent,
            false
        )
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        val daysOfWeek = getFormattedWeekDays(schedule.week.dateStart, schedule.week.dateEnd)
        Log.d("MyDebug", "onBindViewHolder: position $position")

        val dayViews = listOf(
            holder.tvDay1,
            holder.tvDay2,
            holder.tvDay3,
            holder.tvDay4,
            holder.tvDay5,
            holder.tvDay6,
            holder.tvDay7
        )

        for (i in dayViews.indices) {
            dayViews[i].text = daysOfWeek[i]
            dayViews[i].setOnClickListener {
                onDayClickListener?.onDayClicked(i + 1, holder.adapterPosition)
            }
        }
    }



    private fun getFormattedWeekDays(start: String, end: String): List<String> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd", Locale("ru"))

        val startDate = LocalDate.parse(start, inputFormatter)
        val endDate = LocalDate.parse(end, inputFormatter)

        val days = mutableListOf<String>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            days.add(current.format(outputFormatter))
            current = current.plusDays(1)
        }
        return days
    }

    class ScheduleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvDay1 = itemView.findViewById<TextView>(R.id.tv_day1)
        val tvDay2 = itemView.findViewById<TextView>(R.id.tv_day2)
        val tvDay3 = itemView.findViewById<TextView>(R.id.tv_day3)
        val tvDay4 = itemView.findViewById<TextView>(R.id.tv_day4)
        val tvDay5 = itemView.findViewById<TextView>(R.id.tv_day5)
        val tvDay6 = itemView.findViewById<TextView>(R.id.tv_day6)
        val tvDay7 = itemView.findViewById<TextView>(R.id.tv_day7)
    }

    interface OnDayClickListener {
        fun onDayClicked(day: Int, position: Int)
    }


}