// SOURCE:https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/data/api/RestApi.kt
package com.example.android.i_mobv_application.data.network

import android.content.Context
import com.example.android.i_mobv_application.data.network.utils.AuthInterceptor
import com.example.android.i_mobv_application.data.network.utils.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


interface RestApi {

	// User related requests
	@POST("user/create.php")
	suspend fun userCreate(@Body user: UserCreateRequestBody): Response<NetworkUser>

	@POST("user/login.php")
	suspend fun userLogin(@Body user: UserLoginRequestBody): Response<NetworkUser>

	@POST("user/refresh.php")
	fun userRefresh(@Body user: UserRefreshRequestBody): Call<NetworkUser>

	// Friends related requests
	@GET("contact/list.php")
	@Headers("mobv-auth: accept")
	suspend fun getFriends(): Response<List<NetworkFriend>>

	@POST("contact/message.php")
	@Headers("mobv-auth: accept")
	suspend fun addFriend(@Body friend: FriendRequestBody): Response<Unit>

	// Pub related requests
	@GET("bar/list.php")
	@Headers("mobv-auth: accept")
	suspend fun getPubs(): Response<List<NetworkPub>>

	@GET("https://overpass-api.de/api/interpreter?")
	suspend fun pubDetail(@Query("data") data: String): Response<NetworkPubsDetail>

	@GET("https://overpass-api.de/api/interpreter?")
	suspend fun pubNearby(@Query("data") data: String): Response<NetworkPubsDetail>

	@POST("bar/message.php")
	@Headers("mobv-auth: accept")
	suspend fun pubMessage(@Body bar: PubMessageRequestBody) : Response<Any>

	companion object {
		private const val BASE_URL = "https://zadanie.mpage.sk/"

		fun create(context: Context): RestApi {
			val client = OkHttpClient.Builder()
				.addInterceptor(AuthInterceptor(context))
				.authenticator(TokenAuthenticator(context))
				.build()

			val moshi = Moshi.Builder()
				.add(KotlinJsonAdapterFactory())
				.build()

			val retrofit = Retrofit.Builder()
				.baseUrl(BASE_URL)
				.client(client)
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()

			return retrofit.create(RestApi::class.java)
		}
	}
}