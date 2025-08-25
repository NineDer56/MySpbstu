package com.example.myspbstu

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myspbstu.databinding.FragmentChooseGroupBinding
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.presentation.adapter.GroupsAdapter
import com.example.myspbstu.presentation.viewmodel.ChooseGroupViewModel
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import java.util.prefs.Preferences

class ChooseGroupFragment : Fragment() {

    private var _binding: FragmentChooseGroupBinding? = null
    private val binding: FragmentChooseGroupBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseGroupBinding is null")

    private val viewModel: ChooseGroupViewModel by lazy {
        ViewModelProvider(this)[ChooseGroupViewModel::class.java]
    }

    private val groupsAdapter: GroupsAdapter by lazy {
        GroupsAdapter()
    }

    private val navController: NavController by lazy {
        findNavController()
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences(PREFS_GROUP_ID_KEY, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val groupId = prefs.getInt(PREFS_GROUP_ID_KEY, -1)
        val groupName = prefs.getString(PREFS_GROUP_NAME_KEY, "") ?: ""
        if (groupId != -1 && groupName != "") {
            navController.navigate(
                ChooseGroupFragmentDirections
                    .actionChooseGroupFragmentToScheduleFragment(groupId, groupName)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupsAdapter.onGroupClickListener = object : GroupsAdapter.OnGroupClickListener {
            override fun onGroupClick(group: Group) {
                prefs.edit {
                    putInt(PREFS_GROUP_ID_KEY, group.id)
                    putString(PREFS_GROUP_NAME_KEY, group.name)
                }

                navController.navigate(
                    ChooseGroupFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(group.id, group.name)
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
                        0 -> binding.editTextGroupNum.hint = getString(R.string.enter_group_number)
                        1 -> binding.editTextGroupNum.hint = getString(R.string.enter_teachers_name)
                        else -> binding.editTextGroupNum.hint =
                            getString(R.string.enter_group_number)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("Spinner", "onNothingSelected")
                }
            }

        with(binding) {
            rvGroups.adapter = groupsAdapter
            btnFindGroups.setOnClickListener {
                val groupName = editTextGroupNum.text.toString()
                viewModel.getGroupsByName(groupName)
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
    }

    companion object {
        const val PREFS_GROUP_ID_KEY = "sharedPreferencesGroupIdKey"
        const val PREFS_GROUP_NAME_KEY = "sharedPreferencesGroupNameKey"

    }

}