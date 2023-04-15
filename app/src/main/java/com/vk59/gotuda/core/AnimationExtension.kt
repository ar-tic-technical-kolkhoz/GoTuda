package com.vk59.gotuda.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

/**
 * @param to - Visibility state view moving to
 * example: View.INVISIBLE or View.GONE
 */
fun View.fadeOut(to: Int = View.GONE) {
  animate()
    .alpha(0.0f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        visibility = to
      }
    })
}

fun View.fadeIn() {
  alpha = 0.0f
  visibility = View.VISIBLE
  isClickable = false
  animate()
    .alpha(1.0f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        isClickable = true
      }
    })
}

fun View.makeGone() {
  animate()
    .alpha(0.0f)
    .translationY(100f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        visibility = View.GONE
      }
    })
}

fun View.makeVisible() {
  alpha = 0.0f
  translationY = 100f
  visibility = View.VISIBLE
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