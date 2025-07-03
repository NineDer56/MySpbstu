package com.example.myspbstu.presentation.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.myspbstu.R
import java.util.concurrent.TimeUnit

class ExamWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val typeOfExam = inputData.getString(TYPE_OF_EXAM) ?: ""
        val subject = inputData.getString(SUBJECT) ?: ""
        val time = inputData.getString(TIME_OF_EXAM) ?: ""
        //val sendTime = inputData.getString(SEND_TIME) ?: ""
        showNotification(typeOfExam, subject, time)
        return Result.success()
    }

    private fun showNotification(typeOfExam : String, subject : String, time : String){
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.spbstu_notif_icon)
            .setContentTitle(typeOfExam)
            .setContentText("По \"$subject\" завтра в $time")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val examId = "$typeOfExam-$subject-$time".hashCode()

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(applicationContext).notify(examId, notification)
        }
    }

    companion object {
        const val NAME = "ExamWorker"
        const val CHANNEL_ID = "examChanelId"

        const val TYPE_OF_EXAM = "typeOfExam"
        const val SUBJECT = "subject"
        const val TIME_OF_EXAM = "timeOfExam"
        //const val SEND_TIME = "sendTime"

        fun makeRequest(typeOfExam : String, subject : String, time : String, delay : Long) : OneTimeWorkRequest{
            val inputData = workDataOf(
                TYPE_OF_EXAM to typeOfExam,
                SUBJECT to subject,
                TIME_OF_EXAM to time
            )

            return OneTimeWorkRequestBuilder<ExamWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()
        }
    }
}