package com.vk59.gotuda.data.model

data class AuthResult(
  val authorized: Boolean = false,
  val userInfo: UserInfo? = null
)