// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/helpers/Injection.kt
package com.example.android.i_mobv_application.utils.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.android.i_mobv_application.data.database.PubsDatabase
import com.example.android.i_mobv_application.data.network.RestApi
import com.example.android.i_mobv_application.data.repository.NightOutRepository

object Injection {
	private fun provideDatabase(context: Context): PubsDatabase {
		return PubsDatabase.getDatabase(context)
	}

	private fun provideDataRepository(context: Context): NightOutRepository {
		return NightOutRepository.getInstance(RestApi.create(context), provideDatabase(context))
	}

	fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
		return ViewModelFactory(
			provideDataRepository(
				context
			)
		)
	}
}