package com.example.myspbstu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.myspbstu.databinding.FragmentScheduleBinding
import com.example.myspbstu.presentation.adapter.LessonsAdapter
import com.example.myspbstu.presentation.adapter.ScheduleAdapter
import com.example.myspbstu.presentation.viewmodel.ScheduleFragmentViewModel


class ScheduleFragment : Fragment() {

    val args : ScheduleFragmentArgs by navArgs()

    private var _binding : FragmentScheduleBinding? = null
    val binding : FragmentScheduleBinding
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

        observeLiveData()

        viewModel.getScheduleByGroupId(args.groupId)
    }

    private fun observeLiveData(){
        viewModel.lessons.observe(viewLifecycleOwner){
            lessonsAdapter.submitList(it)
        }
        viewModel.schedule.observe(viewLifecycleOwner){
            scheduleAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}