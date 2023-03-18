package com.supercom.paulmaltsev.features.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.supercom.paulmaltsev.R
import com.supercom.paulmaltsev.core.isLocationPermissionGranted
import com.supercom.paulmaltsev.core.location.LocationService
import com.supercom.paulmaltsev.databinding.FragmentMapBinding
import com.supercom.paulmaltsev.features.map.entities.LocationItem
import com.supercom.paulmaltsev.features.map.view_model.MapViewModel

const val MAP_VIEW_ZOOM_LEVEL = 20f

class MapFragment : Fragment() {

    private var mMap: GoogleMap? = null
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListenerToMapView()
        initLocationTrackingSwitchListener()
        notifyUserAboutLocationPermissionStatus()
        observerLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addListenerToMapView() {
        (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)
            .getMapAsync { googleMap ->
                mMap = googleMap
            }
    }

    private fun initLocationTrackingSwitchListener() {
        binding.mapLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!requireContext().isLocationPermissionGranted()) {
                requestLocationPermission()
                binding.mapLocationSwitch.isChecked = false
                return@setOnCheckedChangeListener
            }

            if (isChecked) {
                startTrackLocation()
            } else {
                stopTrackLocation()
            }
        }
    }


    // If the user has restricted location permission several times, he must open the
    // settings to allow this.
    private fun requestLocationPermission() {
        if (!requireContext().isLocationPermissionGranted()) {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        notifyUserAboutLocationPermissionStatus()
    }

    private fun notifyUserAboutLocationPermissionStatus() {
        binding.mapPermissionsDenied.visibility =
            if (!requireActivity().isLocationPermissionGranted()) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun startTrackLocation() {
        Intent(requireActivity().applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            requireActivity().startService(this)
        }
    }

    private fun stopTrackLocation() {
        Intent(requireActivity().applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            requireActivity().startService(this)
        }
    }

    private fun observerLocation() {
        viewModel.locationsLiveData.observe(viewLifecycleOwner) { locationList ->
            locationList.forEach {
                addMarkerToMap(it)
            }
        }
    }

    private fun addMarkerToMap(location: LocationItem) {
        val markerPosition = LatLng(location.latitude, location.longitude)
        mMap?.let {
            it.addMarker(MarkerOptions().position(markerPosition))
            it.moveCamera(CameraUpdateFactory.newLatLng(markerPosition))
            it.animateCamera(CameraUpdateFactory.zoomTo(MAP_VIEW_ZOOM_LEVEL))
        }
    }
}