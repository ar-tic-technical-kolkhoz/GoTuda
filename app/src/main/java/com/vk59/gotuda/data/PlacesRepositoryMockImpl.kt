package com.vk59.gotuda.data

import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.mock.Mocks.placeMapList
import com.vk59.gotuda.data.mock.Mocks.places
import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceToVisit
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlacesRepositoryMockImpl @Inject constructor() : PlacesRepository {

  override suspend fun getPlacesMap(): List<PlaceMap> {
    return withContext(AppDispatcher.io()) {
      return@withContext placeMapList
    }
  }

  override suspend fun getPlaceById(id: String): PlaceToVisit? {
    return withContext(AppDispatcher.io()) {
      return@withContext places.find { it.id == id }
    }
  }
}