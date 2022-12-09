package com.example.android.i_mobv_application.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentRegisterBinding
import com.example.android.i_mobv_application.ui.viewmodels.auth.AuthViewModel
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.setAppBarTitle
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.material.bottomnavigation.BottomNavigationView


class RegisterFragment : Fragment() {
	private var _binding: FragmentRegisterBinding? = null
	private val binding get() = _binding!!
	private lateinit var authViewModel: AuthViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		authViewModel = ViewModelProvider(
			this,
			Injection.provideViewModelFactory(requireContext())
		)[AuthViewModel::class.java]
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		setAppBarTitle(title = getString(R.string.register), fragmentActivity = requireActivity())
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
		_binding = FragmentRegisterBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val user = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((user?.uid ?: "").isNotBlank()) {
			view.findNavController().navigate(R.id.actionHome)
			return
		}

		binding.apply {
			lifecycleOwner = this@RegisterFragment.viewLifecycleOwner
			authentication = this@RegisterFragment.authViewModel
		}.also { bnd ->
			bnd.register.setOnClickListener {
				if (
					binding.username.text.toString().isNotBlank() &&
					binding.password.text.toString().isNotBlank() &&
					binding.password.text.toString().compareTo(
						binding.confirmPassword.text.toString()
					) == 0
				) {
					authViewModel.register(
						binding.username.text.toString(),
						binding.password.text.toString()
					)
				} else if (
					binding.username.text.toString().isBlank() ||
					binding.password.text.toString().isBlank()
				) {
					authViewModel.show("Fill in name and password")
				} else {
					authViewModel.show("Passwords must be same")
				}
			}

			bnd.logIn.setOnClickListener {
				view.findNavController()
					.navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
			}
		}

		authViewModel.user.observe(viewLifecycleOwner) {
			it?.let {
				SharedPreferencesUtils.getInstance().putUserItem(requireContext(), it)
				Navigation.findNavController(requireView()).navigate(R.id.actionHome)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}