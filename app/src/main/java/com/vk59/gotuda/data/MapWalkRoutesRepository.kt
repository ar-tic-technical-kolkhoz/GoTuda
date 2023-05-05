package com.vk59.gotuda.data

import com.vk59.gotuda.map.model.MyGeoPoint
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType.WAYPOINT
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class MapWalkRoutesRepository @Inject constructor(
  private val router: PedestrianRouter
) {

  suspend fun getRoutes(point1: MyGeoPoint, point2: MyGeoPoint): List<Route> {
    return suspendCancellableCoroutine {
      router.requestRoutes(
        listOf(point1.toRequestPoint(), point2.toRequestPoint()),
        TimeOptions(),
        object : Session.RouteListener {
          override fun onMasstransitRoutes(routes: MutableList<Route>) {
            it.resumeWith(Result.success(routes))
          }

          override fun onMasstransitRoutesError(error: Error) {
            it.resumeWith(Result.failure(error as? Throwable? ?: Throwable(error.toString())))
          }
        }
      )
    }
  }

  private fun MyGeoPoint.toRequestPoint(): RequestPoint {
    return RequestPoint(Point(latitude, longitude), WAYPOINT, null)
  }
}