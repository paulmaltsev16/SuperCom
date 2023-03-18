package com.supercom.paulmaltsev.features.bluetooth.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.supercom.paulmaltsev.core.bluetooth.BluetoothClient

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothClient: BluetoothClient = BluetoothClient(application)
    private var isScanningInProgress = true
    val scannedDevices = bluetoothClient.scannedDevices

    fun scanForBluetoothDevices(function: (Boolean) -> Unit) {
        if (isScanningInProgress) {
            bluetoothClient.stopScanForDevices()
        } else {
            bluetoothClient.startScanForDevices()
        }
        function(isScanningInProgress)
        isScanningInProgress = !isScanningInProgress
    }
}