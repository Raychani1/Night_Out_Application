// SOURCE: https://github.com/marosc/mobv2022/blob/master/app/src/main/java/com/example/zadanie/workers/CheckoutWorker.kt
package com.example.android.i_mobv_application.utils.workers

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.data.network.PubMessageRequestBody
import com.example.android.i_mobv_application.data.network.RestApi


class CheckoutWorker(private val appContext: Context, workerParams: WorkerParameters) :
	CoroutineWorker(appContext, workerParams) {

	override suspend fun getForegroundInfo(): ForegroundInfo {
		return ForegroundInfo(
			1, createNotification()
		)
	}

	override suspend fun doWork(): Result {
		val response =
			RestApi.create(appContext).pubMessage(
				PubMessageRequestBody("", "", "", 0.0, 0.0)
			)
		return if (response.isSuccessful) Result.success() else Result.failure()
	}

	private fun createNotification(): Notification {
		val builder =
			NotificationCompat.Builder(appContext, "mobv2022").apply {
				setContentTitle("MOBV 2022")
				setContentText("Exiting bar ...")
				setSmallIcon(R.drawable.ic_baseline_location_on_24)
				priority = NotificationCompat.PRIORITY_DEFAULT
			}

		return builder.build()
	}


}