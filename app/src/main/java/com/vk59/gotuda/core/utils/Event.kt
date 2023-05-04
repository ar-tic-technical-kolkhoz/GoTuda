package com.vk59.gotuda.core.utils

sealed class Event {

  object Success : Event()

  class Error(val throwable: Throwable) : Event()

  object Loading : Event()
}
