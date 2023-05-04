package com.vk59.gotuda.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

  @Provides
  fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences {
    return context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
  }

  companion object {

    private const val SHARED_PREFS = "SHARED_PREFS"
  }
}