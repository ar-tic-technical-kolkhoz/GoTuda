package com.vk59.gotuda.map.mapkit

import com.vk59.gotuda.map.actions.MapActionsListener
import com.vk59.gotuda.map.model.MyGeoPoint
import dagger.assisted.AssistedFactory

@AssistedFactory
interface MapOverlayFactory {

  fun create(
    initialGeoPoint: MyGeoPoint?,
    mapActionsListener: MapActionsListener
  ): MapKitOverlay
}