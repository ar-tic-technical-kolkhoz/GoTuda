package com.vk59.gotuda.data.mock

import com.vk59.gotuda.data.model.PlaceMap
import com.vk59.gotuda.data.model.PlaceTag
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.design.button_list.ButtonUiModel
import com.vk59.gotuda.map.model.MyGeoPoint
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
    Chip("0", "Спорт", true),
    Chip("1", "Улица", true),
    Chip("2", "Помещение", false),
    Chip("3", "Кафе", true),
    Chip("4", "Парк", false),
    Chip("5", "Искусство", false),
    Chip("6", "Стритарт", true),
  )

  val debugButtons: List<ButtonUiModel> =
    listOf(
      ButtonUiModel("osm", "Open Street Map", onClick = { /* */ }),
      ButtonUiModel("mapkit", "MapKit", onClick = {  }),
      ButtonUiModel("showButtons", "Buttons show", onClick = { })
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

  val placeMapList = listOf(
    PlaceMap("1", MyGeoPoint(59.9596, 30.406043)),
    PlaceMap("2", MyGeoPoint(59.95916, 30.40609)),
    PlaceMap("3", MyGeoPoint(59.961103, 30.4069)),
    PlaceMap("4", MyGeoPoint(59.962986, 30.404977)),
    PlaceMap("5", MyGeoPoint(59.960530, 30.400957)),
  )

  val placeTags = listOf(
    PlaceTag("0", "Спорт"),
    PlaceTag("1", "Улица"),
    PlaceTag("2", "Помещение"),
    PlaceTag("3,", "Кафе"),
    PlaceTag("4", "Парк"),
    PlaceTag("5", "Искусство"),
    PlaceTag("6", "Стритарт")
  )

  val places = listOf(
    PlaceToVisit(
      id = "1",
      geoPoint = MyGeoPoint(59.9596, 30.406043),
      photoUrl = "https://roliki-magazin.ru/wp-content/uploads/a/d/0/ad090bc3f1096011e1ba2ac676681976.jpeg",
      name = "Спортплощадка",
      address = "Полюстровский парк",
      tags = listOf(placeTags[0], placeTags[4], placeTags[1])
    ), PlaceToVisit(
      id = "2",
      geoPoint = MyGeoPoint(59.95916, 30.40609),
      photoUrl = "https://avatars.mds.yandex.net/get-discovery-int/1339925/2a0000016d81ea8bf68486d3986d8ced10da/XXXL",
      name = "Музей Яндекса",
      address = "Пискаревский проспект, 2",
      tags = listOf(placeTags[5])
    ), PlaceToVisit(
      id = "3",
      geoPoint =MyGeoPoint(59.961103, 30.4069),
      photoUrl = "https://mebellka.ru/wp-content/uploads/8/6/5/86513168834f58885e887fb521a16b11.jpeg",
      name = "Пространство Кошка",
      address = "Улица Жукова, 3б",
      tags = listOf(placeTags[6], placeTags[4])
    ), PlaceToVisit(
      id = "4",
      geoPoint = MyGeoPoint(59.962986, 30.404977),
      photoUrl = "https://roliki-magazin.ru/wp-content/uploads/a/d/0/ad090bc3f1096011e1ba2ac676681976.jpeg",
      name = "Спортплощадка",
      address = "Полюстровский парк",
      tags = listOf(placeTags[0], placeTags[4])
    ), PlaceToVisit(
      id = "5",
      geoPoint = MyGeoPoint(59.960530, 30.400957),
      photoUrl = "https://roliki-magazin.ru/wp-content/uploads/a/d/0/ad090bc3f1096011e1ba2ac676681976.jpeg",
      name = "Спортплощадка",
      address = "Полюстровский парк",
      tags = listOf(placeTags[0], placeTags[4])
    )
  )
}