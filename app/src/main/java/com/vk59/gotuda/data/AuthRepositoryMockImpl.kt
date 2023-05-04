package com.vk59.gotuda.data

import com.vk59.gotuda.data.model.AuthResult
import com.vk59.gotuda.data.model.RegisterUserModel
import com.vk59.gotuda.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryMockImpl @Inject constructor(
  private val accountSharedRepository: AccountSharedRepository,
) : AuthRepository {

  private val authorizedFlow = MutableStateFlow(AuthResult())

  override fun isAuthorized(): Flow<AuthResult> {
    return authorizedFlow.asStateFlow()
  }

  override suspend fun getUserByEmail(email: String): UserInfo? {
    val userInfo = accountSharedRepository.getUserInfo()
    if (userInfo != null) {
      authorizedFlow.value = AuthResult(true, userInfo)
    }
    return userInfo
  }

  override suspend fun registerUser(userInfo: RegisterUserModel): UserInfo {
    val user = UserInfo("5", userInfo.googleToken, userInfo.email)
    authorizedFlow.value = AuthResult(true, user)
    return user
  }

  override fun authorized(userInfo: UserInfo) {
    authorizedFlow.value = AuthResult(true, userInfo)
  }
}