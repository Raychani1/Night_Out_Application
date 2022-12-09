// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/ui/viewmodels/BarsViewModel.kt
package com.example.android.i_mobv_application.ui.viewmodels.pubs

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.model.Pub
import com.example.android.i_mobv_application.utils.Evento
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import kotlinx.coroutines.launch

class PubViewModel(private val repository: NightOutRepository) : ViewModel() {
	private val _message = MutableLiveData<Evento<String>>()
	val message: LiveData<Evento<String>>
		get() = _message

	val loading = MutableLiveData(false)

	val userLocation = MutableLiveData<NightOutLocation>(null)

	var pubs: LiveData<MutableList<Pub>?> =
		liveData {
			loading.postValue(true)
			repository.apiGetAllPubs(userLocation.value) { _message.postValue(Evento(it)) }
			loading.postValue(false)
			emitSource(repository.databaseGetAllPubs())
		}

	private var _isSortedNameAsc: MutableLiveData<Boolean> = MutableLiveData(false)
	val isSortedNameAsc: LiveData<Boolean?>
		get() = _isSortedNameAsc

	private var _isSortedNumberOfPeopleAsc: MutableLiveData<Boolean> = MutableLiveData(false)
	val isSortedNumberOfPeopleAsc: LiveData<Boolean?>
		get() = _isSortedNumberOfPeopleAsc

	private var _isSortedDistanceAsc: MutableLiveData<Boolean> = MutableLiveData(false)
	val isSortedDistanceAsc: LiveData<Boolean?>
		get() = _isSortedDistanceAsc

	/**
	 * Sort Pubs based on attribute.
	 *
	 * @param sortBy Attribute for direction switch.
	 */
	fun sortPubs(sortBy: String) {
		when (sortBy) {
			"Name" -> _isSortedNameAsc.value = _isSortedNameAsc.value!!.not()
			"NumberOfPeople" -> _isSortedNumberOfPeopleAsc.value =
				_isSortedNumberOfPeopleAsc.value!!.not()
			"Distance" -> _isSortedDistanceAsc.value = _isSortedDistanceAsc.value!!.not()
			else -> {
				// No updates
			}
		}

		pubs.value?.run {
			when (sortBy) {
				"Name" -> {
					when (_isSortedNameAsc.value) {
						true -> sortBy { it.name }
						else -> sortByDescending { it.name }
					}
				}
				"NumberOfPeople" -> {
					when (_isSortedNumberOfPeopleAsc.value) {
						true -> sortBy { it.users }
						else -> sortByDescending { it.users }
					}
				}
				"Distance" -> {
					when (_isSortedDistanceAsc.value) {
						true -> sortBy { it.distance }
						else -> sortByDescending { it.distance }
					}
				}
				else -> {
					sortByDescending { it.users }
				}
			}
		}
	}

	/**
	 * Reload Pubs.
	 */
	fun refreshPubs(userLocation: NightOutLocation?) {
		viewModelScope.launch {
			loading.postValue(true)
			repository.apiGetAllPubs(userLocation) { _message.postValue(Evento(it)) }
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