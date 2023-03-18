package com.supercom.paulmaltsev.core.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

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