package com.vk59.gotuda.presentation.settings

import android.os.Bundle
import android.view.View
import android.view.View.generateViewId
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R.layout
import com.vk59.gotuda.databinding.FragmentSettingsBinding
import com.vk59.gotuda.design.ChipComponent
import kotlinx.coroutines.flow.collectLatest

class SettingsFragment : Fragment(layout.fragment_settings) {

  private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

  private val viewModel: SettingsViewModel by viewModels()

  // TODO: Make using recycler.
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.searchRadius.setTitle("Радиус поиска")
    binding.searchRadius.setCompanionText("1 км")
    binding.everywhereButton.setTitle("Куда угодно")
    binding.everywhereButton.setSubtitle("Мы сами подберем для вас места для посещения")
    lifecycleScope.launchWhenCreated {
      viewModel.chipsState.collectLatest {
        binding.tagsList.showChips(it)
      }
    }
    binding.applyButton.setTitle("Применить")
    binding.applyButton.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
  }
}
