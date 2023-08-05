package com.vk59.gotuda.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vk59.gotuda.R

/**
 * @param to - Visibility state view moving to
 * example: View.INVISIBLE or View.GONE
 */
fun View.fadeOut(to: Int = View.GONE) {
  isClickable = false
  animate()
    .alpha(0.0f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        visibility = to
        isClickable = true
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
  isClickable = false
  animate()
    .alpha(0.0f)
    .translationY(100f)
    .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        visibility = View.GONE
        isClickable = true
      }
    })
}

fun View.makeVisible() {
  alpha = 0.0f
  translationY = 100f
  visibility = View.VISIBLE
  animate()
    .alpha(1.0f)
    .translationY(0f)
    .setListener(object : AnimatorListenerAdapter() {})
}

inline fun FragmentManager.commitWithAnimation(
  allowStateLoss: Boolean = false,
  body: FragmentTransaction.() -> Unit
) {
  commit(allowStateLoss) {
    setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
    body.invoke(this)
  }
}