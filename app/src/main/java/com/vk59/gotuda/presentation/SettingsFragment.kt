package com.vk59.gotuda.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R.layout
import com.vk59.gotuda.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(layout.fragment_settings) {

  private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

  private val viewModel: SettingsViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }
}