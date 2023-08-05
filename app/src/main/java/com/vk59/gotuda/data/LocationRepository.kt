package com.vk59.gotuda.data

import android.Manifest.permission
import android.annotation.SuppressLint
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import androidx.annotation.RequiresPermission
import com.vk59.gotuda.map.model.MyGeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
  private val permissionsRepository: PermissionsRepository,
  private val lastKnownLocationRepository: LastKnownLocationRepository,
) {

  private val locationStateFlow = MutableStateFlow<MyGeoPoint?>(null)

  @SuppressLint("MissingPermission")
  fun listenToLocation(locationManager: LocationManager): Flow<MyGeoPoint> {
    return permissionsRepository.availableToListenToGeo.dropWhile { !it }
      .flatMapLatest {
        requestGeoUpdates(locationManager)
        forceRequestGeoUpdates(locationManager)
        locationStateFlow
          .onEach { point -> point?.let { lastKnownLocationRepository.saveLastKnownLocation(it) } }
          .onStart {
            emit(lastKnownLocationRepository.getLastKnownLocation() ?: MyGeoPoint(0.0, 0.0))
          }.filterNotNull()
      }
      .onStart { emit(MyGeoPoint.DEFAULT) }
  }

  fun obtainLocation(): MyGeoPoint? {
    return locationStateFlow.value
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  fun forceRequestGeoUpdates(locationManager: LocationManager): MyGeoPoint {
    val location = locationManager.getLastKnownLocation(getBestProvider(locationManager))
    val result = location?.let { MyGeoPoint(location.latitude, location.longitude) } ?: MyGeoPoint(0.0, 0.0)
    locationStateFlow.value = result
    return result
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun requestGeoUpdates(manager: LocationManager) {
    manager.requestLocationUpdates(
      getBestProvider(manager),
      1000,
      0.2f,
      onLocationChanged
    )
  }

  private val onLocationChanged = LocationListener { location ->
    locationStateFlow.value = MyGeoPoint(location.latitude, location.longitude)
  }

  private fun getBestProvider(manager: LocationManager): String {
    return manager.getBestProvider(Criteria().apply { isSpeedRequired = true }, true) ?: GPS_PROVIDER
  }
}