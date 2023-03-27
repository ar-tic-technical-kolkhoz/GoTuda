package com.vk59.gotuda.data

import com.vk59.gotuda.data.Mocks.placeDtos
import com.vk59.gotuda.data.model.PlaceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlacesRepository {

  suspend fun getPlaces(): List<PlaceDto> {
    return withContext(Dispatchers.IO) {
      return@withContext placeDtos
    }
  }
}