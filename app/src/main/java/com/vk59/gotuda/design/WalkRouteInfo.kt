package com.vk59.gotuda.design

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.vk59.gotuda.R
import com.vk59.gotuda.core.colorAttr
import com.vk59.gotuda.core.dpToPx


class WalkRouteInfoView(context: Context) : AppCompatTextView(context) {
  init {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.component_text_size_caption))
    setBackgroundResource(R.drawable.bg_walking_route_info)
    setPaddingRelative(
      dpToPx(8),
      dpToPx(4),
      dpToPx(8),
      dpToPx(4)
    )
    setTextColor(colorAttr(R.attr.textMain))
    val lp: LayoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    layoutParams = lp
  }

  fun asBitmap(): Bitmap {
    layout(this)
    return renderView(this)
  }

  private fun layout(view: View) {
    view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
  }

  private fun renderView(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, ARGB_8888)
    view.draw(Canvas(bitmap))
    return bitmap
  }
}
