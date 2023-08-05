package com.vk59.gotuda.presentation.qr

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.zxing.BarcodeFormat.QR_CODE
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.vk59.gotuda.R
import com.vk59.gotuda.databinding.FragmentUserQrBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserQrFragment : Fragment(R.layout.fragment_user_qr) {

  private val binding: FragmentUserQrBinding by viewBinding(FragmentUserQrBinding::bind)

  private val viewModel: UserQrViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
      viewModel.qrUrlFlow().collectLatest {
        val writer = QRCodeWriter()
        try {
          val bitMatrix = writer.encode(it, QR_CODE, 512, 512)
          val width = bitMatrix.width
          val height = bitMatrix.height
          val bmp = Bitmap.createBitmap(width, height, RGB_565)
          for (x in 0 until width) {
            for (y in 0 until height) {
              bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
          }
          binding.qr.setImageBitmap(bmp)
          binding.progressIndicator.isVisible = false
        } catch (e: WriterException) {
          e.printStackTrace()
        }
      }
    }

    binding.goBackButton.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
  }
}