// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/data/api/ApiRequest.kt
package com.example.android.i_mobv_application.data.network

data class UserCreateRequestBody(
	val name: String,
	val password: String
)

data class UserLoginRequestBody(
	val name: String,
	val password: String
)

data class UserRefreshRequestBody(
	val refresh: String
)

data class FriendRequestBody(
	val contact: String
)

data class PubMessageRequestBody(
	val id: String,
	val name: String,
	val type: String,
	val lat: Double,
	val lon: Double
)