package com.supercom.paulmaltsev

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.supercom.paulmaltsev.core.extensions.isNotificationPermissionGranted
import com.supercom.paulmaltsev.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.activityMain_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigationViewListener()
        requestNotificationPermission()
    }

    private fun initBottomNavigationViewListener() {
        binding.activityMainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuMain_map -> navController.navigate(R.id.action_global_mapFragment)
                R.id.menuMain_bluetooth -> navController.navigate(R.id.action_global_bluetoothFragment)
            }
            return@setOnItemSelectedListener true
        }
    }

    // On api 33 the app needs permission to send notification
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        if (!isNotificationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }
}