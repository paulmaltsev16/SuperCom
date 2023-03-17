package com.supercom.paulmaltsev.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

private const val LOCATION_NOTIFICATION_CHANNEL_NAME = "location_channel_name"
const val LOCATION_NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            LOCATION_NOTIFICATION_CHANNEL_ID,
            LOCATION_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(notificationChannel)
        }
    }
}