package com.vk59.gotuda.di

import com.vk59.gotuda.data.AuthRepository
import com.vk59.gotuda.data.AuthRepositoryMockImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

  @Binds
  fun authRepositoryMock(repository: AuthRepositoryMockImpl): AuthRepository
}