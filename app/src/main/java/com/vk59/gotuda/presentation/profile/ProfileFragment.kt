package com.vk59.gotuda.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

  private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val request = Glide.with(requireContext()).load("https://crypto.ru/wp-content/plugins/q-auth/assets/img/default-user.png")
    request.into(binding.userPhoto)

    binding.goBackButton.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
  }
}