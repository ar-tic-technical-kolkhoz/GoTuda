package com.vk59.gotuda.di

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yandex.mapkit.user_location.UserLocationLayer

@SuppressLint("StaticFieldLeak")
object SimpleDi {

  lateinit var context: Context

  var userLocationLayer: UserLocationLayer? = null
}