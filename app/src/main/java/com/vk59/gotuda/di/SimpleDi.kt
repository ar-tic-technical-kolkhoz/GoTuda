package com.vk59.gotuda.di

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.vk59.gotuda.data.PlacesRepository
import com.vk59.gotuda.data.RecommendationRepository
import com.vk59.gotuda.data.RecommendationRepositoryMock
import com.vk59.gotuda.map.MapController
import com.vk59.gotuda.map.data.LastKnownLocationRepository
import com.vk59.gotuda.map.data.LocationRepository
import com.yandex.mapkit.user_location.UserLocationLayer

@SuppressLint("StaticFieldLeak")
object SimpleDi {

  lateinit var context: Context

  var userLocationLayer: UserLocationLayer? = null

  val mapController: MapController by lazy {
    MapController()
  }

  val locationRepository: LocationRepository by lazy {
    LocationRepository()
  }

  val lastKnownLocationRepository: LastKnownLocationRepository by lazy {
    LastKnownLocationRepository(context)
  }

  val handler: Handler by lazy {
    Handler(Looper.getMainLooper())
  }

  val recommendationRepository: RecommendationRepository by lazy {
    RecommendationRepositoryMock()
  }

  val placesRepository: PlacesRepository by lazy {
    PlacesRepository()
  }
}