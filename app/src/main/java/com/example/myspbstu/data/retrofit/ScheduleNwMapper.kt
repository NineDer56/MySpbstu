package com.example.myspbstu.data.retrofit

import com.example.myspbstu.data.retrofit.dto.AuditoryNwModel
import com.example.myspbstu.data.retrofit.dto.BuildingNwModel
import com.example.myspbstu.data.retrofit.dto.DayNwModel
import com.example.myspbstu.data.retrofit.dto.FacultyNwModel
import com.example.myspbstu.data.retrofit.dto.GroupNwModel
import com.example.myspbstu.data.retrofit.dto.LessonNwModel
import com.example.myspbstu.data.retrofit.dto.LessonTypeNwModel
import com.example.myspbstu.data.retrofit.dto.ScheduleNwModel
import com.example.myspbstu.data.retrofit.dto.TeacherNwModel
import com.example.myspbstu.data.retrofit.dto.WeekNwModel
import com.example.myspbstu.domain.model.Auditory
import com.example.myspbstu.domain.model.Building
import com.example.myspbstu.domain.model.Day
import com.example.myspbstu.domain.model.Faculty
import com.example.myspbstu.domain.model.Group
import com.example.myspbstu.domain.model.Lesson
import com.example.myspbstu.domain.model.LessonType
import com.example.myspbstu.domain.model.Schedule
import com.example.myspbstu.domain.model.Teacher
import com.example.myspbstu.domain.model.Week

class ScheduleNwMapper {

    fun mapScheduleNwModelToEntity(nwModel: ScheduleNwModel) : Schedule{
        return Schedule(
            week = mapWeekNwModelToEntity(nwModel.week),
            days = nwModel.days.map { mapDayNwModelToEntity(it) }
        )
    }

    fun mapWeekNwModelToEntity(nwModel: WeekNwModel) : Week {
        return Week(
            dateStart = nwModel.dateStart,
            dateEnd = nwModel.dateEnd,
            idOdd = nwModel.idOdd
        )
    }

    fun mapDayNwModelToEntity(nwModel: DayNwModel) : Day{
        return Day(
            weekday = nwModel.weekday,
            date = nwModel.date,
            lessons = nwModel.lessons.map { mapLessonNwModelToEntity(it) }
        )
    }

    fun mapLessonNwModelToEntity(nwModel: LessonNwModel) : Lesson {
        return Lesson(
            subject = nwModel.subject,
            timeStart = nwModel.timeStart,
            timeEnd = nwModel.timeEnd,
            lessonType = mapLessonTypeNwModelToEntity(nwModel.lessonType),
            groups = nwModel.groups.map { mapGroupNwModelToEntity(it) },
            teachers = nwModel.teachers?.map { mapTeacherToNwModel(it) } ?: listOf(Teacher(-1, "Не знаю кто")),
            auditories = nwModel.auditories.map { mapAuditoryNwModelToEntity(it) }
        )
    }

    fun mapLessonTypeNwModelToEntity(nwModel: LessonTypeNwModel) : LessonType{
        return LessonType(
            id = nwModel.id,
            name = nwModel.name,
            abbr = nwModel.abbr
        )
    }

    fun mapGroupNwModelToEntity(nwModel: GroupNwModel) : Group{
        return Group(
            id = nwModel.id,
            name = nwModel.name,
            faculty = mapLessonTypeNwModelToEntity(nwModel.faculty),
            level = nwModel.level
        )
    }

    fun mapLessonTypeNwModelToEntity(nwModel: FacultyNwModel) : Faculty{
        return Faculty(
            id = nwModel.id,
            name = nwModel.name,
            abbr = nwModel.abbr
        )
    }

    fun mapTeacherToNwModel(nwModel : TeacherNwModel) : Teacher{
        return Teacher(
            id = nwModel.id,
            name = nwModel.name
        )
    }

    fun mapAuditoryNwModelToEntity(nwModel: AuditoryNwModel) : Auditory{
        return Auditory(
            id = nwModel.id,
            name = nwModel.name,
            building = mapBuildingNwModelToEntity(nwModel.building)
        )
    }

    fun mapBuildingNwModelToEntity(nwModel: BuildingNwModel) : Building{
        return Building(
            id = nwModel.id,
            name = nwModel.name,
            abbr = nwModel.abbr
        )
    }
}












