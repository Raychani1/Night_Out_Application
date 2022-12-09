// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/ui/viewmodels/AuthViewModel.kt
package com.example.android.i_mobv_application.ui.viewmodels.auth

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.network.NetworkUser
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.utils.Evento
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: NightOutRepository) : ViewModel() {
	private val _message = MutableLiveData<Evento<String>>()
	val message: LiveData<Evento<String>>
		get() = _message

	val loading = MutableLiveData(false)
	val user = MutableLiveData<NetworkUser>(null)

	/**
	 * Log in user.
	 *
	 * @param name Username used for login.
	 * @param password Password used for login.
	 */
	fun login(name: String, password: String) {
		viewModelScope.launch {
			loading.postValue(true)
			repository.userLogin(
				name,
				password,
				{
					_message.postValue(Evento(it))
				},
				{ user.postValue(it) }
			)
			loading.postValue(false)
		}
	}

	/**
	 * Register user.
	 *
	 * @param name Username used for registration.
	 * @param password Password used for registration.
	 */
	fun register(name: String, password: String) {
		viewModelScope.launch {
			loading.postValue(true)
			repository.userCreate(
				name,
				password,
				{ _message.postValue(Evento(it)) },
				{ user.postValue(it) }
			)
			loading.postValue(false)
		}
	}

	/**
	 * Show message.
	 *
	 * @param msg Message to show.
	 */
	fun show(msg: String) {
		_message.postValue(Evento(msg))
	}
}