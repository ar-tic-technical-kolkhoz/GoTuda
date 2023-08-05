package com.vk59.gotuda.presentation.qr.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import timber.log.Timber
import java.io.ByteArrayOutputStream

class QrImageAnalyzer(
  private val onDoneBitmap: (Bitmap) -> Unit,
  private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

  private val formatReader = MultiFormatReader()

  private fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val vuBuffer = planes[2].buffer // VU

    val ySize = yBuffer.remaining()
    val vuSize = vuBuffer.remaining()

    val nv21 = ByteArray(ySize + vuSize)

    yBuffer.get(nv21, 0, ySize)
    vuBuffer.get(nv21, ySize, vuSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
  }

  override fun analyze(image: ImageProxy) {
    try {
      val bitmap = image.toBitmap()
      val intArray = IntArray(bitmap.width * bitmap.height)
      bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

      val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)

      val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
      onDoneBitmap.invoke(bitmap)
      val result = formatReader.decodeWithState(binaryBitmap)
      onQrDetected.invoke(result.text)
    } catch (t: Throwable) {
      Timber.tag(javaClass.name).d(t)
    } finally {
      image.close()
    }
  }
}