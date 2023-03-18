package com.supercom.paulmaltsev.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.supercom.paulmaltsev.R

const val LOCATION_NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            LOCATION_NOTIFICATION_CHANNEL_ID,
            getString(R.string.location_tracking),
            NotificationManager.IMPORTANCE_LOW
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(notificationChannel)
        }
    }
}