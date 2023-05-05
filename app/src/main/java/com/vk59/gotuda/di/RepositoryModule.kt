package com.vk59.gotuda.di

import com.vk59.gotuda.data.AuthRepository
import com.vk59.gotuda.data.AuthRepositoryMockImpl
import com.vk59.gotuda.data.PlacesRepository
import com.vk59.gotuda.data.PlacesRepositoryMockImpl
import com.vk59.gotuda.data.RecommendationRepository
import com.vk59.gotuda.data.RecommendationRepositoryMockImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

  @Binds
  fun authRepositoryMock(repository: AuthRepositoryMockImpl): AuthRepository

  @Binds
  fun recommendationRepositoryMock(repository: RecommendationRepositoryMockImpl): RecommendationRepository

  @Binds
  fun placesRepositoryMock(repository: PlacesRepositoryMockImpl): PlacesRepository
}