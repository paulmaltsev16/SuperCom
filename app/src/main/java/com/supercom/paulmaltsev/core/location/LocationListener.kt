package com.supercom.paulmaltsev.core.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationListener {

    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception()
}