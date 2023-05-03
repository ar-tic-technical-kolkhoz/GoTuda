package com.vk59.gotuda.data

import com.vk59.gotuda.data.model.AuthResult

interface AuthRepository {

  suspend fun authorize(): AuthResult
}