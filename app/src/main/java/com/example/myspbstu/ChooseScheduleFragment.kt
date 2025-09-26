package com.example.myspbstu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myspbstu.databinding.FragmentChooseScheduleBinding
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.presentation.adapter.GroupsAdapter
import com.example.myspbstu.presentation.adapter.TeachersAdapter
import com.example.myspbstu.presentation.viewmodel.ChooseScheduleViewModel
import com.example.myspbstu.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChooseScheduleFragment : Fragment() {

    private val component by lazy {
        (requireActivity().application as SpbstuApplication)
            .component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ChooseScheduleViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ChooseScheduleViewModel::class.java]
    }

    private var _binding: FragmentChooseScheduleBinding? = null
    private val binding: FragmentChooseScheduleBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseGroupBinding is null")

    private val groupsAdapter: GroupsAdapter by lazy {
        GroupsAdapter()
    }

    private val teachersAdapter: TeachersAdapter by lazy {
        TeachersAdapter()
    }

    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
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

        checkSharedPreferences()
        setSpinnerItemSelectedListener()
        setOnGroupClickListener()
        setOnTeacherClickListeners()
        collectFlow()
        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkSharedPreferences() {
        val groupId = prefs.getInt(
            PREFS_GROUP_ID_KEY,
            PREFS_GROUP_ID_NOT_FOUND
        )
        val groupName = prefs.getString(
            PREFS_GROUP_NAME_KEY,
            PREFS_GROUP_NAME_NOT_FOUND
        ) ?: PREFS_GROUP_NAME_NOT_FOUND

        val teacherId = prefs.getInt(
            PREFS_TEACHER_ID_KEY,
            PREFS_TEACHER_ID_NOT_FOUND
        )
        val teacherName = prefs.getString(
            PREFS_TEACHER_NAME_KEY,
            PREFS_TEACHER_NAME_NOT_FOUND
        ) ?: PREFS_TEACHER_NAME_NOT_FOUND

        if (
            groupId != PREFS_GROUP_ID_NOT_FOUND &&
            groupName != PREFS_GROUP_NAME_NOT_FOUND
        ) {
            navController.navigate(
                ChooseScheduleFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(
                        groupId,
                        groupName,
                        teacherId,
                        teacherName
                    )
            )
        } else if (
            teacherId != PREFS_TEACHER_ID_NOT_FOUND &&
            teacherName != PREFS_TEACHER_NAME_NOT_FOUND
        ) {
            navController.navigate(
                ChooseScheduleFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(
                        groupId,
                        groupName,
                        teacherId,
                        teacherName
                    )
            )
        }
    }

    private fun setOnGroupClickListener() {
        groupsAdapter.onGroupClickListener = object : GroupsAdapter.OnGroupClickListener {
            override fun onGroupClick(group: Group) {
                prefs.edit {
                    putInt(PREFS_GROUP_ID_KEY, group.id)
                    putString(PREFS_GROUP_NAME_KEY, group.name)

                    putInt(PREFS_TEACHER_ID_KEY, PREFS_TEACHER_ID_NOT_FOUND)
                    putString(PREFS_TEACHER_NAME_KEY, PREFS_TEACHER_NAME_NOT_FOUND)
                }

                navController.navigate(
                    ChooseScheduleFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(
                            groupId = group.id,
                            groupName = group.name
                        )
                )
            }
        }
    }

    private fun setOnTeacherClickListeners() {
        teachersAdapter.onTeacherClickListener = object : TeachersAdapter.OnTeacherClickListener {
            override fun onTeacherClick(teacher: Teacher) {
                prefs.edit {
                    putInt(PREFS_TEACHER_ID_KEY, teacher.id)
                    putString(PREFS_TEACHER_NAME_KEY, teacher.name)

                    putInt(PREFS_GROUP_ID_KEY, PREFS_GROUP_ID_NOT_FOUND)
                    putString(PREFS_GROUP_NAME_KEY, PREFS_GROUP_NAME_NOT_FOUND)
                }

                navController.navigate(
                    ChooseScheduleFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(
                            teacherId = teacher.id,
                            teacherName = teacher.name
                        )
                )
            }
        }
    }

    private fun setSpinnerItemSelectedListener() {
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
                        GROUP_SPINNER_POSITION -> {
                            binding.editTextGroupNum.hint = getString(R.string.enter_group_number)
                            binding.rvGroups.adapter = groupsAdapter
                            groupsAdapter.submitList(emptyList())
                        }

                        TEACHER_SPINNER_POSITION -> {
                            binding.editTextGroupNum.hint = getString(R.string.enter_teachers_name)
                            binding.rvGroups.adapter = teachersAdapter
                            teachersAdapter.submitList(emptyList())
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.spinnerSelectionOptions.setSelection(GROUP_SPINNER_POSITION, false)
    }

    private fun setOnClickListeners() {
        binding.btnFindGroups.setOnClickListener {
            val query = binding.editTextGroupNum.text.toString().trim()
            if (query.isEmpty()) return@setOnClickListener
            val areGroupsSelected =
                (binding.spinnerSelectionOptions.selectedItemPosition ==
                        GROUP_SPINNER_POSITION)
            if (areGroupsSelected) {
                viewModel.getGroupsByName(query)
            } else {
                viewModel.getTeachersByName(query)
            }
        }
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.groups.collect {
                        groupsAdapter.submitList(it)
                    }
                }
                launch {
                    viewModel.teachers.collect {
                        teachersAdapter.submitList(it)
                    }
                }
                launch {
                    viewModel.loading.collect {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.uiEvent.collect {
                        when (it) {
                            is ChooseScheduleViewModel.UiEvent.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val PREFS_FILE = "spbstu_app_prefs"

        const val PREFS_GROUP_ID_KEY = "sharedPreferencesGroupIdKey"
        const val PREFS_GROUP_NAME_KEY = "sharedPreferencesGroupNameKey"

        const val PREFS_TEACHER_ID_KEY = "sharedPreferencesTeacherIdKey"
        const val PREFS_TEACHER_NAME_KEY = "sharedPreferencesTeacherNameKey"

        const val PREFS_TEACHER_ID_NOT_FOUND = -1
        const val PREFS_TEACHER_NAME_NOT_FOUND = ""
        const val PREFS_GROUP_ID_NOT_FOUND = -1
        const val PREFS_GROUP_NAME_NOT_FOUND = ""

        const val GROUP_SPINNER_POSITION = 0
        const val TEACHER_SPINNER_POSITION = 1
    }

}