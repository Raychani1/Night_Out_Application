package com.example.android.i_mobv_application.data.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface PubDao {
	@Query("SELECT * FROM pubs WHERE users >= 1")
	fun getAllPubs(): LiveData<List<DatabasePub>?>

	@Query("DELETE FROM pubs")
	fun deletePubs()

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAllPubs(pubs: List<DatabasePub>)
}