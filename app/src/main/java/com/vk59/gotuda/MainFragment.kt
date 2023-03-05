package com.vk59.gotuda

import android.Manifest.permission
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.di.SimpleDi.multipleMapDelegate
import com.vk59.gotuda.map.MapViewDelegate
import com.vk59.gotuda.map.MultipleMapDelegate
import com.vk59.gotuda.map.mapkit.YandexMapViewDelegate
import com.vk59.gotuda.map.osm.OsmMapViewDelegate
import com.yandex.mapkit.MapKitFactory
import org.osmdroid.config.Configuration


class MainFragment : Fragment(R.layout.fragment_main) {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  private var mapDelegate: MultipleMapDelegate? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initBottomButtons()
    initMap()
  }

  override fun onStart() {
    MapKitFactory.getInstance().onStart()
    binding.mapKit.onStart()
    super.onStart()
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    Configuration.getInstance().save(
      getApplicationContext(),
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
    )
    binding.mapView.onPause()
  }

  override fun onStop() {
    binding.mapKit.onStop()
    MapKitFactory.getInstance().onStop()
    super.onStop()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.mapView.onDetach()
  }

  private fun getApplicationContext(): Context {
    return requireContext().applicationContext
  }

  private fun initBottomButtons() {
    viewModel.listenToButtons().observe(viewLifecycleOwner) { buttons ->
      binding.buttonList.addButtons(buttons)
    }
  }

  private fun initMap() {
    initAllMaps()
    viewModel.listenToRequestRequirements().observe(viewLifecycleOwner) { onGranted ->
      registerForActivityResult(
        RequestMultiplePermissions(),
      ) { isGranted ->
        if (isGranted.values.any { !it }) {
          Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_LONG).show()
        } else {
          onGranted.invoke()
        }
      }.launch(arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION))
    }
    lifecycleScope.launchWhenResumed {
      viewModel.listenToUserGeo()
        .collect { requireMapDelegate().moveToUserLocation(it) }
    }
    viewModel.mapViewType.observe(viewLifecycleOwner) { mapType ->
      when (mapType) {
        OSM -> {
          binding.mapKit.isVisible = false
          binding.mapView.isVisible = true
        }
        MAPKIT -> {
          binding.mapKit.isVisible = true
          binding.mapView.isVisible = false
        }
        else -> { /* Nothing to do */
        }
      }
    }
  }

  private fun initAllMaps() {
    requireMapDelegate().delegates.forEach {
      when (it) {
        is YandexMapViewDelegate -> {
          it.initMapView(binding.mapKit)
        }
        is OsmMapViewDelegate -> {
          it.initMapView(binding.mapView)
        }
      }
    }
    requireMapDelegate().showUserLocation()
  }

  private fun requireMapDelegate(): MultipleMapDelegate {
    return mapDelegate ?: multipleMapDelegate(this).also { mapDelegate = it }
  }

  companion object {

    private const val TAG = "MainFragment"
  }
}