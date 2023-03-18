package com.supercom.paulmaltsev.core.data.locale

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.supercom.paulmaltsev.features.map.entities.LocationDao
import com.supercom.paulmaltsev.features.map.entities.LocationItem

private const val DATABASE_NAME = "supercom_database"

@Database(entities = [LocationItem::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun getLocationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): LocationDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                LocationDatabase::class.java,
                DATABASE_NAME,
            ).build()
    }
}