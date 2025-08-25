package com.example.myspbstu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myspbstu.databinding.FragmentChooseScheduleBinding
import com.example.myspbstu.presentation.adapter.GroupsAdapter
import com.example.myspbstu.presentation.adapter.TeachersAdapter
import com.example.myspbstu.presentation.viewmodel.ChooseScheduleViewModel

class ChooseScheduleFragment : Fragment() {

    private var _binding: FragmentChooseScheduleBinding? = null
    private val binding: FragmentChooseScheduleBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseGroupBinding is null")

    private val viewModel: ChooseScheduleViewModel by lazy {
        ViewModelProvider(this)[ChooseScheduleViewModel::class.java]
    }

    private val groupsAdapter: GroupsAdapter by lazy {
        GroupsAdapter()
    }

    private val teachersAdapter : TeachersAdapter by lazy {
        TeachersAdapter()
    }

    private val navController: NavController by lazy {
        findNavController()
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences(PREFS_GROUP_ID_KEY, Context.MODE_PRIVATE)
    }

    private var areGroupsSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val groupId = prefs.getInt(PREFS_GROUP_ID_KEY, -1)
        val groupName = prefs.getString(PREFS_GROUP_NAME_KEY, "") ?: ""
        if (groupId != -1 && groupName != "") {
            navController.navigate(
                ChooseScheduleFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(groupId, groupName)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        groupsAdapter.onGroupClickListener = object : GroupsAdapter.OnGroupClickListener {
//            override fun onGroupClick(group: Group) {
//                prefs.edit {
//                    putInt(PREFS_GROUP_ID_KEY, group.id)
//                    putString(PREFS_GROUP_NAME_KEY, group.name)
//                }
//
//                navController.navigate(
//                    ChooseGroupFragmentDirections
//                        .actionChooseGroupFragmentToScheduleFragment(group.id, group.name)
//                )
//            }
//        }

        observeLiveData()


        binding.spinnerSelectionOptions.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d("Spinner", "onItemSelected: position $position, id $id")

                    when (position) {
                        0 -> {
                            binding.editTextGroupNum.hint = getString(R.string.enter_group_number)
                            areGroupsSelected = true
                        }
                        1 -> {
                            binding.editTextGroupNum.hint = getString(R.string.enter_teachers_name)
                            areGroupsSelected = false
                        }
                        else -> {
                            binding.editTextGroupNum.hint = getString(R.string.enter_group_number)
                            areGroupsSelected = true
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("Spinner", "onNothingSelected")
                }
            }

        with(binding) {
            btnFindGroups.setOnClickListener {
                if(spinnerSelectionOptions.selectedItemPosition == 1){
                    rvGroups.adapter = teachersAdapter
                    val teacherName = editTextGroupNum.text.toString()
                    viewModel.getTeachersByName(teacherName)
                } else {
                    rvGroups.adapter = groupsAdapter
                    val groupName = editTextGroupNum.text.toString()
                    viewModel.getGroupsByName(groupName)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLiveData() {
        viewModel.groups.observe(viewLifecycleOwner) {
            groupsAdapter.submitList(it)
        }
        viewModel.teachers.observe(viewLifecycleOwner){
            teachersAdapter.submitList(it)
        }
    }

    companion object {
        const val PREFS_GROUP_ID_KEY = "sharedPreferencesGroupIdKey"
        const val PREFS_GROUP_NAME_KEY = "sharedPreferencesGroupNameKey"

    }

}