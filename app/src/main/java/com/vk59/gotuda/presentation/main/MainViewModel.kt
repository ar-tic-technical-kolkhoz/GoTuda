package com.vk59.gotuda.presentation.main

import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.LastKnownLocationRepository
import com.vk59.gotuda.data.LocationRepository
import com.vk59.gotuda.data.MapWalkRoutesRepository
import com.vk59.gotuda.data.PlacesRepository
import com.vk59.gotuda.data.RecommendationRepository
import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.map.model.MyGeoPoint
import com.vk59.gotuda.presentation.main.MainFragmentState.FinishActivity
import com.vk59.gotuda.presentation.main.MainFragmentState.LaunchPlace
import com.vk59.gotuda.presentation.main.MainFragmentState.Main
import com.vk59.gotuda.presentation.main.MainFragmentState.MainButtonLoading
import com.vk59.gotuda.presentation.main.MapViewType.MAPKIT
import com.vk59.gotuda.presentation.main.RoutesState.None
import com.vk59.gotuda.presentation.main.buttons.MainButtonsViewModel
import com.vk59.gotuda.presentation.main.data.FollowUserLocationRepository
import com.yandex.mapkit.transport.masstransit.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val placesRepository: PlacesRepository,
  private val locationRepository: LocationRepository,
  private val mapWalkRoutesRepository: MapWalkRoutesRepository,
  private val recommendationRepository: RecommendationRepository,
  private val followUserLocationRepository: FollowUserLocationRepository,
  private val lastKnownLocationRepository: LastKnownLocationRepository
) : ViewModel(), MainButtonsViewModel {

  val mapViewType: LiveData<MapViewType>
    get() = _mapViewType
  private val _mapViewType = MutableLiveData(MAPKIT)

  private val mapObjectsFlow = MutableStateFlow<List<PlaceMap>>(emptyList())

  private val move = MutableStateFlow(Move(MyGeoPoint.DEFAULT))

  private val state = MutableStateFlow<MainFragmentState>(Main).also {
    it.onEach { errorFlow.value = ErrorState.None }
  }

  private val routesFlow = MutableStateFlow<RoutesState>(None)

  private val errorFlow = MutableStateFlow<ErrorState>(ErrorState.None)

  private val goToSettingsFlow = MutableStateFlow(false)

  private val goToQrFlow = MutableStateFlow(false)

  fun backPressed() {
    state.value = when (state.value) {
      is LaunchPlace -> Main
      else -> FinishActivity
    }
  }

  fun listenToUserGeo(locationManager: LocationManager): Flow<MyGeoPoint> {
    return followUserLocationRepository.isFollowing
      .flatMapLatest {
        if (it) {
          locationRepository.listenToLocation(locationManager)
        } else {
          flowOf(null)
        }
      }.filterNotNull()
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

  fun listenToRoutes(): StateFlow<RoutesState> {
    return routesFlow
  }

  fun changeFollowing(boolean: Boolean) {
    followUserLocationRepository.changeFollowing(boolean)
  }

  override fun settingsClicked() {
    goToSettingsFlow.value = true
  }

  fun listenToLaunchSettings(): Flow<Boolean> {
    return goToSettingsFlow.filter { it }
  }

  fun settingsOpened() {
    goToSettingsFlow.value = false
  }

  override fun moveToUserGeo() {
    val location = locationRepository.obtainLocation() ?: return
    move.value = Move(location)
  }

  override fun listenToLocationButton(): Flow<Boolean> {
    return followUserLocationRepository.isFollowing
  }

  override fun followToUserLocation() {
    changeFollowing(true)
  }

  suspend fun obtainInitialLocation(): MyGeoPoint? {
    return lastKnownLocationRepository.getLastKnownLocation()
  }

  override fun qrButtonClicked() {
    goToQrFlow.value = true
  }

  fun listenToQrButtonClicked(): Flow<Boolean> {
    return goToQrFlow.filter { it }
  }

  fun placeTapped(mapObjectId: String) {
    viewModelScope.launch(AppDispatcher.io()) {
      try {
        selectObject(mapObjectId)
        val place = placesRepository.getPlaceById(mapObjectId)
        place?.let { state.value = LaunchPlace(place) }
      } catch (t: Throwable) {
        errorFlow.value = ErrorState.Error(t)
      }
    }
  }

  fun selectObject(id: String) {
    val list = mapObjectsFlow.value.toMutableList()
    list.replaceAll { if (it.id == id) it.copy(selected = true) else it.copy(selected = false) }
    mapObjectsFlow.value = list
    viewModelScope.launch {
      val point1 = locationRepository.obtainLocation() ?: return@launch
      val point2 = list.find { it.id == id }?.geoPoint ?: return@launch
      try {
        val route = mapWalkRoutesRepository.getRoutes(point1, point2).firstOrNull() ?: return@launch
        routesFlow.value = RoutesState.WalkRoute(route)
      } catch (throwable: Throwable) {
        errorFlow.value = ErrorState.Error(throwable)
      }
    }
  }

  fun listenToError(): Flow<ErrorState> {
    return errorFlow
  }

  fun requestRecommendations() {
    // TODO: Exception handler
    viewModelScope.launch {
      try {
        state.value = MainButtonLoading
        val recommendation = recommendationRepository.getRecommendation()
        state.value = LaunchPlace(recommendation.place)
      } catch (t: Throwable) {
        errorFlow.value = ErrorState.Error(t)
      }
    }
  }

  fun deselectObject() {
    val list = mapObjectsFlow.value.toMutableList()
    list.replaceAll { if (it.selected) it.copy(selected = false) else it }
    mapObjectsFlow.value = list
    routesFlow.value = None
  }

  fun userQrOpened() {
    goToQrFlow.value = false
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
}

sealed interface RoutesState {

  object None : RoutesState

  class WalkRoute(val route: Route) : RoutesState
}

sealed interface ErrorState {

  object None : ErrorState

  class Error(val throwable: Throwable) : ErrorState
}