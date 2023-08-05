package com.vk59.gotuda.presentation.qr.scan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

class ScanQrViewModel : ViewModel() {

  private val _qrFlow = MutableStateFlow<String?>(null)

  val qrTextFlow: Flow<String>
    get() = _qrFlow.filterNotNull().distinctUntilChanged()


  fun qrDetected(text: String) {
    _qrFlow.value = text
  }
}