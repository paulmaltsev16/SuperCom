package com.supercom.paulmaltsev.core.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import com.supercom.paulmaltsev.core.extensions.isBluetoothPermissionGranted
import com.supercom.paulmaltsev.features.bluetooth.enteties.BluetoothDeviceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class BluetoothClient(
    private val context: Context
) : BluetoothController {

    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter by lazy { bluetoothManager?.adapter }
    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceItem>>(emptyList())
    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devicesList ->
            val newDevice = BluetoothDeviceItem(device.name, device.address)
            // If found device exist in list returns this list else add new device to list
            if (newDevice in devicesList) {
                devicesList
            } else {
                devicesList + newDevice
            }
        }
    }

    override val scannedDevices: StateFlow<List<BluetoothDeviceItem>>
        get() = _scannedDevices.asStateFlow()

    override fun startScanForDevices() {
        if (!context.isBluetoothPermissionGranted()) {
            return
        }

        context.registerReceiver(foundDeviceReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopScanForDevices() {
        if (!context.isBluetoothPermissionGranted()) {
            return
        }

        // In case receiver was unregistered
        try {
            context.unregisterReceiver(foundDeviceReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        bluetoothAdapter?.cancelDiscovery()
    }
}