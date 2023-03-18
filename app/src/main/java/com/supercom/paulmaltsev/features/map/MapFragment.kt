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
import com.supercom.paulmaltsev.core.extensions.isLocationPermissionGranted
import com.supercom.paulmaltsev.core.location.LocationService
import com.supercom.paulmaltsev.databinding.FragmentMapBinding
import com.supercom.paulmaltsev.features.map.entities.LocationItem
import com.supercom.paulmaltsev.features.map.view_model.MapViewModel

private const val MAP_VIEW_ZOOM_LEVEL = 12f
private const val TEL_AVIV_LATITUDE = 32.0852999
private const val TEL_AVIV_LONGITUDE = 34.7817676
private val REQUIRED_PERMISSIONS = arrayOf(
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION
)

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
        initListeners()
        notifyUserAboutLocationPermissionStatus()
        observerLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initListeners() {
        // Map view
        (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)
            .getMapAsync { googleMap ->
                mMap = googleMap
                val telAviv = LatLng(TEL_AVIV_LATITUDE, TEL_AVIV_LONGITUDE)
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(telAviv))
                mMap?.animateCamera(CameraUpdateFactory.zoomTo(MAP_VIEW_ZOOM_LEVEL))
            }

        // On switch click listener
        binding.mapLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
            startTrackingLocation(isChecked)
        }
    }

    private fun startTrackingLocation(isTrack: Boolean) {
        if (!requireContext().isLocationPermissionGranted()) {
            locationPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            binding.mapLocationSwitch.isChecked = false
            return
        }

        Intent(requireActivity().applicationContext, LocationService::class.java).apply {
            action = if (isTrack) LocationService.ACTION_START else LocationService.ACTION_STOP
            requireActivity().startService(this)
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allPermissionGranted = REQUIRED_PERMISSIONS.all {
            permissionsResult[it] == true
        }

        if (allPermissionGranted) {
            startTrackingLocation(true)
            binding.mapLocationSwitch.isChecked = true
            notifyUserAboutLocationPermissionStatus()
        }
    }

    private fun notifyUserAboutLocationPermissionStatus() {
        binding.mapPermissionsDenied.visibility =
            if (!requireActivity().isLocationPermissionGranted()) {
                View.VISIBLE
            } else {
                View.GONE
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