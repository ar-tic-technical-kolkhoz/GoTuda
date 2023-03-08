package com.vk59.gotuda.map.data

import android.Manifest.permission
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import androidx.annotation.RequiresPermission
import com.vk59.gotuda.map.model.GoGeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository {

  // TODO: Change to last known state
  private val locationStateFlow = MutableStateFlow(GoGeoPoint(0.0, 0.0))

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  fun listenToLocation(locationManager: LocationManager): StateFlow<GoGeoPoint> {
    requestGeoUpdates(locationManager)
    forceRequestGeoUpdates(locationManager)
    return locationStateFlow.asStateFlow()
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  fun forceRequestGeoUpdates(locationManager: LocationManager): GoGeoPoint {
    val location = locationManager.getLastKnownLocation(GPS_PROVIDER)
    val result = location?.let { GoGeoPoint(location.latitude, location.longitude) } ?: GoGeoPoint(0.0, 0.0)
    locationStateFlow.value = result
    return result
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun requestGeoUpdates(manager: LocationManager) {
    manager.requestLocationUpdates(
      getBestProvider(manager),
      5000,
      3f,
      onLocationChanged
    )
  }

  private val onLocationChanged = LocationListener { location ->
    locationStateFlow.value = GoGeoPoint(location.latitude, location.longitude)
  }

  private fun getBestProvider(manager: LocationManager): String {
    return manager.getBestProvider(Criteria().apply { isSpeedRequired = true }, true) ?: GPS_PROVIDER
  }
}