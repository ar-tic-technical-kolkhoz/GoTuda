package com.vk59.gotuda

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vk59.gotuda.core.commitWithAnimation
import com.vk59.gotuda.presentation.main.MainFragment
import com.vk59.gotuda.presentation.onboarding.OnboardingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val viewModel: MainActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportFragmentManager.commitWithAnimation {
      replace(R.id.fragment_container, OnboardingFragment(), "onboarding")
    }
    lifecycleScope.launch {
      viewModel.isAuthorized()
        .collectLatest { authorized ->
          if (authorized) {
            supportFragmentManager.commitWithAnimation {
              replace(R.id.fragment_container, MainFragment(), "start")
            }
          }
        }
    }
    setContentView(R.layout.activity_main)
  }
}