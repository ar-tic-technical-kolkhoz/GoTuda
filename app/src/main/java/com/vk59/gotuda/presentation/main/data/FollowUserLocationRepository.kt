package com.vk59.gotuda.presentation.main.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FollowUserLocationRepository @Inject constructor() {

  private val _isFollowing = MutableStateFlow(true)
  val isFollowing: Flow<Boolean>
    get() = _isFollowing

  fun changeFollowing(boolean: Boolean) {
    _isFollowing.value = boolean
  }
}