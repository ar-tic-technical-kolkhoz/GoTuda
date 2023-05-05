package com.vk59.gotuda.data

import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceToVisit

interface PlacesRepository {

  suspend fun getPlacesMap(): List<PlaceMap>

  suspend fun getPlaceById(id: String): PlaceToVisit?
}