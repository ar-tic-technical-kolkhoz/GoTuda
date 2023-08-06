package com.vk59.gotuda.presentation.qr.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk59.gotuda.presentation.qr.scan.ScanQrViewModel.Event.Loading
import com.vk59.gotuda.presentation.qr.scan.ScanQrViewModel.Event.NotStartedYet
import com.vk59.gotuda.presentation.qr.scan.ScanQrViewModel.Event.QrVerified
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class ScanQrViewModel : ViewModel() {

  private val _eventFlow = MutableStateFlow<Event>(NotStartedYet)

  val eventFlow: Flow<Event>
    get() = _eventFlow

  fun qrDetected(qrText: String) {
    viewModelScope.launch {
      _eventFlow.value = Loading
      delay(300L)
      _eventFlow.value = QrVerified(Random().nextInt(10).toString())
    }
  }

  sealed interface Event {

    object NotStartedYet : Event

    object Loading : Event

    data class QrVerified(val rewardGos: String) : Event

    class Error(val throwable: Throwable) : Event
  }
}