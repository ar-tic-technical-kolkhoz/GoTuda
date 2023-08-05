package com.vk59.gotuda.presentation.main.buttons

import dagger.assisted.AssistedFactory

@AssistedFactory
interface MainButtonsGoViewFactory {

  fun create(viewModel: MainButtonsViewModel): MainButtonsGoView
}