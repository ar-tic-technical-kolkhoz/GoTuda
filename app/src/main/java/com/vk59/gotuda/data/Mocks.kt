package com.vk59.gotuda.data

import com.vk59.gotuda.presentation.settings.Chip

object Mocks {

  val chips = listOf<Chip>(
    Chip("1,", "Спорт", true),
    Chip("1,", "Улица", true),
    Chip("1,", "Помещение", false),
    Chip("1,", "Кафе", true),
    Chip("1,", "Парк", false),
    Chip("1,", "Искусство", false),
    Chip("1,", "Стритарт", true),
  )
}