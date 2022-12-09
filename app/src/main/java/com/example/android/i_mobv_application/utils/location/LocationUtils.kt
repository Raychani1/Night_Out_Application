package com.example.android.i_mobv_application.utils.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat

/**
 * Check Background Location Access.
 *
 * @param context Application Context.
 *
 * @return Access Granted.
 */
fun checkBackgroundPermissions(context: Context): Boolean {
	return ActivityCompat.checkSelfPermission(
		context,
		Manifest.permission.ACCESS_BACKGROUND_LOCATION
	) == PackageManager.PERMISSION_GRANTED
}

/**
 * Check Coarse and Fine Location Access.
 *
 * @param context Application Context.
 *
 * @return Access Granted.
 */
fun checkPermissions(context: Context): Boolean {
	return ActivityCompat.checkSelfPermission(
		context,
		Manifest.permission.ACCESS_FINE_LOCATION
	) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
		context,
		Manifest.permission.ACCESS_COARSE_LOCATION
	) == PackageManager.PERMISSION_GRANTED
}

/**
 * Calculate distance between User and Pub Location.
 *
 * @param userLocation User Location.
 * @param pubLocation Pub Location.
 *
 * @return Distance between User and Pub Location.
 */
fun distanceTo(userLocation: NightOutLocation?, pubLocation: NightOutLocation): Double {
	var distance: Double = -1.0

	if (userLocation != null) {
		distance = Location("").apply {
			latitude = pubLocation.lat
			longitude = pubLocation.lon
		}.distanceTo(Location("").apply {
			latitude = userLocation.lat
			longitude = userLocation.lon
		}).toDouble()
	}

	return distance
}

