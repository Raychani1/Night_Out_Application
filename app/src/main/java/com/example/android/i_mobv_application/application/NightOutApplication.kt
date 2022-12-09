package com.example.android.i_mobv_application.application

import android.app.Application
import com.example.android.i_mobv_application.data.database.PubsDatabase

class NightOutApplication : Application() {
	val database: PubsDatabase by lazy { PubsDatabase.getDatabase(this) }
}