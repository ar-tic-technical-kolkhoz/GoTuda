package com.vk59.gotuda.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.FragmentOnboardingBinding

class OnboardingFragment() : Fragment(R.layout.fragment_onboarding) {

  private val binding: FragmentOnboardingBinding by viewBinding(FragmentOnboardingBinding::bind)

  private val viewModel: OnboardingViewModel by viewModels()
}