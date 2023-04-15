package com.vk59.gotuda.map.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.map.model.MyGeoPoint
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.LazyThreadSafetyMode.NONE

class LastKnownLocationRepository(context: Context) {

  private val prefs by lazy(NONE) {
    context.getSharedPreferences(NAME_LAST_KNOWN_LOCATION_PREF, MODE_PRIVATE)
  }

  suspend fun saveLastKnownLocation(point: MyGeoPoint) {
    withContext(AppDispatcher.io()) {
      prefs.edit().putString(KEY_GEO_POINT, Json.encodeToString(point)).apply()
    }
  }

  suspend fun getLastKnownLocation(): MyGeoPoint? {
    return withContext(AppDispatcher.io()) {
      prefs.getString(KEY_GEO_POINT, null)?.let { Json.decodeFromString(it) }
    }
  }

  companion object {

    private const val NAME_LAST_KNOWN_LOCATION_PREF = "last_known_location"
    private const val KEY_GEO_POINT = "geo_point"
  }
}