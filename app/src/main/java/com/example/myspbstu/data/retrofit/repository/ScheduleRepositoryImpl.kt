package com.example.myspbstu.data.retrofit.repository

import com.example.myspbstu.data.retrofit.ScheduleApiService
import com.example.myspbstu.data.retrofit.ScheduleNwMapper
import com.example.myspbstu.di.IoDispatcher
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val nwMapper: ScheduleNwMapper,
    private val apiService: ScheduleApiService,
    @IoDispatcher private val io : CoroutineDispatcher
) : ScheduleRepository {

    override fun getGroupsByName(name: String): Flow<List<Group>> =
        flow {
            val response = apiService.getGroupsByName(name).groups
            emit(response.map { nwMapper.mapGroupNwModelToEntity(it) })
        }
            .retry(2) { true }
            .flowOn(io)


    override fun getScheduleByGroupId(groupId: Int, date: String): Flow<Schedule> =
        flow {
            emit(
                nwMapper.mapScheduleNwModelToEntity(
                    apiService.getScheduleByGroupId(groupId, date)
                )
            )
        }
            .retry(2) { true }
            .flowOn(io)


    override fun getTeachersByName(name: String): Flow<List<Teacher>> =
        flow {
            emit(
                apiService.getTeachersByName(name).teachers
                    .map { nwMapper.mapTeacherNwModelToEntity(it) })
        }
            .retry(2) { true }
            .flowOn(io)


    override fun getScheduleByTeacherId(teacherId: Int, date: String): Flow<Schedule> =
        flow {
            emit(
                nwMapper.mapScheduleNwModelToEntity(
                    apiService.getScheduleByTeacherId(teacherId, date)
                )
            )
        }
            .retry(2) { true }
            .flowOn(io)
}