package com.example.android.i_mobv_application.ui.fragment.friends

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentFriendsBinding
import com.example.android.i_mobv_application.ui.adapter.FriendCardAdapter
import com.example.android.i_mobv_application.ui.adapter.FriendEventListener
import com.example.android.i_mobv_application.ui.viewmodels.friends.FriendsViewModel
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.setupAppBar
import com.example.android.i_mobv_application.utils.viewmodels.Injection

class FriendsFragment : Fragment() {
	private var _binding: FragmentFriendsBinding? = null
	private val binding get() = _binding!!
	private lateinit var friendsViewModel: FriendsViewModel

	/**
	 * Setup Lottie Animation.
	 *
	 * @param animationView View containing a Lottie Animation.
	 * @param animationId ID of raw Lottie Animation resource.
	 */
	private fun setupAnimation(animationView: LottieAnimationView, animationId: Int) {
		animationView.setAnimation(animationId)
		animationView.playAnimation()
		animationView.repeatCount = LottieDrawable.INFINITE
		animationView.repeatMode = LottieDrawable.RESTART
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		friendsViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[FriendsViewModel::class.java]
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFriendsBinding.inflate(inflater, container, false)
		setupAppBar(
			title = getString(R.string.friends),
			fragmentActivity = requireActivity(),
			lifecycleOwner = viewLifecycleOwner,
			context = requireContext(),
			view = binding.root
		)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val user = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((user?.uid ?: "").isBlank()) {
			Navigation.findNavController(view).navigate(R.id.actionToLogin)
			return
		}

		binding.apply {
			friendsViewModel = this@FriendsFragment.friendsViewModel
			lifecycleOwner = this@FriendsFragment.viewLifecycleOwner
		}.also { bnd ->
			setupAnimation(bnd.noFriendsFound, R.raw.empty)
			
			bnd.refresh.setOnRefreshListener {
				friendsViewModel.refreshFriends()
			}

			bnd.addFriends.setOnClickListener {
				view.findNavController()
					.navigate(FriendsFragmentDirections.actionFriendsFragmentToAddFriendsFragment())
			}
		}

		friendsViewModel.loading.observe(viewLifecycleOwner) {
			binding.refresh.isRefreshing = it
		}

		binding.friendsRecyclerView.adapter = FriendCardAdapter(
			friendEventListener = FriendEventListener { pubId: String? ->
				if (pubId != null) {
					findNavController()
						.navigate(
							FriendsFragmentDirections.actionFriendsFragmentToInfoFragment(
								id = pubId,
								users = -1
							)
						)
				} else {
					friendsViewModel.show("Your friends location is unknown.")
				}
			}
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}