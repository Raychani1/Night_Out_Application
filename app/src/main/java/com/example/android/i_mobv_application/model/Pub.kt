package com.example.android.i_mobv_application.model

data class Pub(
	val id: String,
	val name: String,
	val type: String,
	val latitude: Double,
	val longitude: Double,
	val users: Int,
	val distance: Double?
)

