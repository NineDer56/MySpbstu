package com.example.myspbstu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.myspbstu.databinding.FragmentScheduleBinding
import com.example.myspbstu.presentation.adapter.LessonsAdapter
import com.example.myspbstu.presentation.adapter.WeeksAdapter
import com.example.myspbstu.presentation.viewmodel.ScheduleFragmentViewModel
import com.example.myspbstu.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScheduleFragment : Fragment() {

    private val component by lazy {
        (requireActivity().application as SpbstuApplication)
            .component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ScheduleFragmentViewModel::class.java]
    }

    private val args: ScheduleFragmentArgs by navArgs()

    private var _binding: FragmentScheduleBinding? = null
    val binding: FragmentScheduleBinding
        get() = _binding ?: throw RuntimeException("FragmentScheduleBinding is null")

    private val lessonsAdapter by lazy {
        LessonsAdapter()
    }

    private val weeksAdapter by lazy {
        WeeksAdapter()
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences(
            ChooseScheduleFragment.PREFS_FILE,
            Context.MODE_PRIVATE
        )
    }

    private val snapHelper by lazy { PagerSnapHelper() }

    private val layoutManager by lazy {
        binding.rvWeek.layoutManager as LinearLayoutManager
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setHasOptionsMenu(true)
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
        setToolBar()
        setUpRecyclerViews()
        collectFlow()
        loadDateAndSchedule()
        setOnWeekScrollListener()
        setOnWeekdayClickListener()
        setUpToolBar()
    }

    private fun loadDateAndSchedule() {
        binding.tvNoLessons.visibility = View.VISIBLE
        viewModel.onWeekScrolled(WeeksAdapter.START_POSITION, args.groupId, args.teacherId)
    }

    private fun setUpRecyclerViews() {
        binding.rvSchedule.adapter = lessonsAdapter
        binding.rvWeek.adapter = weeksAdapter

        binding.rvWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        snapHelper.attachToRecyclerView(binding.rvWeek)
        binding.rvWeek.scrollToPosition(WeeksAdapter.START_POSITION)
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.lessons.collect {
                        lessonsAdapter.submitList(it)
                        showNoLessonsText(it.isEmpty())
                    }
                }
                launch {
                    viewModel.currentYear.collect {
                        binding.tvYear.text = it
                    }
                }
                launch {
                    viewModel.currentMonth.collect {
                        binding.tvMonth.text = it
                    }
                }
                launch {
                    viewModel.currentDay.collect {
                        binding.tvToolBarDate.text = it
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
                            is ScheduleFragmentViewModel.UiEvent.Error -> {
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

    private fun setUpToolBar() {
        if (args.teacherId == ChooseScheduleFragment.PREFS_TEACHER_ID_NOT_FOUND) {
            binding.tvToolbarGroupName.text = "${args.groupName} ↓"
        } else {
            binding.tvToolbarGroupName.text = "${args.teacherName} ↓"
        }

        binding.tvToolbarGroupName.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.schedule_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.findNewSchedule -> {
                        prefs.edit {
                            putInt(
                                ChooseScheduleFragment.PREFS_GROUP_ID_KEY,
                                ChooseScheduleFragment.PREFS_GROUP_ID_NOT_FOUND
                            )
                            putString(
                                ChooseScheduleFragment.PREFS_GROUP_NAME_KEY,
                                ChooseScheduleFragment.PREFS_GROUP_NAME_NOT_FOUND
                            )
                            putInt(
                                ChooseScheduleFragment.PREFS_TEACHER_ID_KEY,
                                ChooseScheduleFragment.PREFS_TEACHER_ID_NOT_FOUND
                            )
                            putString(
                                ChooseScheduleFragment.PREFS_TEACHER_NAME_KEY,
                                ChooseScheduleFragment.PREFS_TEACHER_NAME_NOT_FOUND
                            )
                        }
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun setOnWeekScrollListener() {
        binding.rvWeek.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

                    //очистка при скролле
                    viewModel.clearLessons()
                    weeksAdapter.selectedDayIndex = Pair(-1, -1)

                    viewModel.onWeekScrolled(firstVisiblePosition, args.groupId, args.teacherId)
                    binding.tvToolBarDate.text = ""
                }
            }
        })
    }

    private fun setOnWeekdayClickListener() {
        weeksAdapter.onWeekdayClickListener = object : WeeksAdapter.OnWeekdayClickListener {
            override fun onWeekdayClick(dayOfWeek: Int) {
                val position = layoutManager.findFirstVisibleItemPosition()

                viewModel.onDaySelected(position, dayOfWeek)
                weeksAdapter.selectedDayIndex = Pair(position, dayOfWeek)
            }
        }
    }

    private fun showNoLessonsText(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNoLessons.visibility = View.VISIBLE
        } else {
            binding.tvNoLessons.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Granted
        } else {
            // Permission Denied / Cancel
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showSettingsDialog()
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.permission_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED)

            val dontShowAgain = prefs.getBoolean(
                PREFS_DONT_SHOW_NOTIF_PERM_DIALOG,
                false
            )

            if (permissionGranted) {
                // Do your task on permission granted
            } else if (dontShowAgain) {
                // Skip
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showExplainDialog()
                } else {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Below Android 13 You don't need to ask for notification permission.
        }
    }

    private fun showExplainDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.notification_premission))
            .setMessage(getString(R.string.please_grant_the_permission))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNeutralButton(getString(R.string.dont_show_again)) { dialog, _ ->
                prefs.edit { putBoolean(PREFS_DONT_SHOW_NOTIF_PERM_DIALOG, true) }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no_thanks)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun showSettingsDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.permission_denied))
            .setMessage(getString(R.string.you_denied_permission_go_to_settings))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                val intent =
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = ("package:${requireActivity().packageName}").toUri()
                    }
                startActivity(intent)
            }
            .setNeutralButton(getString(R.string.dont_show_again)) { dialog, _ ->
                prefs.edit { putBoolean(PREFS_DONT_SHOW_NOTIF_PERM_DIALOG, true) }
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setToolBar() {
        val toolbar = binding.toolbarSchedule
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).setTitle("")
    }

    companion object {
        private const val PREFS_DONT_SHOW_NOTIF_PERM_DIALOG = "dontShowNotificationPermissionDialog"
    }
}