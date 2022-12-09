// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/ui/viewmodels/LocateViewModel.kt
package com.example.android.i_mobv_application.ui.viewmodels.location

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.utils.Evento
import com.example.android.i_mobv_application.model.NearbyPub
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: NightOutRepository) : ViewModel() {
	private val _message = MutableLiveData<Evento<String>>()
	val message: LiveData<Evento<String>>
		get() = _message

	val loading = MutableLiveData(false)

	val userLocation = MutableLiveData<NightOutLocation>(null)
	val nearestPub = MutableLiveData<NearbyPub>(null)

	private val _checkedIn = MutableLiveData<Evento<Boolean>>()
	val checkedIn: LiveData<Evento<Boolean>>
		get() = _checkedIn

	val pubsInLocation: LiveData<List<NearbyPub>> = userLocation.switchMap {
		liveData {
			loading.postValue(true)
			it?.let {
				val b = repository.apiNearbyPubs(it.lat, it.lon) { _message.postValue(Evento(it)) }
				emit(b)
				if (nearestPub.value == null) {
					nearestPub.postValue(b.firstOrNull())
				}
			} ?: emit(listOf())
			loading.postValue(false)
		}
	}

	/**
	 * Check in user to given Pub.
	 */
	fun checkMe() {
		viewModelScope.launch {
			nearestPub.value?.let {
				repository.apiPubCheckin(
					it,
					{ _message.postValue(Evento(it)) },
					{ _checkedIn.postValue(Evento(it)) }
				)
			}
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