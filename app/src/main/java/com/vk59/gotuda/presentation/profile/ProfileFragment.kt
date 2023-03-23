package com.vk59.gotuda.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

  private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

  private val viewModel: ProfileViewModel by viewModels()

  private val adapter = ProfileAdapter({/* TODO: Edit screen */ }, { /* TODO: Make navigation */ })

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.goBackButton.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
    binding.content.adapter = adapter
    binding.content.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

    viewLifecycleOwner.lifecycleScope.launchWhenResumed {

      viewModel.listenToListItems().collect {
        adapter.submitList(it)
      }
    }
  }
}