package com.supercom.paulmaltsev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.supercom.paulmaltsev.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

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
        requestLocationPermission()
        notifyUserAboutLocationPermissionStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addListenerToMapView() {
        (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)
            .getMapAsync { googleMap ->
                mMap = googleMap
                val sydney = LatLng(-34.0, 151.0)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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
}