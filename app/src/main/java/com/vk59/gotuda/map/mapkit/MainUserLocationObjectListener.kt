package com.vk59.gotuda.map.mapkit

import android.content.Context
import com.vk59.gotuda.R
import com.vk59.gotuda.di.SimpleDi
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider

class MainUserLocationObjectListener(private val context: Context = SimpleDi.context) : UserLocationObjectListener {

  override fun onObjectAdded(userLocationView: UserLocationView) {
    userLocationView.pin.setIcon(
      ImageProvider.fromResource(context, R.drawable.user_geo)
    )
  }

  override fun onObjectRemoved(p0: UserLocationView) {
  }

  override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
  }
}