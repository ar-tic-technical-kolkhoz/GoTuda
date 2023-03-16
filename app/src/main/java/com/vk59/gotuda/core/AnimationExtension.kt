package com.vk59.gotuda.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.core.view.isVisible

fun View.makeInvisible() {
  animate()
    .alpha(0.0f)
    .translationY(100f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        isVisible = false
      }
    })
}

fun View.makeVisible() {
  alpha = 0.0f
  translationY = 100f
  isVisible = true
  isClickable = false
  animate()
    .alpha(1.0f)
    .translationY(0f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        isClickable = true
      }
    })
}