// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/ui/fragments/LocateFragment.kt
package com.example.android.i_mobv_application.ui.fragment.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentLocationBinding
import com.example.android.i_mobv_application.model.NearbyPub
import com.example.android.i_mobv_application.ui.adapter.NearbyPubCardAdapter
import com.example.android.i_mobv_application.ui.adapter.NearbyPubEventListener
import com.example.android.i_mobv_application.ui.viewmodels.location.LocationViewModel
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.location.GeofenceBroadcastReceiver
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import com.example.android.i_mobv_application.utils.location.checkBackgroundPermissions
import com.example.android.i_mobv_application.utils.location.checkPermissions
import com.example.android.i_mobv_application.utils.setupAppBar
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.gms.location.*

class LocationFragment : Fragment() {
	private var _binding: FragmentLocationBinding? = null
	private val binding get() = _binding!!
	private lateinit var locationViewModel: LocationViewModel
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private lateinit var geofencingClient: GeofencingClient

	private val locationPermissionRequest = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		when {
			permissions.getOrDefault(
				Manifest.permission.ACCESS_BACKGROUND_LOCATION,
				false
			) -> {
				// Precise location access granted.
			}
			else -> {
				locationViewModel.show("Background location access denied.")
				// No location access granted.
			}
		}
	}

	/**
	 * Show Permission Dialog for Background Location.
	 */
	private fun permissionDialog() {
		val alertDialog: AlertDialog = requireActivity().let {
			val builder = AlertDialog.Builder(it)
			builder.apply {
				setTitle("Background location needed")
				setMessage("Allow background location (All times) for detecting when you leave bar.")
				setPositiveButton(
					"OK"
				) { _, _ ->
					locationPermissionRequest.launch(
						arrayOf(
							Manifest.permission.ACCESS_BACKGROUND_LOCATION
						)
					)
				}
				setNegativeButton(
					"Cancel"
				) { _, _ ->
					// User cancelled the dialog
				}
			}
			// Create the AlertDialog
			builder.create()
		}
		alertDialog.show()
	}

	/**
	 * Get current location of the User.
	 */
	@SuppressLint("MissingPermission")
	fun getUserLocation() {
		if (checkPermissions(requireContext())) {
			locationViewModel.loading.postValue(true)

			fusedLocationClient.getCurrentLocation(
				CurrentLocationRequest.Builder().setDurationMillis(30000)
					.setMaxUpdateAgeMillis(60000).build(), null
			).addOnSuccessListener {
				it?.let {
					locationViewModel.userLocation.postValue(
						NightOutLocation(
							it.latitude,
							it.longitude
						)
					)
				} ?: locationViewModel.loading.postValue(false)
			}
		}
	}

	/**
	 * Create GEO Fence.
	 *
	 * @param lat Current location Latitude.
	 * @param lon Current location Longitude.
	 */
	@RequiresApi(Build.VERSION_CODES.S)
	@SuppressLint("MissingPermission")
	private fun createFence(lat: Double, lon: Double) {
		if (!checkPermissions(requireContext())) {
			locationViewModel.show("Geofence failed, permissions not granted.")
		}
		val geofenceIntent = PendingIntent.getBroadcast(
			requireContext(), 0,
			Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
		)

		val request = GeofencingRequest.Builder().apply {
			addGeofence(
				Geofence.Builder()
					.setRequestId("night_out_geofence")
					.setCircularRegion(lat, lon, 300F)
					.setExpirationDuration(1000L * 60 * 60 * 24)
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
					.build()
			)
		}.build()

		geofencingClient.addGeofences(request, geofenceIntent).run {
			addOnSuccessListener {
				Handler(Looper.getMainLooper()).postDelayed({
					Navigation.findNavController(requireView()).navigate(R.id.actionHome)
				}, 1000)
			}
			addOnFailureListener {
				// Permission is not granted for All times.
				locationViewModel.show("Geofence failed to create.")
				it.printStackTrace()
			}
		}
	}

	/**
	 * Setup Lottie Animation.
	 *
	 * @param animationView View containing a Lottie Animation.
	 * @param animationId ID of raw Lottie Animation resource.
	 * @param clickStart Start animation on click.
	 */
	private fun setupAnimation(
		animationView: LottieAnimationView,
		animationId: Int,
		clickStart: Boolean
	) {
		animationView.setAnimation(animationId)

		if (clickStart) {
			animationView.setOnClickListener {
				animationView.playAnimation()
			}
		} else {
			animationView.playAnimation()
			animationView.repeatCount = LottieDrawable.INFINITE
			animationView.repeatMode = LottieDrawable.RESTART
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		locationViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[LocationViewModel::class.java]

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
		geofencingClient = LocationServices.getGeofencingClient(requireActivity())
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentLocationBinding.inflate(inflater, container, false)
		setupAppBar(
			title = getString(R.string.location),
			fragmentActivity = requireActivity(),
			lifecycleOwner = viewLifecycleOwner,
			context = requireContext(),
			view = binding.root
		)

		return binding.root
	}

	@RequiresApi(Build.VERSION_CODES.S)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val user = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((user?.uid ?: "").isBlank()) {
			Navigation.findNavController(view).navigate(R.id.actionToLogin)
			return
		}

		binding.apply {
			locationViewModel = this@LocationFragment.locationViewModel
			lifecycleOwner = this@LocationFragment.viewLifecycleOwner
		}.also { bnd ->
			setupAnimation(bnd.markLocation, R.raw.red_mark_location_2, true)
			setupAnimation(bnd.loading, R.raw.female_bartender, false)

			bnd.refresh.setOnRefreshListener {
				getUserLocation()
			}

			bnd.markLocation.setOnClickListener {
				bnd.markLocation.playAnimation()
				if (checkBackgroundPermissions(requireContext())) {
					locationViewModel.checkMe()
				} else {
					permissionDialog()
				}
			}

			bnd.distanceIcon.setImageResource(R.drawable.ic_baseline_signpost_24)
		}

		binding.recyclerView.adapter = NearbyPubCardAdapter(
			nearbyPubEventListener = NearbyPubEventListener { nearbyPub: NearbyPub ->
				locationViewModel.nearestPub.postValue(nearbyPub)
			},
		)

		locationViewModel.loading.observe(viewLifecycleOwner) {
			binding.refresh.isRefreshing = it
		}

		locationViewModel.checkedIn.observe(viewLifecycleOwner) {
			it?.getContentIfNotHandled()?.let {
				if (it) {
					locationViewModel.show("Successfully checked in.")
					locationViewModel.userLocation.value?.let {
						createFence(it.lat, it.lon)
					}
				}
			}
		}

		if (checkPermissions(requireContext())) {
			getUserLocation()
		} else {
			Navigation.findNavController(requireView()).navigate(R.id.actionHome)
		}

		locationViewModel.message.observe(viewLifecycleOwner) {
			if (SharedPreferencesUtils.getInstance().getUserItem(requireContext()) == null) {
				Navigation.findNavController(requireView()).navigate(R.id.actionToLogin)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}