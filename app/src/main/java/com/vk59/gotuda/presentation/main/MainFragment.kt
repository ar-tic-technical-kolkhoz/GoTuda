package com.vk59.gotuda.presentation.main

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vk59.gotuda.R
import com.vk59.gotuda.core.commitWithAnimation
import com.vk59.gotuda.core.makeGone
import com.vk59.gotuda.core.makeVisible
import com.vk59.gotuda.core.view.GoViewsCoordinator
import com.vk59.gotuda.data.PermissionsRepository
import com.vk59.gotuda.data.model.PlaceToVisit
import com.vk59.gotuda.databinding.FragmentMainBinding
import com.vk59.gotuda.design.ErrorSnackbarFactory
import com.vk59.gotuda.map.MapController
import com.vk59.gotuda.map.actions.MapAction
import com.vk59.gotuda.map.actions.MapAction.SinglePlaceTap
import com.vk59.gotuda.map.actions.MapActionsListener
import com.vk59.gotuda.map.model.MapNotAttachedToWindowException
import com.vk59.gotuda.map.model.MyGeoPoint
import com.vk59.gotuda.presentation.main.MainFragmentState.FinishActivity
import com.vk59.gotuda.presentation.main.MainFragmentState.LaunchPlace
import com.vk59.gotuda.presentation.main.MainFragmentState.Main
import com.vk59.gotuda.presentation.main.MainFragmentState.MainButtonLoading
import com.vk59.gotuda.presentation.main.MapViewType.MAPKIT
import com.vk59.gotuda.presentation.main.buttons.MainButtonsGoViewFactory
import com.vk59.gotuda.presentation.profile.ProfileFragment
import com.vk59.gotuda.presentation.qr.UserQrFragment
import com.vk59.gotuda.presentation.settings.SettingsFragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.CameraUpdateReason.GESTURES
import com.yandex.mapkit.map.Map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// TODO: Make it initializing Fragment.
/**
 * The MainFragment must initialize all the required components only
 */
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), CameraListener, MapActionsListener {

  private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

  private val viewModel: MainViewModel by viewModels()

  @Inject
  lateinit var mapController: MapController

  @Inject
  lateinit var permissionsRepository: PermissionsRepository

  @Inject
  lateinit var goViewsCoordinator: GoViewsCoordinator

  @Inject
  lateinit var mainButtonsGoViewFactory: MainButtonsGoViewFactory

  private var currentModalView: View? = null
  private var locationManager: LocationManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java)
    if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
      initLocationManager()
    } else {
      this.requestPermissions()
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
      viewModel.listenToLaunchSettings().collectLatest {
        viewModel.settingsOpened()
        launchSettings()
      }
    }
    lifecycleScope.launch {
      viewModel.listenToQrButtonClicked().collectLatest {
        viewModel.userQrOpened()
        launchUserQr()
      }
    }
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

  private fun launchUserQr() {
    parentFragmentManager.commitWithAnimation {
      replace(R.id.fragment_container, UserQrFragment())
      addToBackStack("main")
    }
  }

  override fun onStart() {
    MapKitFactory.getInstance().onStart()
    binding.mapKit.onStart()
    super.onStart()
    binding.mapKit.map.addCameraListener(this)
    launchMainButtons()
  }

  override fun onResume() {
    super.onResume()
    viewLifecycleOwner.lifecycleScope.launch {
      // TODO: Bad solution, fix it!
      initMap(viewModel.obtainInitialLocation())
      viewModel.getPlaces()
      viewModel.listenToMapObjects().collectLatest { list ->
        list.forEach { place ->
          val icon = if (place.selected) R.drawable.ic_place_selected else R.drawable.ic_place
          this@MainFragment.mapController.addPlacemark(place.id, place.geoPoint, icon)
        }
      }
    }
    locationManager?.let {
      launchObserveGeoUpdates(it)
    }
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.listenToFragmentState().collectLatest { state ->
        when (state) {
          Main -> launchMainButtons()
          MainButtonLoading -> launchMainButtons(loading = true)
          is LaunchPlace -> {
            viewModel.selectObject(state.place.id)
            launchCardRecommendations(state.place)
          }

          FinishActivity -> {
            requireActivity().finish()
          }
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.listenToError().collectLatest { error ->
        if (error is ErrorState.Error) {
          ErrorSnackbarFactory(binding.root).create(R.drawable.ic_warning, getString(R.string.something_went_wrong))
            .show()
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.listenToRoutes().collectLatest {
        if (it is RoutesState.WalkRoute) {
          mapController.showRoute(it.route)
        } else {
          mapController.removeRoute()
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
      viewModel.changeFollowing(false)
    }
  }

  override fun onStop() {
    binding.mapKit.onStop()
    MapKitFactory.getInstance().onStop()
    this.mapController.detach()
    binding.mapKit.map.addCameraListener(this)
    super.onStop()
    goViewsCoordinator.hideAll()
  }

  private fun launchMainButtons(loading: Boolean = false) {
    currentModalView?.makeGone()

    goViewsCoordinator.show(mainButtonsGoViewFactory.create(viewModel), binding.root)
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
    binding.cardsView.setOnGeoButtonClickListener {
      viewModel.moveToUserGeo()
    }
  }

  private fun initMap(initialGeoPoint: MyGeoPoint?) {
    requireMapController().attachViews(listOf(binding.mapKit), initialGeoPoint, this)
    viewModel.mapViewType.observe(viewLifecycleOwner) { mapType ->
      when (mapType) {
        MAPKIT -> {
          binding.mapKit.isVisible = true
        }

        else -> { /* Nothing to do */
        }
      }
    }
  }

  @RequiresApi(VERSION_CODES.P)
  private fun initLocationManager() {
    if (locationManager?.isLocationEnabled == true) {
      this.requestPermissions()
    }
  }

  private fun requestPermissions() {
    if (ActivityCompat.checkSelfPermission(
        requireContext(), permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        requireContext(), permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      registerForActivityResult(
        RequestMultiplePermissions(),
      ) { isGranted ->
        permissionsRepository.permissionsGranted(isGranted.values.all { it })
        if (!isGranted.values.all { it }) {
          Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_LONG).show()
        }
      }.launch(arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION))
    } else {
      permissionsRepository.permissionsGranted(true)
    }
  }

  private fun launchObserveGeoUpdates(manager: LocationManager) {
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.listenToUserGeo(manager).collectLatest {
        requireMapController().showUserLocation(it)
        try {
          requireMapController().moveToUserLocation(it)
        } catch (e: java.lang.IllegalStateException) {
          Timber.d(e)
        } catch (mapNotAttached: MapNotAttachedToWindowException) {
          Timber.d(mapNotAttached)
        }
      }
    }
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.listenToMove().collect {
        viewModel.changeFollowing(true)
        requireMapController().moveToUserLocation(it.geoPoint)
      }
    }
  }

  private fun requireMapController(): MapController {
    return this.mapController
  }

  companion object {

    private const val TAG = "MainFragment"
  }
}