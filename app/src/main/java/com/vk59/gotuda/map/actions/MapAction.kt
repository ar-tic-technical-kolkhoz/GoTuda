package com.vk59.gotuda.map.actions

sealed interface MapAction {

  class SinglePlaceTap(val mapObjectId: String) : MapAction
}