package com.example.android.i_mobv_application.model

class NearbyPub(
	val id: String,
	val name: String,
	val type: String,
	val lat: Double,
	val lon: Double,
	val tags: Map<String, String>,
	var distance: Double = 0.0
) {
	override fun equals(other: Any?): Boolean {
		val otherPub = other as NearbyPub
		return this.id == otherPub.id && this.name == otherPub.name && this.type == otherPub.type && this.lat == otherPub.lat && this.lon == otherPub.lon && this.tags == otherPub.tags && this.distance == otherPub.distance
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + name.hashCode()
		result = 31 * result + type.hashCode()
		result = 31 * result + lat.hashCode()
		result = 31 * result + lon.hashCode()
		result = 31 * result + tags.hashCode()
		result = 31 * result + distance.hashCode()
		return result
	}
}
