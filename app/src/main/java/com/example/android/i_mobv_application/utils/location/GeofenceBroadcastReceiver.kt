// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/GeofenceBroadcastReceiver.kt
package com.example.android.i_mobv_application.utils.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.android.i_mobv_application.utils.workers.CheckoutWorker
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
		if (geofencingEvent != null) {
			if (geofencingEvent.hasError()) {
				GeofenceStatusCodes
					.getStatusCodeString(geofencingEvent.errorCode)
				return
			}
		} else {
			return
		}

		// Get the transition type.
		val geofenceTransition = geofencingEvent.geofenceTransition

		// Test that the reported transition was of interest.
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
			val triggeringGeofences = geofencingEvent.triggeringGeofences
			triggeringGeofences?.forEach {
				if (it.requestId.compareTo("night_out_geofence") == 0) {
					context?.let { context ->
						val uploadWorkRequest: WorkRequest =
							OneTimeWorkRequestBuilder<CheckoutWorker>()
								.build()
						WorkManager
							.getInstance(context)
							.enqueue(uploadWorkRequest)
					}
				}
			}
		}
	}
}