package com.vk59.gotuda.map.data

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vk59.gotuda.di.SimpleDi
import com.vk59.gotuda.map.model.GoGeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository(private val context: Context = SimpleDi.context) {

  private val locationManager: LocationManager? = ContextCompat.getSystemService(context, LocationManager::class.java)

  // TODO: Change to last known state
  private val locationStateFlow = MutableStateFlow(GoGeoPoint(0.0, 0.0))

  fun listenToLocation(onPermissionRequired: (() -> Unit) -> Unit): StateFlow<GoGeoPoint> {
    locationManager?.let {
      if (VERSION.SDK_INT >= VERSION_CODES.P) {
        initLocationManager(it, onPermissionRequired)
      } else {
        initLocationManagerLessP(it, onPermissionRequired)
      }
    }
    return locationStateFlow.asStateFlow()
  }

  @RequiresApi(VERSION_CODES.P)
  private fun initLocationManager(manager: LocationManager, onPermissionRequired: (() -> Unit) -> Unit) {
    if (manager.isLocationEnabled) {
      initLocationManagerLessP(manager, onPermissionRequired)
    }
  }

  private fun initLocationManagerLessP(manager: LocationManager, onPermissionRequired: (() -> Unit) -> Unit) {
    if (ActivityCompat.checkSelfPermission(
        context,
        permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      onPermissionRequired.invoke {
        requestGeoUpdates(manager)
      }
    } else {
      requestGeoUpdates(manager)
    }
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun requestGeoUpdates(manager: LocationManager) {
    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, onLocationChanged())
  }

  private fun onLocationChanged() = LocationListener { location ->
    locationStateFlow.value = GoGeoPoint(location.latitude, location.longitude)
  }
}