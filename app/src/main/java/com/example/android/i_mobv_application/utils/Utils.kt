package com.example.android.i_mobv_application.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.example.android.i_mobv_application.MainActivity
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.utils.data.SharedPreferencesUtils

// Helping Variable(s)
val iconMapping: Map<String, Map<String, String>> = mapOf(
	"Name" to mapOf(
		"ASC" to R.drawable.ic_baseline_sort_by_alpha_48.toString(),
		"DESC" to R.drawable.ic_baseline_sort_by_alpha_desc_48.toString(),
	),
	"Users" to mapOf(
		"ASC" to R.drawable.ic_baseline_person_24.toString(),
		"DESC" to R.drawable.ic_baseline_group_24.toString(),
	),
	"Distance" to mapOf(
		"ASC" to R.drawable.ic_baseline_straighten_24.toString(),
		"DESC" to R.drawable.ic_baseline_filter_list_24.toString(),
	),
)

// Functions

/**
 * Return index of element which starts with substring.
 *
 * @param data Data to search through.
 * @param substring Substring to search for.
 *
 * @return Index of List element which starts with substring.
 */
fun indexStartsWith(data: List<String>, substring: String): Int {
	return data.indexOf(data.find { item -> item.startsWith(substring) })
}

/**
 * Process whole week Opening Hours.
 *
 * @param openingHours Generated Pub Opening Hours.
 * @param dayTimeSplit Whole week Opening Hours Data.
 */
fun processWeek(openingHours: MutableList<String>, dayTimeSplit: List<String>) {
	openingHours.forEachIndexed { index, day ->
		openingHours[index] = "${day}: ${dayTimeSplit[0]}"
	}
}

/**
 * Process Day Range Opening Hours.
 *
 * @param openingHours Generated Pub Opening Hours.
 * @param dayTimeSplit Day Range Opening Hours Data.
 */
fun processDaysRange(openingHours: MutableList<String>, dayTimeSplit: List<String>) {
	val daysRange = dayTimeSplit[0].split('-')
	val start = indexStartsWith(openingHours, daysRange[0])
	val end = indexStartsWith(openingHours, daysRange[1])

	if (start > end) {
		for (i in 0..start) {
			openingHours[i] = "${openingHours[i].slice(0..1)}: ${dayTimeSplit[1]}"
		}
		openingHours[end] = "${openingHours[end].slice(0..1)}: ${dayTimeSplit[1]}"
	} else {
		for (i in start..end) {
			openingHours[i] = "${openingHours[i].slice(0..1)}: ${dayTimeSplit[1]}"
		}
	}
}

/**
 * Process Day Pairs Opening Hours.
 *
 * @param openingHours Generated Pub Opening Hours.
 * @param dayTimeSplit Day Pairs Opening Hours Data.
 */
fun processDayPairs(openingHours: MutableList<String>, dayTimeSplit: List<String>) {
	if (dayTimeSplit[0].length == 3) {
		val index = indexStartsWith(openingHours, dayTimeSplit[0].slice(0..1))

		for (i in index..index + 1) {
			openingHours[i] =
				"${openingHours[i].slice(0..1)}: ${dayTimeSplit.last()}"
		}
	} else {
		val days = dayTimeSplit[0].split(',')

		days.forEach { day ->
			openingHours[indexStartsWith(openingHours, day)] =
				"${day}: ${dayTimeSplit[1]}"
		}
	}
}

/**
 * Process Single Day Opening Hours.
 *
 * @param openingHours Generated Pub Opening Hours.
 * @param dayTimeSplit Single Day Opening Hours Data.
 */
fun processSingleDay(openingHours: MutableList<String>, dayTimeSplit: List<String>) {
	openingHours[indexStartsWith(openingHours, dayTimeSplit[0])] =
		"${dayTimeSplit[0].slice(0..1)}: ${dayTimeSplit[1]}"
}

/**
 * Add 'Closed' tag to days without opening hours.
 *
 * @param openingHours Generated Pub Opening Hours.
 */
