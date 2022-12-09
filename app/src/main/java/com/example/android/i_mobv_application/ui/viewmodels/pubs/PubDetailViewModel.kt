// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/ui/viewmodels/DetailViewModel.kt
package com.example.android.i_mobv_application.ui.viewmodels.pubs

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.repository.NightOutRepository
import com.example.android.i_mobv_application.utils.Evento
import com.example.android.i_mobv_application.model.NearbyPub
import kotlinx.coroutines.launch

class PubDetailViewModel(private val repository: NightOutRepository) : ViewModel() {
	private val _message = MutableLiveData<Evento<String>>()
	val message: LiveData<Evento<String>>
		get() = _message

	val loading = MutableLiveData(false)

	val pub = MutableLiveData<NearbyPub>(null)

	/**
	 * Load Pub Details.
	 *
	 * @param id Pub ID.
	 */
	fun loadBar(id: String) {
		if (id.isBlank())
			return
		viewModelScope.launch {
			loading.postValue(true)
			pub.postValue(repository.apiPubDetail(id) { _message.postValue(Evento(it)) })
			loading.postValue(false)
		}
	}
}