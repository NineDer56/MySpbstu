package com.example.myspbstu.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Formatter
import java.util.Locale

class WeeksAdapter : RecyclerView.Adapter<WeeksAdapter.WeekViewHolder>() {

    var onWeekdayClickListener : OnWeekdayClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeksAdapter.WeekViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.week_item,
            parent,
            false
        )
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeksAdapter.WeekViewHolder, position: Int) {
        val weekOffset = position - START_POSITION
        val monday = LocalDate.now()
            .with(DayOfWeek.MONDAY)
            .plusWeeks(weekOffset.toLong())
        val weekDates = (0..6).map { monday.plusDays(it.toLong()) }

        for((index, dayView) in holder.dayViews.withIndex()){
            dayView.text = weekDates[index].dayOfMonth.toString()

            dayView.setOnClickListener {
                onWeekdayClickListener?.onWeekdayClick(index)
            }
        }


    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }


    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayViews: List<TextView> = listOf(
            itemView.findViewById(R.id.tv_day1),
            itemView.findViewById(R.id.tv_day2),
            itemView.findViewById(R.id.tv_day3),
            itemView.findViewById(R.id.tv_day4),
            itemView.findViewById(R.id.tv_day5),
            itemView.findViewById(R.id.tv_day6),
            itemView.findViewById(R.id.tv_day7)
        )
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2

        // TODO оптимизировать
        fun getYearByPosition(position: Int) : String{
            val weekOffset = position - START_POSITION
            val monday = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .plusWeeks(weekOffset.toLong())
            return monday.year.toString()
        }

        fun getMonthByPosition(position: Int) : String{
            val weekOffset = position - START_POSITION
            val monday = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .plusWeeks(weekOffset.toLong())

            val formatter = DateTimeFormatter.ofPattern("LLLL", Locale("ru"))
            val month = monday.format(formatter)

            return month.toString().replaceFirstChar { it.titlecaseChar() }
        }

        fun getDateByPosition(position: Int) : String{
            val weekOffset = position - START_POSITION
            val monday = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .plusWeeks(weekOffset.toLong())
            return monday.toString()
        }
    }

    interface OnWeekdayClickListener{
        fun onWeekdayClick(dayOfWeek : Int)
    }
}