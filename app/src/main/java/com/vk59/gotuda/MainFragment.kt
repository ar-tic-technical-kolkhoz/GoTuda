package com.vk59.gotuda

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.di.SimpleDi.multipleMapDelegate
import com.vk59.gotuda.map.MultipleMapDelegate
import com.vk59.gotuda.map.mapkit.YandexMapViewDelegate
import com.vk59.gotuda.map.osm.OsmMapViewDelegate
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import com.vk59.gotuda.presentation.SettingsFragment


class MainFragment : Fragment(R.layout.fragment_main) {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  private var mapDelegate: MultipleMapDelegate? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initMap()
    launchDebugBottomButtons()
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

  private fun launchDebugBottomButtons() {
    lifecycleScope.launchWhenResumed {
      viewModel.debugButtonsShown.collectLatest {
        binding.buttonList.isVisible = it
      }
    }
    viewModel.listenToButtons().observe(viewLifecycleOwner) { buttons ->
      binding.buttonList.addButtons(buttons)
    }
    configureBottomControls()
  }

  private fun configureBottomControls() {
    binding.mainBottomButtons.settingsButton.setIconResource(R.drawable.ic_settings)
    binding.mainBottomButtons.settingsButton.setOnClickListener {
      launchSettings()
    }
    binding.mainBottomButtons.geoButton.setIconResource(R.drawable.ic_geo_arrow)
    binding.mainBottomButtons.goTudaButton.setTitle("Go")
    binding.mainBottomButtons.goTudaButton.setTitleSizeSp(30f)
  }

  private fun launchSettings() {
    parentFragmentManager.beginTransaction()
      .add(SettingsFragment(), "settings")
      .addToBackStack("main")
      .commit()
  }

  private fun initMap() {
    initAllMaps()
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

  @RequiresApi(VERSION_CODES.P)
  private fun initLocationManager(locationManager: LocationManager) {
    if (locationManager.isLocationEnabled) {
      initLocationManagerLessP(locationManager)
    }
  }

  private fun initLocationManagerLessP(manager: LocationManager) {
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        requireContext(),
        permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      registerForActivityResult(
        RequestMultiplePermissions(),
      ) { isGranted ->
        if (isGranted.values.all { it }) {
          launchObserveGeoUpdates(manager)
        } else {
          Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_LONG).show()
        }
      }.launch(arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION))
    } else {
      launchObserveGeoUpdates(manager)
    }
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun launchObserveGeoUpdates(manager: LocationManager) {
    lifecycleScope.launch {
      viewModel.listenToUserGeo(manager).collect {
        mapDelegate?.moveToUserLocation(it)
      }
    }
  }

  private fun initAllMaps() {
    val locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java) ?: return

    requireMapDelegate().delegates.forEach {
      when (it) {
        is YandexMapViewDelegate -> {
          val mapKit = MapKitFactory.getInstance()
          mapKit.resetLocationManagerToDefault()
          it.initMapView(binding.mapKit)
        }
        is OsmMapViewDelegate -> {
          it.initMapView(binding.mapView)
        }
      }
    }
    if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
      initLocationManager(locationManager)
    } else {
      initLocationManagerLessP(locationManager)
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