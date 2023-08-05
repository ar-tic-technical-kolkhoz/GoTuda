package com.vk59.gotuda.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsRepository @Inject constructor() {

  private val _availableToListenToGeo = MutableStateFlow(false)

  val availableToListenToGeo: Flow<Boolean>
    get() = _availableToListenToGeo

  fun permissionsGranted(boolean: Boolean) {
    _availableToListenToGeo.value = boolean
  }
}