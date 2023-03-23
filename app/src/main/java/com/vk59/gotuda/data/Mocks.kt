package com.vk59.gotuda.data

import com.vk59.gotuda.presentation.model.Place
import com.vk59.gotuda.presentation.profile.ProfileListItem
import com.vk59.gotuda.presentation.profile.ProfileListItem.Button
import com.vk59.gotuda.presentation.profile.ProfileListItem.Header
import com.vk59.gotuda.presentation.profile.ProfileListItem.HistoryPlace
import com.vk59.gotuda.presentation.profile.ProfileListItem.Title
import com.vk59.gotuda.presentation.profile.ProfileListItem.TitleValue
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

  const val DEFAULT_PHOTO_URL = "https://crypto.ru/wp-content/plugins/q-auth/assets/img/default-user.png"

  val profileItems = listOf<ProfileListItem>(
    Header(
      "Иван Костылев",
      "https://million-wallpapers.ru/wallpapers/2/42/12171433047292316421/lezhashhaya-v-zadumchivosti-na-odeyale-zelenoglazaya-koshka.jpg?scale=1"
    ),
    Button("Редактировать"),
    Title("Статистика"),
    TitleValue("Процент принятия рекомендаций", "66%"),
    TitleValue("Посещено мест", "17"),
    TitleValue("Ваш любимый тег", "Спорт"),
    TitleValue("Пользователей похожих на вас", "21%"),
    Title("История посещений"),
    HistoryPlace(
      Place(
        "1",
        "Спортплощадка",
        "https://avatars.mds.yandex.net/i?id=2a000001870d922f2adab28a942252eea8a2-405250-fast-images&n=13"
      )
    ),
    HistoryPlace(
        Place(
          "2",
          "Спортплощадка",
          "https://avatars.mds.yandex.net/i?id=2a000001870d922f2adab28a942252eea8a2-405250-fast-images&n=13"
        )
        )
  )
}