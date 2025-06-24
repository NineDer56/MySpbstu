package com.example.myspbstu.data.retrofit

import com.example.myspbstu.data.retrofit.dto.GroupResponse
import com.example.myspbstu.data.retrofit.dto.ScheduleNwModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApiService {

    @GET("search/groups")
    suspend fun getGroupsByName(@Query("q") name: String) : GroupResponse

    @GET("scheduler/{groupId}")
    suspend fun getScheduleByGroupId(
        @Path("groupId") groupId : Int,
        @Query("date") date : String
    ) : ScheduleNwModel
}