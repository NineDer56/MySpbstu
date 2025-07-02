package com.example.myspbstu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.myspbstu.databinding.FragmentScheduleBinding
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.presentation.adapter.LessonsAdapter
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import com.example.myspbstu.presentation.viewmodel.ScheduleFragmentViewModel


class ScheduleFragment : Fragment() {

    val args: ScheduleFragmentArgs by navArgs()

    private var _binding: FragmentScheduleBinding? = null
    val binding: FragmentScheduleBinding
        get() = _binding ?: throw RuntimeException("FragmentScheduleBinding is null")

    private val lessonsAdapter by lazy {
        LessonsAdapter()
    }

    private val weeksAdapter by lazy {
        WeeksAdapter()
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ScheduleFragmentViewModel::class.java]
    }

    private val groupId: Int by lazy {
        args.groupId
    }

    private val snapHelper by lazy { PagerSnapHelper() }

    private var currentDays : List<Day>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSchedule.adapter = lessonsAdapter
        binding.rvWeek.adapter = weeksAdapter

        binding.rvWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        snapHelper.attachToRecyclerView(binding.rvWeek)
        binding.rvWeek.scrollToPosition(WeeksAdapter.START_POSITION)

        observeLiveData()
        viewModel.loadMonthAndYear(WeeksAdapter.START_POSITION)
        viewModel.loadScheduleByPositionAndGroupId(WeeksAdapter.START_POSITION, groupId)

        val layoutManager = binding.rvWeek.layoutManager as LinearLayoutManager

        binding.rvWeek.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

                    //очистка при скролле
                    viewModel.clearLessons()
                    viewModel.onWeekScrolled(firstVisiblePosition, groupId)
                }

            }
        })

        weeksAdapter.onWeekdayClickListener = object : WeeksAdapter.OnWeekdayClickListener{
            override fun onWeekdayClick(dayOfWeek: Int) {
                viewModel.onDaySelected(dayOfWeek)
            }
        }

    }

    private fun observeLiveData() {
        viewModel.lessons.observe(viewLifecycleOwner) {
            lessonsAdapter.submitList(it)
        }
        viewModel.currentYear.observe(viewLifecycleOwner){
            binding.tvYear.text = it
        }
        viewModel.currentMonth.observe(viewLifecycleOwner){
            binding.tvMonth.text = it
        }
        viewModel.days.observe(viewLifecycleOwner){
            currentDays = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}