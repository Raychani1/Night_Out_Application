package com.example.android.i_mobv_application.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import android.content.Context


@Database(entities = [DatabasePub::class], version = 7, exportSchema = false)
abstract class PubsDatabase : RoomDatabase() {
	abstract val pubDao: PubDao

	companion object {
		@Volatile
		private var INSTANCE: PubsDatabase? = null

		fun getDatabase(context: Context): PubsDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					PubsDatabase::class.java,
					"night_out_pubs_database"
				)
					.fallbackToDestructiveMigration()
					.build()
				INSTANCE = instance
				return instance
			}
		}
	}
}

