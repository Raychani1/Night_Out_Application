package com.example.android.i_mobv_application.ui.fragment.pubs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentInfoBinding
import com.example.android.i_mobv_application.ui.viewmodels.pubs.PubDetailViewModel
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.setAppBarTitle
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.material.bottomnavigation.BottomNavigationView


class InfoFragment : Fragment() {
	private var _binding: FragmentInfoBinding? = null
	private val binding get() = _binding!!
	private lateinit var pubDetailViewModel: PubDetailViewModel

	private var id: String? = null
	var users: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		pubDetailViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[PubDetailViewModel::class.java]

		arguments?.let {
			id = it.getString("id")
			users = it.getInt("users")
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		setAppBarTitle(title = getString(R.string.pub_detail), fragmentActivity = requireActivity())
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
		_binding = FragmentInfoBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val x = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((x?.uid ?: "").isBlank()) {
			Navigation.findNavController(view).navigate(R.id.actionToLogin)
			return
		}

		pubDetailViewModel.loadBar(id!!)

		binding.apply {
			lifecycleOwner = this@InfoFragment.viewLifecycleOwner
			infoFragment = this@InfoFragment
			pubDetailViewModel = this@InfoFragment.pubDetailViewModel
		}.also { bnd ->
			if (users != -1){
				bnd.numberOfUsersIcon.setImageResource(R.drawable.ic_baseline_person_pin_circle_24)
			}

			bnd.showOnMap.setOnClickListener {
				startActivity(
					Intent(
						Intent.ACTION_VIEW,
						Uri.parse(
							"geo:${pubDetailViewModel.pub.value?.lat}," +
									"${pubDetailViewModel.pub.value?.lon}"
						)
					)
				)
			}

			bnd.callPhone.setOnClickListener {
				startActivity(
					Intent(
						Intent.ACTION_DIAL,
						Uri.parse(
							"tel:${pubDetailViewModel.pub.value?.tags!!["phone"]}"
						)
					)
				)
			}

			bnd.visitWebsite.setOnClickListener {
				startActivity(
					Intent(
						Intent.ACTION_VIEW,
						Uri.parse(pubDetailViewModel.pub.value?.tags?.get("website"))
					)
				)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility =
			View.VISIBLE
		_binding = null
	}
}