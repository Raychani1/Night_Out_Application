package com.example.android.i_mobv_application.ui.fragment.pubs

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentListBinding
import com.example.android.i_mobv_application.ui.adapter.PubCardAdapter
import com.example.android.i_mobv_application.ui.adapter.PubEventListener
import com.example.android.i_mobv_application.ui.viewmodels.location.LocationViewModel
import com.example.android.i_mobv_application.ui.viewmodels.pubs.PubViewModel
import com.example.android.i_mobv_application.utils.bindPubsRecyclerView
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.iconMapping
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import com.example.android.i_mobv_application.utils.location.checkPermissions
import com.example.android.i_mobv_application.utils.setupAppBar
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListFragment : Fragment() {
	private var _binding: FragmentListBinding? = null
	private val binding get() = _binding!!
	private lateinit var pubViewModel: PubViewModel
	private lateinit var locationViewModel: LocationViewModel
	private lateinit var fusedLocationClient: FusedLocationProviderClient

	private var sortButtonClicked = false

	// Location Permissions
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

	// Location Related Functions
	/**
	 * Get current location of the User.
	 */
	@SuppressLint("MissingPermission")
	fun getUserLocation() {
		if (checkPermissions(requireContext())) {
			locationViewModel.loading.postValue(true)

			fusedLocationClient.getCurrentLocation(
				CurrentLocationRequest
					.Builder()
					.setDurationMillis(30000)
					.setMaxUpdateAgeMillis(60000)
					.build(),
				null
			).addOnSuccessListener {
				it?.let {
					val userLocation = NightOutLocation(
						it.latitude,
						it.longitude
					)

					locationViewModel.userLocation.postValue(userLocation)
					pubViewModel.userLocation.postValue(userLocation)
				} ?: locationViewModel.loading.postValue(false)
			}
		}
	}

	// Animations
	private val rotateOpenAnimation: Animation by lazy {
		AnimationUtils.loadAnimation(
			requireContext(),
			R.anim.rotate_open_animation
		)
	}

	private val rotateCloseAnimation: Animation by lazy {
		AnimationUtils.loadAnimation(
			requireContext(),
			R.anim.rotate_close_animation
		)
	}

	private val fromBottomAnimation: Animation by lazy {
		AnimationUtils.loadAnimation(
			requireContext(),
			R.anim.from_bottom_animation
		)
	}

	private val toBottomAnimation: Animation by lazy {
		AnimationUtils.loadAnimation(
			requireContext(),
			R.anim.to_bottom_animation
		)
	}

	/**
	 * Process sort button clicked action.
	 */
	private fun onSortButtonClicked() {
		setVisibility(sortButtonClicked)
		setAnimation(sortButtonClicked)
		buttonSetClickable()

		sortButtonClicked = sortButtonClicked.not()
	}

	/**
	 * Open or close Floating Action Menu.
	 *
	 * @param buttonClicked Sort button was clicked.
	 */
	private fun setVisibility(buttonClicked: Boolean) {
		if (!buttonClicked) {
			binding.sortPubsByName.visibility = VISIBLE
			binding.sortPubsByPeople.visibility = VISIBLE
			binding.sortPubsByDistance.visibility = VISIBLE
		} else {
			binding.sortPubsByName.visibility = INVISIBLE
			binding.sortPubsByPeople.visibility = INVISIBLE
			binding.sortPubsByDistance.visibility = INVISIBLE
		}
	}

	/**
	 * Set animation on Floating Action Buttons.
	 *
	 * @param buttonClicked Sort button was clicked.
	 */
	private fun setAnimation(buttonClicked: Boolean) {
		if (!buttonClicked) {
			binding.sortPubsByName.startAnimation(fromBottomAnimation)
			binding.sortPubsByPeople.startAnimation(fromBottomAnimation)
			binding.sortPubsByDistance.startAnimation(fromBottomAnimation)
			binding.sortPubs.startAnimation(rotateOpenAnimation)
		} else {
			binding.sortPubsByName.startAnimation(toBottomAnimation)
			binding.sortPubsByPeople.startAnimation(toBottomAnimation)
			binding.sortPubsByDistance.startAnimation(toBottomAnimation)
			binding.sortPubs.startAnimation(rotateCloseAnimation)
		}
	}


	/**
	 * Check if main sort button was clicked.
	 */
	private fun buttonSetClickable() {
		if (!sortButtonClicked) {
			binding.sortPubsByName.isClickable = true
			binding.sortPubsByPeople.isClickable = true
			binding.sortPubsByDistance.isClickable = true
		} else {
			binding.sortPubsByName.isClickable = false
			binding.sortPubsByPeople.isClickable = false
			binding.sortPubsByDistance.isClickable = false
		}
	}

	/**
	 * Set Floating Action Button Icon and Tooltip Text.
	 *
	 * @param fab Floating Action Button to modify.
	 * @param attribute Sorting attribute used for accessing mapping.
	 * @param direction Sorting direction.
	 * @param iconMapping Attribute and Direction based icon mapping.
	 * @param tooltipText Tooltip Text.
	 */
	private fun setFabIconAndTooltip(
		fab: FloatingActionButton,
		attribute: String,
		direction: Boolean,
		iconMapping: Map<String, Map<String, String>>,
		tooltipText: String
	) {
		val directionAcronym: String = if (direction) "DESC" else "ASC"

		fab.setImageResource(iconMapping[attribute]?.get(directionAcronym)!!.toInt())
		fab.tooltipText = "Sort Pubs based on $tooltipText in ${
			directionAcronym.lowercase().replaceFirstChar(Char::titlecase)
		}ending order"
	}

	/**
	 * Set Floating Action Button Icons based on current sorting.
	 */
	private fun setIcon() {
		val pairs = mapOf(
			"Name" to mapOf(
				"FAB" to binding.sortPubsByName,
				"Direction" to pubViewModel.isSortedNameAsc.value!!,
				"TooltipText" to "their name"
			),
			"Users" to mapOf(
				"FAB" to binding.sortPubsByPeople,
				"Direction" to pubViewModel.isSortedNumberOfPeopleAsc.value!!,
				"TooltipText" to "the number of people in them"
			),
			"Distance" to mapOf(
				"FAB" to binding.sortPubsByDistance,
				"Direction" to pubViewModel.isSortedDistanceAsc.value!!,
				"TooltipText" to "distance"
			),
		)

		pairs.forEach { (key, value) ->
			setFabIconAndTooltip(
				fab = value["FAB"] as FloatingActionButton,
				attribute = key,
				direction = value["Direction"] as Boolean,
				iconMapping = iconMapping,
				tooltipText = value["TooltipText"] as String
			)
		}

	}

	/**
	 * Sort Pubs.
	 *
	 * @param sortBy Sort Pubs based on this attribute.
	 */
	private fun sortPubs(sortBy: String = "") {
		pubViewModel.sortPubs(sortBy)
		setIcon()
		bindPubsRecyclerView(binding.recyclerView, pubViewModel.pubs.value)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		pubViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[PubViewModel::class.java]

		locationViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[LocationViewModel::class.java]

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

		if (!(checkPermissions(requireContext()))) {
			locationPermissionRequest.launch(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				)
			)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentListBinding.inflate(inflater, container, false)
		setupAppBar(
			title = getString(R.string.night_out),
			fragmentActivity = requireActivity(),
			lifecycleOwner = viewLifecycleOwner,
			context = requireContext(),
			view = binding.root
		)
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility =
			VISIBLE

		getUserLocation()

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val user = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((user?.uid ?: "").isBlank()) {
			Navigation.findNavController(view).navigate(R.id.actionToLogin)
			return
		}

		binding.apply {
			pubViewModel = this@ListFragment.pubViewModel
			locationViewModel = this@ListFragment.locationViewModel
			lifecycleOwner = this@ListFragment.viewLifecycleOwner
		}.also { bnd ->
			bnd.refresh.setOnRefreshListener {
				getUserLocation()
				pubViewModel.refreshPubs(locationViewModel.userLocation.value)
				sortPubs()
			}

			bnd.sortPubs.setOnClickListener {
				onSortButtonClicked()
			}

			bnd.sortPubsByName.setOnClickListener {
				sortPubs("Name")
			}

			bnd.sortPubsByPeople.setOnClickListener {
				sortPubs("NumberOfPeople")
			}

			bnd.sortPubsByDistance.setOnClickListener {
				sortPubs("Distance")
			}
		}

		binding.recyclerView.adapter = PubCardAdapter(
			pubEventListener = PubEventListener { pubId: String, users: Int ->
				findNavController()
					.navigate(
						ListFragmentDirections.actionListFragmentToInfoFragment(
							id = pubId,
							users = users
						)
					)
			},
		)

		pubViewModel.loading.observe(viewLifecycleOwner) {
			binding.refresh.isRefreshing = it
		}

		pubViewModel.message.observe(viewLifecycleOwner) {
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