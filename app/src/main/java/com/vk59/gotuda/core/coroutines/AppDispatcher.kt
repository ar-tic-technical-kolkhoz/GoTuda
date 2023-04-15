package com.vk59.gotuda.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Singleton */
object AppDispatcher {

  fun io(): CoroutineDispatcher = Dispatchers.IO
}