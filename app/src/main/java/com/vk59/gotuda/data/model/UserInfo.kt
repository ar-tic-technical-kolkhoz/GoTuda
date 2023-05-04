package com.vk59.gotuda.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
  @SerialName("id")
  val id: String?,
  @SerialName("googleToken")
  val googleToken: String,
  @SerialName("email")
  val email: String
)
