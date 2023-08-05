package com.vk59.gotuda.presentation.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class UserQrViewModel : ViewModel() {

  private val _qrUrl = MutableStateFlow<String?>(null)

  init {
    viewModelScope.launch {
      delay(1000)
      _qrUrl.value = "https://google.com"
    }
  }

  fun qrUrlFlow(): Flow<String> {
    return _qrUrl.filterNotNull()
  }
}