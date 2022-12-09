package com.example.android.i_mobv_application

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.i_mobv_application.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	private lateinit var navController: NavController

	private val appBarConfiguration = AppBarConfiguration(
		setOf(
			R.id.loginFragment,
			R.id.registerFragment,
			R.id.listFragment,
			R.id.locationFragment,
			R.id.friendsFragment,
		)
	)

	override fun onStart() {
		super.onStart()

		@Suppress("DEPRECATION")
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	}

	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController

		setSupportActionBar(binding.toolbar)
		binding.toolbarTitle.text = binding.toolbar.title

		supportActionBar?.setDisplayShowTitleEnabled(false)
		setupActionBarWithNavController(navController, appBarConfiguration)

		val navController = navHostFragment.navController
		findViewById<BottomNavigationView>(R.id.bottom_nav)
			.setupWithNavController(navController)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val navController = findNavController(R.id.nav_host_fragment)
		return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}

	fun setActionBarTitle(title: String?) {
		binding.toolbarTitle.text = title
	}
}