package com.example.myspbstu.di

import android.app.Application
import com.example.myspbstu.ChooseScheduleFragment
import com.example.myspbstu.ScheduleFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [ViewModelModule::class, DataModule::class])
interface ApplicationComponent {

    fun inject(fragment: ChooseScheduleFragment)

    fun inject(fragment: ScheduleFragment)

    @Component.Factory
    interface Factory{

        fun create(
            @BindsInstance application: Application
        ) : ApplicationComponent
    }
}