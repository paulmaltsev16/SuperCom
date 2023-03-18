package com.supercom.paulmaltsev.features.bluetooth.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.supercom.paulmaltsev.core.bluetooth.BluetoothClient

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothClient: BluetoothClient = BluetoothClient(application)
    private var _isScanningInProgress = false
    val isScanningInProgress get() = _isScanningInProgress
    val scannedDevices = bluetoothClient.scannedDevices

    fun scanForBluetoothDevices(function: (Boolean) -> Unit) {
        if (_isScanningInProgress) {
            bluetoothClient.stopScanForDevices()
        } else {
            bluetoothClient.startScanForDevices()
        }

        _isScanningInProgress = !_isScanningInProgress
        function(_isScanningInProgress)
    }
}