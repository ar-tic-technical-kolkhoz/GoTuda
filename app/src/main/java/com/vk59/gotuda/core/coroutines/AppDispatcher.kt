package com.vk59.gotuda.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Singleton */
object AppDispatcher {

  fun main(): CoroutineDispatcher = Dispatchers.Main

  fun io(): CoroutineDispatcher = Dispatchers.IO
}