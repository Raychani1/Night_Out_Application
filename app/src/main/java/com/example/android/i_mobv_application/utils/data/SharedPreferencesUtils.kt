package com.example.android.i_mobv_application.utils.data

import android.content.Context
import android.content.SharedPreferences
import com.example.android.i_mobv_application.data.network.NetworkUser
import com.google.gson.Gson

class SharedPreferencesUtils private constructor() {

	/**
	 * Get SharedPreferences Instance.
	 *
	 * @param context Application Context.
	 *
	 * @return SharedPreferences Instance.
	 */
	private fun getSharedPreferences(context: Context?): SharedPreferences? {
		return context?.getSharedPreferences(
			shpKey, Context.MODE_PRIVATE
		)
	}

	/**
	 * Clear Data in SharedPreferences.
	 *
	 * @param context Application Context.
	 */
	fun clearData(context: Context?) {
		val sharedPref = getSharedPreferences(context) ?: return
		val editor = sharedPref.edit()
		editor.clear()
		editor.apply()
	}

	/**
	 * Save User details to SharedPreferences.
	 *
	 * @param context Application Context.
	 * @param userItem User details to save.
	 */
	fun putUserItem(context: Context?, userItem: NetworkUser?) {
		val sharedPref = getSharedPreferences(context) ?: return
		val editor = sharedPref.edit()
		userItem?.let {
			editor.putString(userKey, Gson().toJson(it))
			editor.apply()
			return
		}
		editor.putString(userKey, null)
		editor.apply()
	}

	/**
	 * Fetch User details from SharedPreferences.
	 *
	 * @param context Application Context.
	 *
	 * @return User details.
	 */
	fun getUserItem(context: Context?): NetworkUser? {
		val sharedPref = getSharedPreferences(context) ?: return null
		val json = sharedPref.getString(userKey, null) ?: return null

		return Gson().fromJson(json, NetworkUser::class.java)
	}

	companion object {
		@Volatile
		private var INSTANCE: SharedPreferencesUtils? = null

		fun getInstance(): SharedPreferencesUtils =
			INSTANCE ?: synchronized(this) {
				INSTANCE
					?: SharedPreferencesUtils().also { INSTANCE = it }
			}

		private const val shpKey = "NightOut_SharedPreferencesKey"
		private const val userKey = "NightOut_UserKey"
	}
}