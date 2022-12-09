package com.example.android.i_mobv_application.data.network

import com.example.android.i_mobv_application.data.database.DatabasePub
import com.example.android.i_mobv_application.model.Friend
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import com.example.android.i_mobv_application.utils.location.distanceTo
import com.squareup.moshi.Json


data class NetworkUser(
	val uid: String,
	val access: String,
	val refresh: String
)

data class NetworkPub(
	@Json(name = "bar_id")
	val id: String,
	@Json(name = "bar_name")
	val name: String,
	@Json(name = "bar_type")
	val type: String,
	@Json(name = "lat")
	val latitude: Double,
	@Json(name = "lon")
	val longitude: Double,
	@Json(name = "users")
	val users: Int,
	val distance: Double?
)

data class NetworkPubDetail(
	val type: String,
	val id: String,
	val lat: Double,
	val lon: Double,
	val tags: Map<String, String>
)

data class NetworkPubsDetail(
	val elements: List<NetworkPubDetail>
)

fun NetworkPub.asDatabaseModel(userLocation: NightOutLocation?): DatabasePub {
	return DatabasePub(
		id = id,
		name = name,
		type = type,
		latitude = latitude,
		longitude = longitude,
		users = users,
		distance = distanceTo(
			userLocation,
			NightOutLocation(latitude, longitude)
		)
	)
}

data class NetworkFriend(
	val user_id: String,
	val user_name: String,
	val bar_id: String?,
	val bar_name: String?
)

fun NetworkFriend.asModel(): Friend {
	return Friend(
		userId = user_id,
		username = user_name,
		currentPubId = bar_id,
		currentPubName = bar_name,
	)
}