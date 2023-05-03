package com.vk59.gotuda.data

import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.model.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AuthRepositoryMockImpl : AuthRepository {

  override suspend fun authorize(): AuthResult {
    return withContext(AppDispatcher.io()) {
      delay(500)
      AuthResult()
    }
  }
}