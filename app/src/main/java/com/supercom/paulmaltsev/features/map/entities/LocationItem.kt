package com.supercom.paulmaltsev.features.map.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LOCATION_TABLE")
data class LocationItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "LATITUDE") val latitude: Double,
    @ColumnInfo(name = "LONGITUDE") val longitude: Double
)