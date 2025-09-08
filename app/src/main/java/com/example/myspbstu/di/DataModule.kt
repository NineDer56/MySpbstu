package com.example.myspbstu.di

import com.example.myspbstu.data.retrofit.ScheduleApiFactory
import com.example.myspbstu.data.retrofit.ScheduleApiService
import com.example.myspbstu.data.retrofit.repository.ScheduleRepositoryImpl
import com.example.myspbstu.domain.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindScheduleRepository(impl: ScheduleRepositoryImpl) : ScheduleRepository

    companion object{

        @ApplicationScope
        @Provides
        fun provideScheduleApiService() : ScheduleApiService{
            return ScheduleApiFactory.scheduleApiService
        }
    }
}