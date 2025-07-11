package com.example.myspbstu

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myspbstu.databinding.FragmentChooseGroupBinding
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.presentation.adapter.GroupsAdapter
import com.example.myspbstu.presentation.viewmodel.ChooseGroupViewModel
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
                prefs.edit { putInt(PREFS_GROUP_ID_KEY, group.id).apply() }
                prefs.edit { putString(PREFS_GROUP_NAME_KEY, group.name).apply() }

                navController.navigate(
                    ChooseGroupFragmentDirections
                        .actionChooseGroupFragmentToScheduleFragment(group.id, group.name)
                )
            }
        }

        observeLiveData()

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