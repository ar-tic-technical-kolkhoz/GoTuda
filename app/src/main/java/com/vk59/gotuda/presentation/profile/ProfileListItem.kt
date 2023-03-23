package com.vk59.gotuda.presentation.profile

import com.vk59.gotuda.presentation.model.Place

sealed interface ProfileListItem {

  data class Header(val name: String, val photoUrl: String) : ProfileListItem

  data class Button(val text: String) : ProfileListItem

  data class Title(val text: String) : ProfileListItem

  data class TitleValue(val title: String, val value: String) : ProfileListItem

  data class HistoryPlace(val place: Place) : ProfileListItem
}