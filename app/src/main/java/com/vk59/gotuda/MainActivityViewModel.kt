package com.vk59.gotuda

import androidx.lifecycle.ViewModel
import com.vk59.gotuda.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val authRepository: AuthRepository
) : ViewModel() {

  fun isAuthorized(): Flow<Boolean> {
    return authRepository.isAuthorized().map { it.authorized }
  }
}