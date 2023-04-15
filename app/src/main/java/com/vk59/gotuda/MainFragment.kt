package com.vk59.gotuda

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.bumptech.glide.Glide
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.core.fadeIn
import com.vk59.gotuda.core.fadeOut
import com.vk59.gotuda.core.makeGone
import com.vk59.gotuda.core.makeVisible
import com.vk59.gotuda.data.Mocks.DEFAULT_PHOTO_URL
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.di.SimpleDi
import com.vk59.gotuda.di.SimpleDi.multipleMapDelegate
import com.vk59.gotuda.map.MultipleMapDelegate
import com.vk59.gotuda.map.mapkit.YandexMapViewDelegate
import com.vk59.gotuda.map.model.MapNotAttachedToWindowException
import com.vk59.gotuda.map.osm.OsmMapViewDelegate
import com.vk59.gotuda.presentation.profile.ProfileFragment
import com.vk59.gotuda.presentation.settings.SettingsFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.CameraUpdateReason.GESTURES
import com.yandex.mapkit.map.Map
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.config.Configuration
import timber.log.Timber
import java.util.LinkedList


class MainFragment : Fragment(R.layout.fragment_main), CameraListener {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val backStack = LinkedList<View>()

  private val viewModel: MainViewModel by viewModels()

  private var currentModalView: View? = null

  private var mapDelegate: MultipleMapDelegate? = null

  private var followToUserLocation: Boolean = true

  private val handler: Handler = SimpleDi.handler

  @SuppressLint("ClickableViewAccessibility")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Glide.with(requireContext()).load(DEFAULT_PHOTO_URL)
      .into(binding.userPhoto)
    currentModalView = binding.mainBottomButtons.root
    binding.userPhoto.setOnClickListener {
      launchPassport()
    }
    launchMainButtons()
    launchDebugBottomButtons()
    val callback: OnBackPressedCallback =
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = onBackPressed()
      }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      callback
    )
  }

  private fun launchPassport() {
    parentFragmentManager.beginTransaction()
      .replace(R.id.fragment_container, ProfileFragment())
      .addToBackStack("main")
      .commit()
  }

  private fun launchSettings() {
    parentFragmentManager.beginTransaction()
      .replace(R.id.fragment_container, SettingsFragment())
      .addToBackStack("main")
      .commit()
  }

  override fun onStart() {
    MapKitFactory.getInstance().onStart()
    binding.mapKit.onStart()
    super.onStart()
    binding.mapKit.map.addCameraListener(this)
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
    initMap()

    viewModel.getPlaces()
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      viewModel.listenToMapObjects().collectLatest {
        it.forEach { place ->
          mapDelegate?.addPlacemark(place.geoPoint, R.drawable.ic_place)
        }
      }
    }
    val locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java) ?: return
    if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
      initLocationManager(locationManager)
    } else {
      initLocationManagerLessP(locationManager)
    }
  }

  override fun onCameraPositionChanged(map: Map, pos: CameraPosition, reason: CameraUpdateReason, finished: Boolean) {
    if (reason == GESTURES) {
      followToUserLocation = false
      val geoButton = binding.mainBottomButtons.geoButton
      if (geoButton.visibility != View.VISIBLE) {
        binding.mainBottomButtons.geoButton.fadeIn()
      }
    }
  }

  override fun onPause() {
    super.onPause()
    Configuration.getInstance().save(
      getApplicationContext(),
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
    )
    binding.mapView.onPause()
  }

  fun onBackPressed() {
    if (backStack.isNotEmpty()) {
      currentModalView?.makeGone()
      val newView = backStack.pop()
      newView.makeVisible()
      currentModalView = newView
    } else {
      requireActivity().finish()
    }
  }

  override fun onStop() {
    binding.mapKit.onStop()
    MapKitFactory.getInstance().onStop()
    mapDelegate?.detach()
    mapDelegate = null
    binding.mapKit.map.addCameraListener(this)
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
  }

  private fun launchMainButtons() {
    binding.mainBottomButtons.settingsButton.setIconResource(R.drawable.ic_settings)
    binding.mainBottomButtons.settingsButton.setOnClickListener {
      launchSettings()
    }
    binding.mainBottomButtons.geoButton.setIconResource(R.drawable.ic_geo_arrow)
    binding.mainBottomButtons.geoButton.setOnClickListener {
      viewModel.moveToUserGeo()
      handler.postDelayed(
        {
          binding.mainBottomButtons.geoButton.fadeOut(to = View.INVISIBLE)
        },
        500L
      )
    }
    binding.mainBottomButtons.goTudaButton.setTitle("Go")
    binding.mainBottomButtons.goTudaButton.setTitleSizeSp(30f)
    binding.mainBottomButtons.goTudaButton.setOnClickListener {
      launchCardRecommendations()
    }
  }

  private fun launchCardRecommendations() {
    currentModalView?.makeGone()
    backStack.add(binding.mainBottomButtons.root)

    binding.cardsView.makeVisible()
    currentModalView = binding.cardsView
    binding.cardsView.setOnBackButtonClickListener { onBackPressed() }
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
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      viewModel.listenToUserGeo(manager).collect {
        if (followToUserLocation) {
          try {
            requireMapDelegate().moveToUserLocation(it)
            requireMapDelegate().showUserLocation(it)
          } catch (e: java.lang.IllegalStateException) {
            Timber.d(e)
          } catch (mapNotAttached: MapNotAttachedToWindowException) {
            Timber.d(mapNotAttached)
          }
        }
      }
    }
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      viewModel.listenToMove().collect {
        followToUserLocation = true
        requireMapDelegate().moveToUserLocation(it.goGeoPoint)
      }
    }
  }

  private fun initAllMaps() {
    requireMapDelegate().delegates.forEach {
      when (it) {
        is YandexMapViewDelegate -> {
          val mapKit = MapKitFactory.getInstance()
          mapKit.resetLocationManagerToDefault()
          it.attach(binding.mapKit)
        }
        is OsmMapViewDelegate -> {
          it.attach(binding.mapView)
        }
      }
    }
  }

  private fun requireMapDelegate(): MultipleMapDelegate {
    return mapDelegate ?: multipleMapDelegate(this).also { mapDelegate = it }
  }

  companion object {

    private const val TAG = "MainFragment"
  }
}