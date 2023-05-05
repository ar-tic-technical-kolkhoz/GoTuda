package com.vk59.gotuda.core

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.yandex.runtime.image.ImageProvider

fun fromDrawable(context: Context, @DrawableRes drawableInt: Int): ImageProvider {
  return ImageProvider.fromBitmap(AppCompatResources.getDrawable(context, drawableInt)?.toBitmap())
}