package com.vk59.gotuda.data

import android.Manifest.permission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsRepository @Inject constructor() {

  private val grantedPermissions = MutableStateFlow<List<String>>(emptyList())

  val isLocationPermissionsGranted: Flow<Boolean>
    get() = grantedPermissions.map { it.containsAll(LOCATION_PERMISSIONS) }


  val isCameraPermissionsGranted: Flow<Boolean>
    get() = grantedPermissions.map { it.containsAll(CAMERA_PERMISSION) }

  fun permissionGranted(permissions: List<String>) {
    grantedPermissions.value += permissions
  }

  companion object {

    val LOCATION_PERMISSIONS = listOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)

    val CAMERA_PERMISSION = listOf(permission.CAMERA)
  }
}