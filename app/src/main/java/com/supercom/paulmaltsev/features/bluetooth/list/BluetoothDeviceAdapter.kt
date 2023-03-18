package com.supercom.paulmaltsev.features.bluetooth.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.supercom.paulmaltsev.databinding.ItemBluetoothDeviceBinding
import com.supercom.paulmaltsev.features.bluetooth.enteties.BluetoothDeviceItem
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("NotifyDataSetChanged")
class BluetoothDeviceAdapter(
    private var scannedDeviceList: ArrayList<BluetoothDeviceItem>
) : RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>(), Filterable {

    private var filteredList = arrayListOf<BluetoothDeviceItem>()

    init {
        filteredList = scannedDeviceList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBluetoothDeviceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(it: ArrayList<BluetoothDeviceItem>) {
        scannedDeviceList = it
        filteredList = it
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemBluetoothDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bluetoothDeviceItem: BluetoothDeviceItem) {
            binding.itemBluetoothDeviceTitle.text = bluetoothDeviceItem.name ?: "-"
            binding.itemBluetoothDeviceAddress.text = bluetoothDeviceItem.address ?: "-"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val searchText = constraint.toString().lowercase(Locale.getDefault())
                var filteredList = arrayListOf<BluetoothDeviceItem>()

                if (searchText.isEmpty()) {
                    filteredList = scannedDeviceList
                } else {
                    for (item in scannedDeviceList) {
                        if (item.address?.lowercase()?.contains(searchText) == true) {
                            filteredList.add(item)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                val anyObject: Any? = results.values
                filteredList = if (anyObject is ArrayList<*>) {
                    anyObject.filterIsInstance<BluetoothDeviceItem>() as ArrayList<BluetoothDeviceItem>
                } else {
                    arrayListOf()
                }
                notifyDataSetChanged()
            }
        }
    }
}