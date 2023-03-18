package com.supercom.paulmaltsev.features.map.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.supercom.paulmaltsev.core.data.locale.LocationDatabase

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationDao = LocationDatabase.getDatabase(application).getLocationDao()
    val locationsLiveData get() = locationDao.read

}
