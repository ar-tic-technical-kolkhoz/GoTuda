package com.vk59.gotuda

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.vk59.gotuda.di.SimpleDi
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class GoTudaApplication() : Application() {

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, false)

    if (BuildConfig.DEBUG) {
      Timber.plant(DebugTree())
    }
    if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
      val client = AndroidFlipperClient.getInstance(this)
      client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
      client.start()
    }

    MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

    SimpleDi.context = applicationContext
  }
}