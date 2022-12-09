// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/data/api/helper/AuthInterceptor.kt
package com.example.android.i_mobv_application.data.network.utils

import android.content.Context
import com.example.android.i_mobv_application.BuildConfig
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(val context: Context) : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		synchronized(this) {
			val request = chain.request()
				.newBuilder()
				.addHeader("User-Agent", "Mobv-Android/1.0.0")
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")

			if (chain.request().header("mobv-auth")?.compareTo("accept") == 0) {
				request.addHeader(
					"Authorization",
					"Bearer ${SharedPreferencesUtils.getInstance().getUserItem(context)?.access}"
				)

			}
			SharedPreferencesUtils.getInstance().getUserItem(context)?.uid?.let {
				request.addHeader(
					"x-user",
					it
				)
			}
			request.addHeader("x-apikey", BuildConfig.API_KEY)

			return chain.proceed(request.build())
		}
	}
}