package com.vk59.gotuda.presentation.qr.reward

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R
import com.vk59.gotuda.core.commitWithAnimation
import com.vk59.gotuda.databinding.FragmentScanQrRewardBinding
import com.vk59.gotuda.presentation.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanQrRewardFragment : Fragment(R.layout.fragment_scan_qr_reward) {

  private val binding: FragmentScanQrRewardBinding by viewBinding(FragmentScanQrRewardBinding::bind)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.rewardValue.text = "+${arguments?.getString("reward")}"

    binding.signInButton.setOnClickListener{
      parentFragmentManager.commitWithAnimation {
        replace(R.id.fragment_container, MainFragment())
      }
    }
  }
}