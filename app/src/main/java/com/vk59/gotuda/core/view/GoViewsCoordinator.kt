package com.vk59.gotuda.core.view

import android.view.View
import android.view.ViewGroup
import com.vk59.gotuda.core.makeGone
import com.vk59.gotuda.core.makeVisible
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoViewsCoordinator @Inject constructor() {

  private val shownView = mutableMapOf<View, ViewGroup>()

  fun show(view: View, parent: ViewGroup) {
    view.apply {
      makeVisible()
    }
    parent.addView(view.apply {
      bringToFront()
    })
  }

  fun hide(view: View) {
    shownView.filter { view == it.key }.forEach { deletingView, parent ->
      deletingView.makeGone()
      parent.removeView(deletingView)
    }
  }
}