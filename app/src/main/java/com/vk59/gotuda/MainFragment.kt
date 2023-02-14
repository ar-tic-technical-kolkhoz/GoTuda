package com.vk59.gotuda

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.button_list.ButtonUiModel
import com.vk59.gotuda.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainFragment : Fragment(R.layout.fragment_main) {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    showBottomButtons()
    Configuration.getInstance().load(
      requireContext().applicationContext,
      PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
    );
    val map = binding.mapView
    map.setTileSource(MAPNIK)
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.mapView);
    locationOverlay.enableMyLocation()
    binding.mapView.overlays.add(locationOverlay)
    val locationManager: LocationManager? = getSystemService(requireContext(), LocationManager::class.java)
    val compassOverlay = CompassOverlay(context, InternalCompassOrientationProvider(context), map)
    compassOverlay.enableCompass()
    map.overlays.add(compassOverlay)
    locationManager?.let {
      if (VERSION.SDK_INT >= VERSION_CODES.P) {
        initLocationManager(it)
      } else {
        initLocationManagerLessP(it)
      }
    }
  }

  private fun getApplicationContext(): Context {
    return requireContext().applicationContext
  }

  private fun showBottomButtons() {
    binding.buttonList.addButtons(buttons)
  }

  @RequiresApi(VERSION_CODES.P)
  private fun initLocationManager(manager: LocationManager) {
    if (manager.isLocationEnabled) {
      initLocationManagerLessP(manager)
    }
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
    );
    binding.mapView.onPause();
  }

  override fun onDestroy() {
    super.onDestroy()
    binding.mapView.onDetach()
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
          requestGeoUpdates(manager)
        } else {
          Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_LONG).show()
        }
      }.launch(arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION))
    } else {
      requestGeoUpdates(manager)
    }
  }

  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
  private fun requestGeoUpdates(manager: LocationManager) {
    manager.requestLocationUpdates(GPS_PROVIDER, 5000, 10f, onLocationChanged(binding.mapView))
  }

  companion object {
    // TODO: Move to ViewModel
    private val buttons = listOf(
      ButtonUiModel("start", "Start"),
      ButtonUiModel("finish", "Finish"),
      ButtonUiModel("mood", "Mood")
    )

    private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private fun onLocationChanged(mapView: MapView) = LocationListener { location ->
      val geoPoint = GeoPoint(location.latitude, location.longitude)
      mapView.controller.animateTo(geoPoint, 18.0, 1000)
    }
  }
}