package com.supercom.paulmaltsev.features.bluetooth

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.supercom.paulmaltsev.R
import com.supercom.paulmaltsev.core.extensions.hideKeyboard
import com.supercom.paulmaltsev.core.extensions.isBluetoothPermissionGranted
import com.supercom.paulmaltsev.databinding.FragmentBluetoothBinding
import com.supercom.paulmaltsev.features.bluetooth.enteties.BluetoothDeviceItem
import com.supercom.paulmaltsev.features.bluetooth.list.BluetoothDeviceAdapter
import com.supercom.paulmaltsev.features.bluetooth.view_model.BluetoothViewModel

class BluetoothFragment : Fragment() {

    private var _binding: FragmentBluetoothBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BluetoothViewModel by viewModels()
    private val bluetoothDeviceAdapter = BluetoothDeviceAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBluetoothBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScannedDeviceRv()
        initListeners()
        observerScannedDevices()
    }

    override fun onResume() {
        super.onResume()
        showProgressBar(viewModel.isScanningInProgress)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initScannedDeviceRv() {
        binding.bluetoothRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bluetoothDeviceAdapter
            setHasFixedSize(true)
        }
    }

    private fun initListeners() {
        // Add listener to 'Enter'-key of keyboard
        binding.bluetoothFilterResult.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.bluetoothFilterResult.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.bluetoothScanBtn.setOnClickListener {
            scanForDevices()
        }

        binding.bluetoothFilterResult.doOnTextChanged { text, _, _, _ ->
            bluetoothDeviceAdapter.filter.filter(text)
        }
    }

    private fun scanForDevices() {
        if (!requireContext().isBluetoothPermissionGranted()
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
            )
            return
        }

        viewModel.scanForBluetoothDevices { isScanning ->
            showProgressBar(isScanning)
        }
    }

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        scanForDevices()
    }

    private fun showProgressBar(isShow: Boolean) {
        if (isShow) {
            binding.bluetoothScanBtn.text = getString(R.string.stop)
            binding.bluetoothProgressBar.visibility = View.VISIBLE
        } else {
            binding.bluetoothScanBtn.text = getString(R.string.scan)
            binding.bluetoothProgressBar.visibility = View.GONE
        }
    }

    private fun observerScannedDevices() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.scannedDevices.collect {
                val arrayList = arrayListOf<BluetoothDeviceItem>()
                arrayList.addAll(it)
                bluetoothDeviceAdapter.updateList(arrayList)
            }
        }
    }
}