package com.example.myspbstu.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myspbstu.R
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.databinding.ActivityMainBinding
import com.example.myspbstu.domain.usecase.GetGroupsByNameUseCase
import com.example.myspbstu.domain.usecase.GetScheduleByGroupIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val rep = ScheduleRepositoryImpl()
        val getScheduleByGroupIdUseCase = GetScheduleByGroupIdUseCase(rep)
        val getGroupsByNameUseCase = GetGroupsByNameUseCase(rep)

        binding.btnTest.setOnClickListener {
            coroutineScope.launch {
                val groups = getGroupsByNameUseCase("5130903")
                Log.d("MainActivity", groups.toString())
            }

        }
    }


}