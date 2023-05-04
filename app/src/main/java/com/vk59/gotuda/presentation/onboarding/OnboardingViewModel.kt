package com.vk59.gotuda.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.core.utils.Event
import com.vk59.gotuda.data.AccountSharedRepository
import com.vk59.gotuda.data.AuthRepository
import com.vk59.gotuda.data.model.RegisterUserModel
import com.vk59.gotuda.data.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val accountSharedRepository: AccountSharedRepository
) : ViewModel() {

  private val status: MutableStateFlow<Event> = MutableStateFlow(Event.Success)

  private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    onError.invoke(throwable)
  }

  private val onError: (Throwable) -> Unit = { throwable ->
    throwable.printStackTrace()
    status.value = Event.Error(throwable)
  }

  fun listenToEvent(): Flow<Event> {
    return status
  }

  fun authorize(account: GoogleSignInAccount) {
    status.value = Event.Loading
    val email = account.email
    val googleId = account.id
    val name = account.displayName
    if (email == null || googleId == null || name == null) {
      status.value = Event.Error(NullPointerException("email, googleId and name must be not null"))
      return
    }
    viewModelScope.launch(AppDispatcher.io() + exceptionHandler) {
      try {
        val user = authRepository.getUserByEmail(email)
        registerOrSaveUser(user, email, name, googleId)
        status.value = Event.Success
      } catch (e: Throwable) {
        registerOrSaveUser(null, email, name, googleId)
        onError.invoke(e)
      }
    }
  }

  private suspend fun registerOrSaveUser(
    user: UserInfo?,
    email: String,
    name: String,
    googleId: String
  ) {
    return if (user == null) {
      registerUser(email, name, googleId)
    } else {
      saveUser(user)
    }
  }

  private suspend fun registerUser(email: String, name: String, googleId: String) {
    authRepository.registerUser(RegisterUserModel(null, googleId, name, email))
  }

  private suspend fun saveUser(userInfo: UserInfo) {
    authRepository.authorized(userInfo)
    accountSharedRepository.saveUserInfo(userInfo)
  }
}