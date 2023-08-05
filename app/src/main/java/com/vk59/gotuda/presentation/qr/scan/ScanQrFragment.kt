package com.vk59.gotuda.presentation.qr.scan

import android.Manifest.permission
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R
import com.vk59.gotuda.core.commitWithAnimation
import com.vk59.gotuda.databinding.FragmentScanQrBinding
import com.vk59.gotuda.design.ErrorSnackbarFactory
import com.vk59.gotuda.permissions.PermissionsHelper
import com.vk59.gotuda.presentation.qr.UserQrFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ScanQrFragment : Fragment(R.layout.fragment_scan_qr) {

  private val binding: FragmentScanQrBinding by viewBinding(FragmentScanQrBinding::bind)

  private val viewModel: ScanQrViewModel by viewModels()

  @Inject
  lateinit var permissionsHelper: PermissionsHelper

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
      if (permissionsHelper.requestPermissionsIfNeeded(this@ScanQrFragment, permission.CAMERA)) {
        prepareCamera()
      } else {
        ErrorSnackbarFactory(binding.root).create(
          R.drawable.ic_warning,
          getString(R.string.permission_camera_not_granted)
        ).show()
      }
    }
    binding.goToQr.setOnClickListener {
      parentFragmentManager.commitWithAnimation {
        replace(R.id.fragment_container, UserQrFragment())
      }
    }
    binding.goBackButton.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
    lifecycleScope.launch {
      viewModel.qrTextFlow.collectLatest { qrText ->
        ErrorSnackbarFactory(binding.root).create(
          R.drawable.ic_warning,
          "QR found: $qrText"
        ).show()
      }
    }
  }

  private fun prepareCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    val cameraExecutor = ContextCompat.getMainExecutor(requireContext())
    cameraProviderFuture.addListener({
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

      val preview = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(binding.camera.surfaceProvider)
        }

      val imageAnalyzer = ImageAnalysis.Builder()
        .build()
        .also {
          it.setAnalyzer(cameraExecutor, QrImageAnalyzer({ bitmap ->
            binding.bitmapPreview.setImageBitmap(bitmap)
          }) { qr ->
            viewModel.qrDetected(qr)
          })
        }

      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
      } catch (exc: Exception) {
        Timber.e("ScanQR", "Use case binding failed", exc)
      }

    }, cameraExecutor)
  }
}