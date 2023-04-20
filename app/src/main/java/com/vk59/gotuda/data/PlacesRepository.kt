package com.vk59.gotuda.data

import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.Mocks.placeMapList
import com.vk59.gotuda.data.Mocks.places
import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceToVisit
import kotlinx.coroutines.withContext

class PlacesRepository {

  suspend fun getPlacesMap(): List<PlaceMap> {
    return withContext(AppDispatcher.io()) {
      return@withContext placeMapList
    }
  }

  suspend fun getPlaceById(id: String): PlaceToVisit? {
    return withContext(AppDispatcher.io()) {
      return@withContext places.find { it.id == id }
    }
  }
}