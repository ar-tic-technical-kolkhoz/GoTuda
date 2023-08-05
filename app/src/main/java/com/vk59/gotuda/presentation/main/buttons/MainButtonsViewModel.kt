package com.vk59.gotuda.presentation.main.buttons

import kotlinx.coroutines.flow.Flow

interface MainButtonsViewModel {

  fun settingsClicked()

  fun moveToUserGeo()

  fun listenToLocationButton(): Flow<Boolean>

  fun followToUserLocation()
}