// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/data/api/helper/TokenAuthenticator.kt
package com.example.android.i_mobv_application.data.network.utils

import android.content.Context
import com.example.android.i_mobv_application.data.network.RestApi
import com.example.android.i_mobv_application.data.network.UserRefreshRequestBody
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route


class TokenAuthenticator(val context: Context) : Authenticator {
	override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
		synchronized(this) {
			if (response.request().header("mobv-auth")
					?.compareTo("accept") == 0 && response.code() == 401
			) {
				val userItem = SharedPreferencesUtils.getInstance().getUserItem(context)

				if (userItem == null) {
					SharedPreferencesUtils.getInstance().clearData(context)
					return null
				}

				val tokenResponse = RestApi.create(context).userRefresh(
					UserRefreshRequestBody(
						refresh = userItem.refresh
					)
				).execute()

				if (tokenResponse.isSuccessful) {
					tokenResponse.body()?.let {
						SharedPreferencesUtils.getInstance().putUserItem(context, it)
						return response.request().newBuilder()
							.header("authorization", "Bearer ${it.access}")
							.build()
					}
				}

				SharedPreferencesUtils.getInstance().clearData(context)
				return null
			}
		}
		return null
	}
}