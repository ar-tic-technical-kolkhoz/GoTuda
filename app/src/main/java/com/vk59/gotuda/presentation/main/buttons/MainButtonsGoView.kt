package com.vk59.gotuda.presentation.main.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.vk59.gotuda.R
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.core.dimen
import com.vk59.gotuda.core.dpToPx
import com.vk59.gotuda.core.fadeIn
import com.vk59.gotuda.core.fadeOut
import com.vk59.gotuda.databinding.GoViewMainButtonsBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
class MainButtonsGoView @AssistedInject constructor(
  @ActivityContext
  context: Context,
  @Assisted
  private val viewModel: MainButtonsViewModel
) : ConstraintLayout(context) {

  init {
    setPadding(
      dimen(R.dimen.margin_border),
      dimen(R.dimen.margin_border),
      dimen(R.dimen.margin_border),
      dpToPx(40),
    )
  }

  private val binding = GoViewMainButtonsBinding.inflate(LayoutInflater.from(context), this)

  private val scope = CoroutineScope(AppDispatcher.main() + CoroutineName(javaClass.name))

  private val handler: Handler = Handler(Looper.getMainLooper())

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    binding.apply {
      settingsButton.setIconResource(R.drawable.ic_settings)
      geoButton.setIconResource(R.drawable.ic_geo_arrow)
      qrButton.setIconResource(R.drawable.ic_qr)
      settingsButton.setOnClickListener {
        viewModel.settingsClicked()
      }
      geoButton.setOnClickListener {
        viewModel.followToUserLocation()
        viewModel.moveToUserGeo()
      }
    }
    scope.launch {
      viewModel.listenToLocationButton().collectLatest {
        if (it) {
          postDelayed { binding.geoButton.fadeOut(to = View.INVISIBLE) }
        } else {

          postDelayed { binding.geoButton.fadeIn() }
        }
      }
    }
  }

  private fun postDelayed(r: Runnable) {
    handler.postDelayed(r, 300L)
  }
}