package com.vk59.gotuda.data

import com.vk59.gotuda.data.model.AuthResult
import com.vk59.gotuda.data.model.RegisterUserModel
import com.vk59.gotuda.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

  fun isAuthorized(): Flow<AuthResult>

  suspend fun getUserByEmail(email: String): UserInfo?

  suspend fun registerUser(userInfo: RegisterUserModel): UserInfo

  fun authorized(userInfo: UserInfo)
}