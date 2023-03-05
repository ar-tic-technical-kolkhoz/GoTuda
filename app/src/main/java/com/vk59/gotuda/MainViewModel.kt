package com.vk59.gotuda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.button_list.ButtonUiModel
import com.vk59.gotuda.map.data.LocationRepository
import com.vk59.gotuda.map.model.GoGeoPoint
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

  // Inject
  private val locationRepository: LocationRepository = LocationRepository()

  val mapViewType: LiveData<MapViewType>
    get() = _mapViewType

  private val _mapViewType = MutableLiveData(MAPKIT)

  private val requestRequirements = MutableLiveData<() -> Unit>()

  private fun setViewType(viewType: MapViewType) {
    _mapViewType.value = viewType
  }

  fun listenToButtons(): LiveData<List<ButtonUiModel>> {
    val buttons = listOf(
      ButtonUiModel("osm", "Open Street Map", onClick = { setViewType(OSM) }),
      ButtonUiModel("mapkit", "MapKit", onClick = { setViewType(MAPKIT) })
    )
    return MutableLiveData(buttons)
  }

  fun listenToUserGeo(): StateFlow<GoGeoPoint> {
    return locationRepository.listenToLocation { requestRequirements.value = it }
  }

  fun listenToRequestRequirements(): LiveData<() -> Unit> {
    // TODO: Move to Requirements delegate (?)
    return requestRequirements
  }
}

enum class MapViewType {
  OSM,
  MAPKIT
}