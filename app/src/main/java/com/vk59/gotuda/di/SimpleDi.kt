package com.vk59.gotuda.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.MultipleMapDelegate
import com.vk59.gotuda.map.data.MapConfigDataSource

@SuppressLint("StaticFieldLeak")
object SimpleDi {

  lateinit var context: Context

  fun multipleMapDelegate(fragment: Fragment): MultipleMapDelegate {
    return MultipleMapDelegate(MapConfigDataSource.obtainMapViewDelegates(fragment))
  }
}