package com.vk59.gotuda.core.view

import android.view.View
import android.view.ViewGroup
import com.vk59.gotuda.core.makeGone
import com.vk59.gotuda.core.makeVisible
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoViewsCoordinator @Inject constructor() {

  private val shownViews = mutableMapOf<View, ViewGroup>()

  fun show(view: View, parent: ViewGroup) {
    view.apply {
      makeVisible()
    }
    parent.addView(view.apply {
      bringToFront()
    })
    shownViews[view] = parent
  }

  fun hide(view: View) {
    val viewsToRemove = shownViews.filter { view == it.key }
    viewsToRemove.forEach { deletingView, parent ->
      deletingView.makeGone()
      parent.removeView(deletingView)
    }
    shownViews.remove(view)
  }

  fun hideAll() {
    shownViews.values.forEach { parent ->
      parent.removeAllViews()
    }
    shownViews.clear()
  }
}