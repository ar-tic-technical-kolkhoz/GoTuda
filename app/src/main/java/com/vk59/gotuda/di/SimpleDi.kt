package com.vk59.gotuda.di

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.MultipleMapDelegate
import com.vk59.gotuda.map.data.MapConfigDataSource
import com.yandex.mapkit.user_location.UserLocationLayer

@SuppressLint("StaticFieldLeak")
object SimpleDi {

  lateinit var context: Context

  fun multipleMapDelegate(fragment: Fragment): MultipleMapDelegate {
    return MultipleMapDelegate(MapConfigDataSource.obtainMapViewDelegates(fragment))
  }

  var userLocationLayer: UserLocationLayer? = null

  val handler: Handler by lazy {
    Handler(Looper.getMainLooper())
  }
}