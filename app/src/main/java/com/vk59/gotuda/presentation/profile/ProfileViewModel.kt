package com.vk59.gotuda.presentation.profile

import androidx.lifecycle.ViewModel
import com.vk59.gotuda.data.Mocks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

  private val profileListItems = MutableStateFlow<List<ProfileListItem>>(emptyList())

  init {
    profileListItems.value = Mocks.profileItems
  }

  fun listenToListItems(): StateFlow<List<ProfileListItem>> {
    return profileListItems
  }
}