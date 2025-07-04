package com.example.myspbstu.presentation.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.application
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
        createNotificationChannel()

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Exam Notifications"
            val descriptionText = "Уведомления об экзаменах"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }



    companion object {
        const val CHANNEL_ID = "examChanelId"

        const val TYPE_OF_EXAM = "typeOfExam"
        const val SUBJECT = "subject"
        const val TIME_OF_EXAM = "timeOfExam"

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