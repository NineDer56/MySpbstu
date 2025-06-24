package com.example.myspbstu.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScheduleApiFactory {
    companion object{
        private const val BASE_URL = "https://ruz.spbstu.ru/api/v1/ruz/"

        private val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val scheduleApiService : ScheduleApiService = retrofit.create(ScheduleApiService::class.java)
    }
}