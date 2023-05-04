package com.vk59.gotuda.data

import android.content.SharedPreferences
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.model.UserInfo
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AccountSharedRepository @Inject constructor(
  private val sharedPreferences: SharedPreferences
) {

  suspend fun getAccount(name: String = ACCOUNT_GOOGLE_ID): String =
    withContext(AppDispatcher.io()) {
      sharedPreferences.getString(name, "") ?: ""
    }

  suspend fun saveUserInfo(userInfo: UserInfo) {
    withContext(AppDispatcher.io()) {
      val data = Json.encodeToString(userInfo)
      sharedPreferences.edit()
        .putString(USER_INFO, data)
        .apply()
    }
  }

  suspend fun getUserInfo(): UserInfo? {
    return withContext(AppDispatcher.io()) {
      val data = sharedPreferences.getString(USER_INFO, null)
      if (data != null) {
        Json.decodeFromString(UserInfo.serializer(), data)
      } else {
        null
      }
    }
  }

  fun saveCurrentWalletId(idWallet: Long) {
    sharedPreferences.edit()
      .putLong(ID_WALLET, idWallet)
      .apply()
  }

  fun getCurrentWalletId() {
    sharedPreferences.getLong(ID_WALLET, 1)
  }

  companion object {
    const val USER_INFO = "user_info"
    const val ACCOUNT_EMAIL = "account_email"
    const val ACCOUNT_GOOGLE_ID = "account_id"
    const val ACCOUNT_ID_TOKEN = "account_id_token"
    const val ACCOUNT_ID_USER = "account_id_user"
    const val ID_WALLET = "id_wallet"
  }
}
