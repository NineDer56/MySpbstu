package com.example.myspbstu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myspbstu.databinding.FragmentScheduleBinding
import com.example.myspbstu.presentation.adapter.LessonsAdapter
import com.example.myspbstu.presentation.adapter.ScheduleAdapter
import com.example.myspbstu.presentation.viewmodel.ScheduleFragmentViewModel
import kotlinx.coroutines.launch


class ScheduleFragment : Fragment() {

    val args: ScheduleFragmentArgs by navArgs()

    private var _binding: FragmentScheduleBinding? = null
    val binding: FragmentScheduleBinding
        get() = _binding ?: throw RuntimeException("FragmentScheduleBinding is null")

    private val lessonsAdapter by lazy {
        LessonsAdapter()
    }

    private val scheduleAdapter by lazy {
        ScheduleAdapter()
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ScheduleFragmentViewModel::class.java]
    }

    private val groupId: Int by lazy {
        args.groupId
    }

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

        binding.rvWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvWeek.adapter = scheduleAdapter
        PagerSnapHelper().attachToRecyclerView(binding.rvWeek)

        binding.rvWeek.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position == RecyclerView.NO_POSITION) return

                val offset = viewModel.getOffsetByPosition(position)
                viewModel.setCurrentOffset(offset)

                viewModel.updateYearAndMonthByPosition(position)

                viewLifecycleOwner.lifecycleScope.launch {
                    if (!viewModel.isWeekLoaded(offset - 1)) {
                        viewModel.loadWeekByOffset(groupId, offset - 1)
                    }
                    if (!viewModel.isWeekLoaded(offset + 1)) {
                        viewModel.loadWeekByOffset(groupId, offset + 1)
                    }
                }
            }

        })


        observeLiveData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadInitial(groupId)
            binding.rvWeek.scrollToPosition(1)
        }

    }

    private fun observeLiveData() {
        viewModel.lessons.observe(viewLifecycleOwner) {
            lessonsAdapter.submitList(it)
        }
        viewModel.schedules.observe(viewLifecycleOwner) {
            scheduleAdapter.submitList(it)
            Log.d("MyDebug", "observeLiveData: list updated")
        }
        viewModel.currentYear.observe(viewLifecycleOwner){
            binding.tvYear.text = it
        }
        viewModel.currentMonth.observe(viewLifecycleOwner){
            binding.tvMonth.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}