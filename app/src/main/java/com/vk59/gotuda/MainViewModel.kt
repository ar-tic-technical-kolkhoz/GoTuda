package com.vk59.gotuda

import android.Manifest.permission
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk59.gotuda.MainFragmentState.ErrorState
import com.vk59.gotuda.MainFragmentState.FinishActivity
import com.vk59.gotuda.MainFragmentState.LaunchPlace
import com.vk59.gotuda.MainFragmentState.Main
import com.vk59.gotuda.MainFragmentState.MainButtonLoading
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.PlacesRepository
import com.vk59.gotuda.data.RecommendationRepository
import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.design.button_list.ButtonUiModel
import com.vk59.gotuda.di.SimpleDi
import com.vk59.gotuda.map.data.LastKnownLocationRepository
import com.vk59.gotuda.map.data.LocationRepository
import com.vk59.gotuda.map.model.MyGeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

  val mapViewType: LiveData<MapViewType>
    get() = _mapViewType
  private val _mapViewType = MutableLiveData(MAPKIT)

  val debugButtonsShown: StateFlow<Boolean>
    get() = _debugButtonsShown.asStateFlow()
  private val _debugButtonsShown = MutableStateFlow(BuildConfig.DEBUG)

  private val mapObjectsFlow = MutableStateFlow<List<PlaceMap>>(emptyList())

  private val move = MutableStateFlow(Move(MyGeoPoint(0.0, 0.0)))

  private val state = MutableStateFlow<MainFragmentState>(Main)

  // Inject
  private val locationRepository: LocationRepository = SimpleDi.locationRepository
  private val lastKnownLocationRepository: LastKnownLocationRepository = SimpleDi.lastKnownLocationRepository
  private val placesRepository: PlacesRepository = SimpleDi.placesRepository
  private val recommendationRepository: RecommendationRepository = SimpleDi.recommendationRepository

  fun listenToButtons(): LiveData<List<ButtonUiModel>> {
    val buttons = listOf(
      ButtonUiModel("osm", "Open Street Map", onClick = { setViewType(OSM) }),
      ButtonUiModel("mapkit", "MapKit", onClick = { setViewType(MAPKIT) }),
      ButtonUiModel("showButtons", "Buttons show", onClick = { _debugButtonsShown.value = !debugButtonsShown.value })
    )
    return MutableLiveData(buttons)
  }

  fun backPressed() {
    state.value = when(state.value) {
      is LaunchPlace -> Main
      else -> FinishActivity
    }
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  fun listenToUserGeo(locationManager: LocationManager): Flow<MyGeoPoint> {
    return locationRepository.listenToLocation(locationManager)
  }

  fun listenToFragmentState(): Flow<MainFragmentState> {
    return state.asStateFlow()
  }

  fun getPlaces() {
    viewModelScope.launch {
      mapObjectsFlow.value = placesRepository.getPlacesMap()
    }
  }

  fun listenToMapObjects(): StateFlow<List<PlaceMap>> {
    return mapObjectsFlow.asStateFlow()
  }

  fun listenToMove(): StateFlow<Move> {
    return move.asStateFlow()
  }

  fun moveToUserGeo() {
    val location = locationRepository.obtainLocation() ?: return
    move.value = Move(location)
  }

  suspend fun obtainInitialLocation(): MyGeoPoint? {
    return lastKnownLocationRepository.getLastKnownLocation()
  }

  private fun setViewType(viewType: MapViewType) {
    _mapViewType.value = viewType
  }

  fun placeTapped(mapObjectId: String) {
    viewModelScope.launch(AppDispatcher.io()) {
      try {
        selectObject(mapObjectId)
        val place = placesRepository.getPlaceById(mapObjectId)
        place?.let { state.value = LaunchPlace(place) }
      } catch (t: Throwable) {
        state.value = ErrorState(t)
      }
    }
  }

  private fun selectObject(id: String) {
    val list = mapObjectsFlow.value.toMutableList()
    list.replaceAll { if (it.id == id) it.copy(selected = true) else it.copy(selected = false) }
    mapObjectsFlow.value = list
  }

  fun requestRecommendations() {
    // TODO: Exception handler
    viewModelScope.launch {
      try {
        state.value = MainButtonLoading
        val recommendation = recommendationRepository.getRecommendation()
        state.value = LaunchPlace(recommendation.place)
      } catch (t: Throwable) {
        state.value = ErrorState(t)
      }
    }
  }

  fun deselectObject() {
    val list = mapObjectsFlow.value.toMutableList()
    list.replaceAll { if (it.selected) it.copy(selected = false) else it }
    mapObjectsFlow.value = list
  }
}

enum class MapViewType {
  OSM,
  MAPKIT
}

class Move(val geoPoint: MyGeoPoint)

sealed interface MainFragmentState {

  object FinishActivity : MainFragmentState

  object Main : MainFragmentState

  object MainButtonLoading : MainFragmentState

  class LaunchPlace(val place: PlaceToVisit) : MainFragmentState

  class ErrorState(val throwable: Throwable) : MainFragmentState
}