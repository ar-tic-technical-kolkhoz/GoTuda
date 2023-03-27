package com.vk59.gotuda

import android.Manifest.permission
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.data.PlacesRepository
import com.vk59.gotuda.data.model.PlaceDto
import com.vk59.gotuda.design.button_list.ButtonUiModel
import com.vk59.gotuda.map.data.LocationRepository
import com.vk59.gotuda.map.model.GoGeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

  // Inject
  private val locationRepository: LocationRepository = LocationRepository()

  val mapViewType: LiveData<MapViewType>
    get() = _mapViewType

  private val _mapViewType = MutableLiveData(MAPKIT)

  val debugButtonsShown: StateFlow<Boolean>
    get() = _debugButtonsShown.asStateFlow()

  private val _debugButtonsShown = MutableStateFlow(BuildConfig.DEBUG)

  private fun setViewType(viewType: MapViewType) {
    _mapViewType.value = viewType
  }

  private val placesRepository = PlacesRepository()

  private val mapObjectsFlow = MutableStateFlow<List<PlaceDto>>(emptyList())

  private val move = MutableStateFlow(Move(GoGeoPoint(0.0, 0.0)))

  fun listenToButtons(): LiveData<List<ButtonUiModel>> {
    val buttons = listOf(
      ButtonUiModel("osm", "Open Street Map", onClick = { setViewType(OSM) }),
      ButtonUiModel("mapkit", "MapKit", onClick = { setViewType(MAPKIT) }),
      ButtonUiModel("showButtons", "Buttons show", onClick = { _debugButtonsShown.value = !debugButtonsShown.value })
    )
    return MutableLiveData(buttons)
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  fun listenToUserGeo(locationManager: LocationManager): StateFlow<GoGeoPoint> {
    return locationRepository.listenToLocation(locationManager)
  }

  fun getPlaces() {
    viewModelScope.launch {
      mapObjectsFlow.value = placesRepository.getPlaces()
    }
  }

  fun listenToMapObjects(): StateFlow<List<PlaceDto>> {
    return mapObjectsFlow.asStateFlow()
  }

  fun listenToMove(): StateFlow<Move> {
    return move.asStateFlow()
  }

  fun moveToUserGeo() {
    val location = locationRepository.obtainLocation()
    move.value = Move(location)
  }
}

enum class MapViewType {
  OSM,
  MAPKIT
}

class Move(val goGeoPoint: GoGeoPoint)