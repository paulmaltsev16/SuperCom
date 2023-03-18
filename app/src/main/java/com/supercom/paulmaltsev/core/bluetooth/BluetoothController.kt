package com.supercom.paulmaltsev.core.bluetooth

import com.supercom.paulmaltsev.features.bluetooth.enteties.BluetoothDeviceItem
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDeviceItem>>
    fun startScanForDevices()
    fun stopScanForDevices()
}