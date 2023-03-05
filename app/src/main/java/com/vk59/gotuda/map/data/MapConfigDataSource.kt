package com.vk59.gotuda.map.data

import androidx.fragment.app.Fragment
import com.vk59.gotuda.map.MapViewDelegate
import com.vk59.gotuda.map.mapkit.YandexMapViewDelegate
import com.vk59.gotuda.map.osm.OsmMapViewDelegate

object MapConfigDataSource {

  fun obtainMapViewDelegates(fragment: Fragment): List<MapViewDelegate> {
    return buildList {
      add(OsmMapViewDelegate(fragment))
      add(YandexMapViewDelegate(fragment))
    }
  }
}