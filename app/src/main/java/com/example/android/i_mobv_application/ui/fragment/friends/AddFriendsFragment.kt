package com.example.android.i_mobv_application.ui.fragment.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentAddFriendsBinding
import com.example.android.i_mobv_application.ui.viewmodels.friends.FriendsViewModel
import com.example.android.i_mobv_application.utils.setAppBarTitle
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.material.bottomnavigation.BottomNavigationView


class AddFriendsFragment : Fragment() {
	private var _binding: FragmentAddFriendsBinding? = null
	private val binding get() = _binding!!
	private lateinit var friendsViewModel: FriendsViewModel

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
		setAppBarTitle(
			title = getString(R.string.add_friends),
			fragmentActivity = requireActivity()
		)
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
		_binding = FragmentAddFriendsBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			friendsViewModel = this@AddFriendsFragment.friendsViewModel
			lifecycleOwner = this@AddFriendsFragment.viewLifecycleOwner
		}.also { bnd ->
			bnd.addFriend.setOnClickListener {
				if (binding.username.text.toString().isNotBlank()) {
					friendsViewModel.addFriend(
						binding.username.text.toString(),
					)
				}else {
					friendsViewModel.show("Fill in username")
				}
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