package com.vk59.gotuda

import android.Manifest.permission
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.map.mapkit.MainUserLocationObjectListener
import com.vk59.gotuda.map.model.GoGeoPoint
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapType.MAP
import com.yandex.mapkit.user_location.UserLocationLayer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainFragment : Fragment(R.layout.fragment_main) {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  private var userLocationLayer: UserLocationLayer? = null

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
    initOsm()
    initMapKit()
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
        .collect { onLocationChanged(it) }
    }
  }

  private fun initMapKit() {
    binding.mapKit.map.mapType = MAP
    if (requireContext().resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
      binding.mapKit.map.isNightModeEnabled = true
    }

    val mapKit = MapKitFactory.getInstance()
    mapKit.resetLocationManagerToDefault()
    userLocationLayer = mapKit.createUserLocationLayer(binding.mapKit.mapWindow)
    userLocationLayer?.isVisible = true
    userLocationLayer?.isHeadingEnabled = true

    userLocationLayer?.setObjectListener(MainUserLocationObjectListener())
  }

  private fun initOsm() {
    Configuration.getInstance().load(
      requireContext().applicationContext,
      PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
    )
    val map = binding.mapView
    map.setTileSource(MAPNIK)
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.mapView)
    locationOverlay.setPersonIcon(getDrawable(requireContext(), R.drawable.user_geo)?.toBitmap())
    locationOverlay.setDirectionIcon(getDrawable(requireContext(), R.drawable.user_geo)?.toBitmap())
    locationOverlay.enableMyLocation()
    binding.mapView.overlays.add(locationOverlay)
  }

  private fun onLocationChanged(location: GoGeoPoint) {
    // 1. OSM
    val geoPoint = GeoPoint(location.latitude, location.longitude)
    binding.mapView.controller.animateTo(geoPoint, 18.0, 1000)

    binding.mapKit.map.move(
      CameraPosition(
        Point(location.latitude, location.longitude),
        18f,
        0f,
        0f
      )
    )

  }

  companion object {

    private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private const val TAG = "MainFragment"
  }
}