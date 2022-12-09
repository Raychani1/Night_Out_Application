// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/helpers/ViewModelFactory.kt
package com.example.android.i_mobv_application.utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.ui.viewmodels.auth.AuthViewModel
import com.example.android.i_mobv_application.ui.viewmodels.friends.FriendsViewModel
import com.example.android.i_mobv_application.ui.viewmodels.location.LocationViewModel
import com.example.android.i_mobv_application.ui.viewmodels.pubs.PubDetailViewModel
import com.example.android.i_mobv_application.ui.viewmodels.pubs.PubViewModel

class ViewModelFactory(private val repository: NightOutRepository) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return AuthViewModel(repository) as T
		}

		if (modelClass.isAssignableFrom(PubViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return PubViewModel(repository) as T
		}

		if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return LocationViewModel(repository) as T
		}

		if (modelClass.isAssignableFrom(PubDetailViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return PubDetailViewModel(repository) as T
		}

		if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return FriendsViewModel(repository) as T
		}

		throw IllegalArgumentException("Unknown ViewModel class")
	}
}