package com.vk59.gotuda.data

import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.Mocks.placeMapList
import com.vk59.gotuda.data.Mocks.places
import com.vk59.gotuda.data.model.Recommendation
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.random.nextInt

interface RecommendationRepository {

  suspend fun getRecommendation(): Recommendation

  suspend fun accepted(recommendationId: String)

  suspend fun showLater(recommendationId: String)

  suspend fun decline(recommendationId: String)
}

class RecommendationRepositoryMock : RecommendationRepository {

  override suspend fun getRecommendation(): Recommendation {
    return withContext(AppDispatcher.io()) {
      delay(2000L)
      Recommendation("${Random.nextLong(100000L)}", places[Random.nextInt(placeMapList.indices)])
    }
  }

  override suspend fun accepted(recommendationId: String) {
    withContext(AppDispatcher.io()) {
      delay(1000L)
    }
  }

  override suspend fun showLater(recommendationId: String) {
    withContext(AppDispatcher.io()) {
      delay(1000L)
    }
  }

  override suspend fun decline(recommendationId: String) {
    withContext(AppDispatcher.io()) {
      delay(1000L)
    }
  }
}