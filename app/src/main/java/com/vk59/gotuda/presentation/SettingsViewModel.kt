package com.vk59.gotuda.presentation

import androidx.lifecycle.ViewModel
import com.vk59.gotuda.data.Mocks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

  val chipsState: StateFlow<List<Chip>>
    get() = _chipsState

  private val _chipsState = MutableStateFlow(Mocks.chips)
}

class Chip(val id: String, val name: String, val selected: Boolean)