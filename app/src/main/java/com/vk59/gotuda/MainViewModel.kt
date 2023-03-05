package com.vk59.gotuda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.button_list.ButtonUiModel

class MainViewModel : ViewModel() {

  private val _mapViewType = MutableLiveData(MAPKIT)

  val mapViewType: LiveData<MapViewType>
    get() = _mapViewType

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
}

enum class MapViewType {
  OSM,
  MAPKIT
}