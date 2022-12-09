package com.example.android.i_mobv_application.ui.viewmodels.friends

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.model.Friend
import com.example.android.i_mobv_application.utils.Evento
import kotlinx.coroutines.launch


class FriendsViewModel(private val repository: NightOutRepository) : ViewModel() {
	private val _message = MutableLiveData<Evento<String>>()
	val message: LiveData<Evento<String>>
		get() = _message

	val loading = MutableLiveData(false)
	val friends = MutableLiveData<List<Friend>?>()

	init {
		viewModelScope.launch {
			loading.postValue(true)
			friends.postValue(
				repository.apiGetAllFriends { _message.postValue(Evento(it)) }
					.sortedBy { it.username }
			)
			loading.postValue(false)
		}
	}

	/**
	 * Refresh Friends List.
	 */
	fun refreshFriends() {
		viewModelScope.launch {
			loading.postValue(true)
			friends.postValue(
				repository.apiGetAllFriends { _message.postValue(Evento(it)) }
					.sortedBy { it.username }
			)
			loading.postValue(false)
		}
	}

	/**
	 * Add new Friend.
	 *
	 * @param username New Friends username.
	 */
	fun addFriend(username: String) {
		viewModelScope.launch {
			loading.postValue(true)
			repository.addFriend(
				username,
				{ _message.postValue(Evento(it)) },
				{ _message.postValue(Evento(it)) }
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