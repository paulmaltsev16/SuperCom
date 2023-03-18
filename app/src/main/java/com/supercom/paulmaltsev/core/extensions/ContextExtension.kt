package com.supercom.paulmaltsev.core.extensions

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


fun Context.isLocationPermissionGranted(): Boolean {
    return this.isGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            && this.isGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
}

fun Context.isNotificationPermissionGranted(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return true
    }
    return this.isGranted(android.Manifest.permission.POST_NOTIFICATIONS)
}

fun Context.isBluetoothPermissionGranted(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return true
    }
    return this.isGranted(android.Manifest.permission.BLUETOOTH_SCAN)
            && this.isGranted(android.Manifest.permission.BLUETOOTH_CONNECT)
}

private fun Context.isGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = manager.getRunningServices(Integer.MAX_VALUE)
    for (service in runningServices) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}