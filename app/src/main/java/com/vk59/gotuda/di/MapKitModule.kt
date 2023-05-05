package com.vk59.gotuda.di

import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class MapKitModule {

  @Provides
  fun walkRouter(): PedestrianRouter {
    return TransportFactory.getInstance().createPedestrianRouter()
  }
}