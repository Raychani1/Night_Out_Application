package com.example.android.i_mobv_application.ui.fragment.auth

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FragmentLoginBinding
import com.example.android.i_mobv_application.ui.viewmodels.auth.AuthViewModel
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import com.example.android.i_mobv_application.utils.setAppBarTitle
import com.example.android.i_mobv_application.utils.viewmodels.Injection
import com.google.android.material.bottomnavigation.BottomNavigationView


class LoginFragment : Fragment() {
	private var _binding: FragmentLoginBinding? = null
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
		setAppBarTitle(title = getString(R.string.log_in), fragmentActivity = requireActivity())
		requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
		_binding = FragmentLoginBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val user = SharedPreferencesUtils.getInstance().getUserItem(requireContext())
		if ((user?.uid ?: "").isNotBlank()) {
			view.findNavController()
				.navigate(LoginFragmentDirections.actionLoginFragmentToListFragment())
		}

		binding.apply {
			lifecycleOwner = this@LoginFragment.viewLifecycleOwner
			authentication = this@LoginFragment.authViewModel
		}.also { bnd ->
			bnd.login.setOnClickListener {
				if (
					binding.username.text.toString().isNotBlank() &&
					binding.password.text.toString().isNotBlank()
				) {
					authViewModel.login(
						binding.username.text.toString(),
						binding.password.text.toString()
					)
				} else {
					authViewModel.show("Fill in name and password")
				}
			}

			bnd.register.setOnClickListener {
				view.findNavController()
					.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
			}
		}

		authViewModel.user.observe(viewLifecycleOwner) {
			it?.let {
				SharedPreferencesUtils.getInstance().putUserItem(requireContext(), it)
				view.findNavController()
					.navigate(LoginFragmentDirections.actionLoginFragmentToListFragment())
			}
		}
	}

	override fun onResume() {
		super.onResume()

		binding.root.isFocusableInTouchMode = true
		binding.root.requestFocus()
		binding.root.setOnKeyListener { _, keyCode, _ ->
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				finishAffinity(requireActivity())
				true
			} else false
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}