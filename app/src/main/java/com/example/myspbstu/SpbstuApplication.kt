package com.example.myspbstu

import android.app.Application
import com.example.myspbstu.di.DaggerApplicationComponent

class SpbstuApplication : Application() {
    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }
}