package com.vk59.gotuda.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.vk59.gotuda.core.coroutines.AppDispatcher
import com.vk59.gotuda.data.PermissionsRepository
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PermissionsHelper @Inject constructor(
  @ActivityContext
  private val context: Context,
  private val permissionsRepository: PermissionsRepository,
) {

  /**
   * @return true if permissions requested and granted, or already granted
   */
  suspend fun requestPermissionsIfNeeded(fragment: Fragment, vararg permissions: String): Boolean {
    return withContext(AppDispatcher.main()) {
      val list = permissions.toList()

      val permissionsGranted =
        list.all { ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
      return@withContext if (!permissionsGranted) {
        suspendCancellableCoroutine<Boolean> {
          fragment.registerForActivityResult(
            RequestMultiplePermissions(),
          ) { isGranted ->
            val allGranted = isGranted.values.all { it }
            permissionsRepository.permissionGranted(isGranted.entries.filter { it.value }.map { it.key })
            it.resumeWith(Result.success(allGranted))
          }.launch(list.toTypedArray())
        }
      } else {
        permissionsRepository.permissionGranted(list)
        true
      }
    }
  }
}