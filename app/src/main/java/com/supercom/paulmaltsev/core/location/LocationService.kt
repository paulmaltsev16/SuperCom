package com.supercom.paulmaltsev.core.location

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.supercom.paulmaltsev.R
import com.supercom.paulmaltsev.core.LOCATION_NOTIFICATION_CHANNEL_ID
import com.supercom.paulmaltsev.core.data.locale.LocationDatabase
import com.supercom.paulmaltsev.features.map.entities.LocationItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationListener: LocationListener

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val FOREGROUND_SERVICE_ID = 1
        const val LOCATION_UPDATES_INTERVALS = 1000L
        const val LOCATION_LIMIT_IN_DB = 20
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationListener = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startService()
            ACTION_STOP -> stopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun startService() {
        val notification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_location))
            .setSmallIcon(R.drawable.ic_location)
            .build()

        locationListener
            .getLocationUpdates(LOCATION_UPDATES_INTERVALS)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                saveLocationToDatabase(location)
            }
            .launchIn(serviceScope)

        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun saveLocationToDatabase(location: Location) {
        serviceScope.launch {
            // Saves new location to db
            val dao = LocationDatabase.getDatabase(this@LocationService).getLocationDao()
            val locationItem = LocationItem(0, location.latitude, location.longitude)
            dao.insert(locationItem)

            // Removes oldest location if limit was reached
            if (dao.getLocationsCount() > LOCATION_LIMIT_IN_DB) {
                val oldestId = dao.getOldestId() ?: return@launch
                dao.deleteById(oldestId)
            }
        }
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }
}