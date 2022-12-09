package com.example.android.i_mobv_application.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.i_mobv_application.model.Pub


@Entity(tableName = "pubs")
data class DatabasePub constructor(
	@PrimaryKey
	val id: String,
	@ColumnInfo(name = "name")
	val name: String,
	@ColumnInfo(name = "type")
	val type: String,
	@ColumnInfo(name = "latitude")
	val latitude: Double,
	@ColumnInfo(name = "longitude")
	val longitude: Double,
	@ColumnInfo(name = "users")
	val users: Int,
	val distance: Double?
)

fun DatabasePub.asModel(): Pub {
	return Pub(
		id = id,
		name = name,
		type = type,
		latitude = latitude,
		longitude = longitude,
		users = users,
		distance = distance
	)
}

fun List<DatabasePub>.asModel(): MutableList<Pub> {
	return map {
		it.asModel()
	}.toMutableList()
}