fun addClosedDays(openingHours: MutableList<String>) {
	openingHours.forEachIndexed { index, day ->
		if (day.length == 2) {
			openingHours[index] = "${day}: Closed"
		}
	}
}

/**
 * Generate Pub Opening Hours.
 *
 * @param openingHoursData Opening Hours Data from the Internet.
 *
 * @return Generated Pub Opening Hours.
 */
fun generateOpeningHours(openingHoursData: String?): String {
	return when {
		!openingHoursData.isNullOrEmpty() -> {
			try {
				val openingHours = mutableListOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
				val splitData = openingHoursData.split(';').map { it.trim() }

				splitData.forEach { data ->
					val dayTimeSplit = data.split(' ')

					when {
						// Day specified
						dayTimeSplit[0][0].isLetter() -> {
							when {
								// Days Range
								dayTimeSplit[0].contains('-') -> processDaysRange(
									openingHours,
									dayTimeSplit
								)

								// Day Pairs
								dayTimeSplit[0].contains(',') -> processDayPairs(
									openingHours,
									dayTimeSplit
								)

								// Single Day
								else -> processSingleDay(openingHours, dayTimeSplit)
							}
						}

						// Day not specified
						else -> processWeek(openingHours, dayTimeSplit)
					}

					// Add Closed Days
					addClosedDays(openingHours)
				}
				openingHours.joinToString("\n")
			} catch (ex: Exception) {
				return "No Opening Hours Data found"
			}

		}
		else -> "No Opening Hours Data found"
	}
}

/**
 * Setup App Bar.
 *
 * @param title App Bar Title.
 * @param fragmentActivity Current Fragment Activity.
 * @param lifecycleOwner Fragment Lifecycle Owner.
 * @param context Application Context.
 * @param view Current View.
 */
fun setupAppBar(
	title: String,
	fragmentActivity: FragmentActivity,
	lifecycleOwner: LifecycleOwner,
	context: Context,
	view: View
) {
	// The usage of an interface lets you inject your own implementation
	val menuHost: MenuHost = fragmentActivity

	setAppBarTitle(title, fragmentActivity)

	// Add menu items without using the Fragment Menu APIs
	// Note how we can tie the MenuProvider to the viewLifecycleOwner
	// and an optional Lifecycle.State (here, RESUMED) to indicate when
	// the menu should be visible
	menuHost.addMenuProvider(object : MenuProvider {
		override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
			// Add menu items here
			menuInflater.inflate(R.menu.logout_menu, menu)
		}

		override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
			// Handle the menu selection
			return when (menuItem.itemId) {
				R.id.action_logout -> {
					createLogoutAlertDialog(view, context).show()
					true
				}
				else -> false
			}
		}
	}, lifecycleOwner, Lifecycle.State.RESUMED)
}

/**
 * Set App Bar Title.
 *
 * @param title New App Bar Title.
 * @param fragmentActivity Current Fragment Activity.
 */
fun setAppBarTitle(title: String, fragmentActivity: FragmentActivity) {
	val main = fragmentActivity as MainActivity
	main.setActionBarTitle(title)
}

/**
 * Create Logout Alert Dialog.
 *
 * @param view Current View.
 * @param context Application Context.
 *
 * @return Created Alert Dialog.
 */
fun createLogoutAlertDialog(view: View, context: Context): AlertDialog {
	val alertDialog: AlertDialog = AlertDialog.Builder(context).create()

	alertDialog.setTitle(R.string.logout)
	alertDialog.setMessage(context.getString(R.string.logout_message))
	alertDialog.setButton(
		Dialog.BUTTON_POSITIVE, context.getString(R.string.yes_logout)
	) { _, _ ->
		SharedPreferencesUtils.getInstance().clearData(context)
		view.findNavController().navigate(R.id.actionToLogin)
	}
	alertDialog.setButton(
		Dialog.BUTTON_NEGATIVE,
		context.getString(R.string.cancel)
	) { dialog, _ ->
		dialog.cancel()
	}

	return alertDialog
}
