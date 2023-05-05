package com.vk59.gotuda.data

import com.vk59.gotuda.data.model.Recommendation

interface RecommendationRepository {

  suspend fun getRecommendation(): Recommendation

  suspend fun accepted(recommendationId: String)

  suspend fun showLater(recommendationId: String)

  suspend fun decline(recommendationId: String)
}