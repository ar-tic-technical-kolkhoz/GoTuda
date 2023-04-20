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
import com.vk59.gotuda.MainFragmentState.ErrorState
import com.vk59.gotuda.MainFragmentState.FinishActivity
import com.vk59.gotuda.MainFragmentState.LaunchPlace
import com.vk59.gotuda.MainFragmentState.Main
import com.vk59.gotuda.MainFragmentState.MainButtonLoading
import com.vk59.gotuda.MapViewType.MAPKIT
import com.vk59.gotuda.MapViewType.OSM
import com.vk59.gotuda.core.commitWithAnimation
import com.vk59.gotuda.core.fadeIn
import com.vk59.gotuda.core.fadeOut
import com.vk59.gotuda.core.makeGone
import com.vk59.gotuda.core.makeVisible
import com.vk59.gotuda.data.Mocks.DEFAULT_PHOTO_URL
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.di.SimpleDi
import com.vk59.gotuda.di.SimpleDi.mapController
import com.vk59.gotuda.map.MapController
import com.vk59.gotuda.map.actions.MapAction
import com.vk59.gotuda.map.actions.MapAction.SinglePlaceTap
import com.vk59.gotuda.map.actions.MapActionsListener
import com.vk59.gotuda.map.model.MapNotAttachedToWindowException
import com.vk59.gotuda.map.model.MyGeoPoint
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

// TODO: Make it initializing Fragment.
/**
 * The MainFragment must initialize all the required components only
 */
class MainFragment : Fragment(R.layout.fragment_main), CameraListener, MapActionsListener {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  private var currentModalView: View? = null

  private var mapDelegate: MapController? = null

  private var followToUserLocation: Boolean = true

  private val handler: Handler = SimpleDi.handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java) ?: return
    if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
      initLocationManager(locationManager)
    } else {
      initLocationManagerLessP(locationManager)
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Glide.with(requireContext()).load(DEFAULT_PHOTO_URL).into(binding.userPhoto)
    currentModalView = binding.mainBottomButtons.root
    binding.userPhoto.setOnClickListener {
      launchPassport()
    }
    launchMainButtons()
    launchDebugBottomButtons()
    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() = viewModel.backPressed()
    }
    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner, callback
    )
  }

  private fun launchPassport() {
    parentFragmentManager.commitWithAnimation {
      replace(R.id.fragment_container, ProfileFragment())
      addToBackStack("main")
    }
  }

  private fun launchSettings() {
    parentFragmentManager.commitWithAnimation {
      replace(R.id.fragment_container, SettingsFragment())
      addToBackStack("main")
    }
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
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      // TODO: Bad solution, fix it!
      initMap(viewModel.obtainInitialLocation())
      viewModel.getPlaces()
      viewModel.listenToMapObjects().collectLatest { list ->
        list.forEach { place ->
          val icon = if (place.selected) R.drawable.ic_place_selected else R.drawable.ic_place
          mapDelegate?.addPlacemark(place.id, place.geoPoint, icon)
        }
      }
    }
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      viewModel.listenToFragmentState().collectLatest { state ->
        when (state) {
          Main -> launchMainButtons()
          MainButtonLoading -> launchMainButtons(loading = true)
          is LaunchPlace -> launchCardRecommendations(state.place)
          is ErrorState -> { /* TODO*/
          }
          FinishActivity -> {
            requireActivity().finish()
          }
        }
      }
    }
  }

  override fun handleMapAction(action: MapAction) {
    action as SinglePlaceTap
    viewModel.placeTapped(action.mapObjectId)
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
      getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
    )
    binding.mapView.onPause()
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

  private fun launchMainButtons(loading: Boolean = false) {
    currentModalView?.makeGone()

    binding.mainBottomButtons.apply {

      root.makeVisible()
      currentModalView = binding.mainBottomButtons.root
      settingsButton.isClickable = !loading
      geoButton.isClickable = !loading
      settingsButton.setIconResource(R.drawable.ic_settings)
      geoButton.setIconResource(R.drawable.ic_geo_arrow)

      if (loading) {
        goTudaButton.setOnClickListener {}
        settingsButton.setOnClickListener { }
        geoButton.setOnClickListener {}
        goTudaButton.setProgressing(true)
      } else {
        goTudaButton.setTitle("Go")
        goTudaButton.setTitleSizeSp(30f)
        goTudaButton.setOnClickListener {
          viewModel.requestRecommendations()
        }
        goTudaButton.setProgressing(false)
        settingsButton.setOnClickListener {
          launchSettings()
        }
        geoButton.setOnClickListener {
          viewModel.moveToUserGeo()
          handler.postDelayed(
            {
              geoButton.fadeOut(to = View.INVISIBLE)
            }, 500L
          )
        }

      }
    }
  }

  private fun launchCardRecommendations(place: PlaceToVisit) {
    currentModalView?.makeGone()

    binding.cardsView.makeVisible()
    binding.cardsView.bindPlace(place)
    currentModalView = binding.cardsView
    binding.cardsView.setOnBackButtonClickListener {
      viewModel.deselectObject()
      viewModel.backPressed()
    }
  }

  private fun initMap(initialGeoPoint: MyGeoPoint?) {
    requireMapDelegate().attachViews(this, listOf(binding.mapKit, binding.mapView), initialGeoPoint, this)
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
        requireContext(), permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        requireContext(), permission.ACCESS_COARSE_LOCATION
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
        requireMapDelegate().moveToUserLocation(it.geoPoint)
      }
    }
  }

  private fun requireMapDelegate(): MapController {
    return mapDelegate ?: mapController.also { mapDelegate = it }
  }

  companion object {

    private const val TAG = "MainFragment"
  }
}