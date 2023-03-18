package com.supercom.paulmaltsev.features.map.entities

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationItem: LocationItem)

    @get:Query("SELECT * FROM LOCATION_TABLE ORDER BY ID ASC")
    val read: LiveData<List<LocationItem>>

    @Query("SELECT COUNT(*) FROM LOCATION_TABLE")
    fun getLocationsCount(): Int

    @Query("SELECT id FROM LOCATION_TABLE ORDER BY ID ASC LIMIT 1")
    suspend fun getOldestId(): Int?

    @Query("DELETE FROM LOCATION_TABLE WHERE ID = :id")
    suspend fun deleteById(id: Int)
}