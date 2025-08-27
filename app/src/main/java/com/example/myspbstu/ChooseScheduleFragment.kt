package com.example.myspbstu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myspbstu.databinding.FragmentChooseScheduleBinding
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Teacher
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

    private val teachersAdapter: TeachersAdapter by lazy {
        TeachersAdapter()
    }

    private val navController: NavController by lazy {
        findNavController()
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    private var areGroupsSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val groupId = prefs.getInt(PREFS_GROUP_ID_KEY, 0)
        val groupName = prefs.getString(PREFS_GROUP_NAME_KEY, "") ?: ""

        val teacherId = prefs.getInt(PREFS_TEACHER_ID_KEY, 0)
        val teacherName = prefs.getString(PREFS_TEACHER_NAME_KEY, "") ?: ""

        if (groupId != -1 && groupName != "") {
            navController.navigate(
                ChooseScheduleFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(groupId, groupName)
            )
        } else if (teacherId != -1 && teacherName != "") {
            navController.navigate(
                ChooseScheduleFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(
                        groupId = 0,
                        groupName = "",
                        teacherId = teacherId,
                        teacherName = teacherName
                    )
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

        groupsAdapter.onGroupClickListener = object : GroupsAdapter.OnGroupClickListener {
            override fun onGroupClick(group: Group) {
                prefs.edit {
                    putInt(PREFS_GROUP_ID_KEY, group.id)
                    putString(PREFS_GROUP_NAME_KEY, group.name)

                    putInt(PREFS_TEACHER_ID_KEY, 0)
                    putString(PREFS_TEACHER_NAME_KEY, "")
                }

                navController.navigate(
                    ChooseScheduleFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(group.id, group.name)
                )
            }
        }

        teachersAdapter.onTeacherClickListener = object : TeachersAdapter.OnTeacherClickListener {
            override fun onTeacherClick(teacher: Teacher) {
                prefs.edit {
                    putInt(PREFS_TEACHER_ID_KEY, teacher.id)
                    putString(PREFS_TEACHER_NAME_KEY, teacher.name)

                    putInt(PREFS_GROUP_ID_KEY, 0)
                    putString(PREFS_GROUP_NAME_KEY, "")
                }

                navController.navigate(
                    ChooseScheduleFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(
                            groupId = 0,
                            groupName = "",
                            teacherId = teacher.id,
                            teacherName = teacher.name
                        )
                )
            }
        }

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
                if (spinnerSelectionOptions.selectedItemPosition == 1) {
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
        viewModel.teachers.observe(viewLifecycleOwner) {
            teachersAdapter.submitList(it)
        }
    }

    companion object {
        const val PREFS_FILE = "app_prefs"

        const val PREFS_GROUP_ID_KEY = "sharedPreferencesGroupIdKey"
        const val PREFS_GROUP_NAME_KEY = "sharedPreferencesGroupNameKey"

        const val PREFS_TEACHER_ID_KEY = "sharedPreferencesTeacherIdKey"
        const val PREFS_TEACHER_NAME_KEY = "sharedPreferencesTeacherNameKey"

    }

}