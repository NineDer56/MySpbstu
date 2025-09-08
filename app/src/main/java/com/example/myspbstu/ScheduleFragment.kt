package com.example.myspbstu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
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
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.example.myspbstu.presentation.viewmodel.ViewModelFactory
import javax.inject.Inject


class ScheduleFragment : Fragment() {

    private val component by lazy{
        (requireActivity().application as SpbstuApplication)
            .component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ScheduleFragmentViewModel::class.java]
    }

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

    private val groupId: Int by lazy {
        args.groupId
    }

    private val groupName : String by lazy {
        args.groupName
    }

    private val teacherId: Int by lazy {
        args.teacherId
    }

    private val teacherName : String by lazy {
        args.teacherName
    }

    private val prefs by lazy {
        requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val snapHelper by lazy { PagerSnapHelper() }

    private var currentDays: List<Day>? = null

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

        binding.tvNoLessons.visibility = View.VISIBLE

        binding.rvSchedule.adapter = lessonsAdapter
        binding.rvWeek.adapter = weeksAdapter

        binding.rvWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        Log.d("ScheduleFragment", "$groupId, $groupName, $teacherId")

        snapHelper.attachToRecyclerView(binding.rvWeek)
        binding.rvWeek.scrollToPosition(WeeksAdapter.START_POSITION)

        observeLiveData()
        viewModel.loadMonthAndYear(WeeksAdapter.START_POSITION)

        if(teacherId == 0){
            viewModel.loadScheduleByPositionAndGroupId(WeeksAdapter.START_POSITION, groupId)
        } else {
            viewModel.loadScheduleByPositionAndTeacherId(WeeksAdapter.START_POSITION, teacherId)
        }


        val layoutManager = binding.rvWeek.layoutManager as LinearLayoutManager

        binding.rvWeek.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

                    //очистка при скролле
                    viewModel.clearLessons()
                    weeksAdapter.selectedDayIndex = Pair(-1, -1)

                    viewModel.onWeekScrolled(firstVisiblePosition, groupId, teacherId)
                    binding.tvToolBarDate.text = ""
                }

            }
        })

        weeksAdapter.onWeekdayClickListener = object : WeeksAdapter.OnWeekdayClickListener {
            override fun onWeekdayClick(dayOfWeek: Int) {
                val position = layoutManager.findFirstVisibleItemPosition()

                viewModel.onDaySelected(position, dayOfWeek)
                weeksAdapter.selectedDayIndex = Pair(position, dayOfWeek)
            }
        }

        if(teacherId == 0){
            binding.tvToolbarGroupName.text = "$groupName ↓"
        } else {
            binding.tvToolbarGroupName.text = "$teacherName ↓"
        }

        binding.tvToolbarGroupName.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.schedule_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.findNewGroup -> {
                        prefs.edit { putInt(ChooseScheduleFragment.PREFS_GROUP_ID_KEY, -1) }
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun observeLiveData() {
        viewModel.lessons.observe(viewLifecycleOwner) {
            lessonsAdapter.submitList(it)
            showNoLessonsText(it.isEmpty())
        }
        viewModel.currentYear.observe(viewLifecycleOwner) {
            binding.tvYear.text = it
        }
        viewModel.currentMonth.observe(viewLifecycleOwner) {
            binding.tvMonth.text = it
        }
        viewModel.days.observe(viewLifecycleOwner) {
            currentDays = it
        }
        viewModel.currentDay.observe(viewLifecycleOwner){
            binding.tvToolBarDate.text = it
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
                Toast.makeText(requireActivity(), "Разрешение не предоставлено", Toast.LENGTH_SHORT)
                    .show()
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
                NOTIFICATION_PREFS_KEY,
                false
            )

            if (permissionGranted) {
                // Do your task on permission granted
            } else if(dontShowAgain){
                // Skip
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showExplainDialog()
                } else{
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
            .setTitle("Разрешения на уведомления")
            .setMessage("Чтобы получать напоминания об экзаменах, пожалуйста, разрешите показ уведомлений.")
            .setPositiveButton("Ок") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNeutralButton("Не показывать снова"){dialog,_ ->
                prefs.edit { putBoolean(NOTIFICATION_PREFS_KEY, true) }
                dialog.dismiss()
            }
            .setNegativeButton("Нет, спасибо") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



    private fun showSettingsDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Разрешение отклонено")
            .setMessage("Вы запретили показ уведомлений. Чтобы включить их, перейдите в настройки приложения.")
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent =
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = ("package:${requireActivity().packageName}").toUri()
                    }
                startActivity(intent)
            }
            .setNeutralButton("Не показывать снова"){dialog,_ ->
                prefs.edit { putBoolean(NOTIFICATION_PREFS_KEY, true) }
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
        private const val NOTIFICATION_PREFS_KEY = "dontShowNotificationPermissionDialog"
    }
}