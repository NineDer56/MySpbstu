package com.example.myspbstu.di

import androidx.lifecycle.ViewModel
import com.example.myspbstu.presentation.viewmodel.ChooseScheduleViewModel
import com.example.myspbstu.presentation.viewmodel.ScheduleFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChooseScheduleViewModel::class)
    fun bindChooseScheduleViewModel(viewModel: ChooseScheduleViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleFragmentViewModel::class)
    fun bindScheduleFragmentViewModel(viewModel: ScheduleFragmentViewModel) : ViewModel
}