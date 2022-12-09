package com.example.android.i_mobv_application.data.repository

import androidx.lifecycle.*
import com.example.android.i_mobv_application.data.database.PubsDatabase
import com.example.android.i_mobv_application.data.database.asModel
import com.example.android.i_mobv_application.data.network.*
import com.example.android.i_mobv_application.model.Friend
import com.example.android.i_mobv_application.model.Pub
import com.example.android.i_mobv_application.utils.*
import com.example.android.i_mobv_application.model.NearbyPub
import com.example.android.i_mobv_application.utils.location.NightOutLocation
import com.example.android.i_mobv_application.utils.location.distanceTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class NightOutRepository(
	private val service: RestApi,
	private val database: PubsDatabase,
	private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

	// User related calls

	/**
	 * Create new User.
	 *
	 * @param name New username.
	 * @param password New password.
	 * @param onError Action to be triggered on error.
	 * @param onStatus Action to be triggered on progress.
	 */
	suspend fun userCreate(
		name: String,
		password: String,
		onError: (error: String) -> Unit,
		onStatus: (success: NetworkUser?) -> Unit
	) {
		try {
			val resp = service.userCreate(UserCreateRequestBody(name = name, password = password))
			if (resp.isSuccessful) {
				resp.body()?.let { user ->
					if (user.uid == "-1") {
						onStatus(null)
						onError(NAME_ALREADY_EXISTS)
					} else {
						onStatus(user)
					}
				}
			} else {
				onError(FAILED_TO_SIGN_UP)
				onStatus(null)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_SIGN_UP_INTERNET)
			onStatus(null)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_SIGN_UP_ERROR)
			onStatus(null)
		}
	}

	/**
	 * Log in User.
	 *
	 * @param name Username.
	 * @param password Password.
	 * @param onError Action to be triggered on error.
	 * @param onStatus Action to be triggered on progress.
	 */
	suspend fun userLogin(
		name: String,
		password: String,
		onError: (error: String) -> Unit,
		onStatus: (success: NetworkUser?) -> Unit
	) {
		try {
			val resp = service.userLogin(UserLoginRequestBody(name = name, password = password))
			if (resp.isSuccessful) {
				resp.body()?.let { user ->
					if (user.uid == "-1") {
						onStatus(null)
						onError(WRONG_NAME_OR_PASSWORD)
					} else {
						onStatus(user)
					}
				}
			} else {
				onError(FAILED_TO_LOG_IN)
				onStatus(null)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOG_IN_INTERNET)
			onStatus(null)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOG_IN_ERROR)
			onStatus(null)
		}
	}

	/**
	 * Get all Friends from API.
	 *
	 * @param onError Action to be triggered on error.
	 *
	 * @return Friends List.
	 */
	suspend fun apiGetAllFriends(
		onError: (error: String) -> Unit
	): List<Friend> {
		var friendList = listOf<Friend>()

		try {
			val resp = service.getFriends()
			if (resp.isSuccessful) {
				resp.body()?.let { friends ->
					if (friends.isNotEmpty()) {
						friendList = friends.map {
							it.asModel()
						} as MutableList<Friend>
					}
				}
			} else {
				onError(FAILED_TO_LOAD_FRIENDS)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_FRIENDS_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_FRIENDS_ERROR)
		}

		return friendList
	}

	/**
	 * Add Friend.
	 *
	 * @param username New Friends Username.
	 * @param onError Action to be triggered on error.
	 * @param onSuccess Action to be triggered on success.
	 */
	suspend fun addFriend(
		username: String,
		onError: (error: String) -> Unit,
		onSuccess: (success: String) -> Unit
	) {
		try {
			val resp = service.addFriend(FriendRequestBody(contact = username))
			if (resp.isSuccessful) {
				onSuccess(FRIEND_ADDED_SUCCESSFULLY)
			} else {
				onError(USERNAME_NOT_FOUND)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_ADD_FRIEND_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_ADD_FRIEND_ERROR)
		}
	}

	// Pubs related calls

	/**
	 * Get all Pubs from API.
	 *
	 * @param userLocation Users current location.
	 * @param onError Action to be triggered on error.
	 */
	suspend fun apiGetAllPubs(
		userLocation: NightOutLocation?,
		onError: (error: String) -> Unit
	) {
		try {
			val resp = service.getPubs()
			if (resp.isSuccessful) {
				resp.body()?.let { bars ->
					withContext(dispatcher) {
						database.pubDao.deletePubs()
						database.pubDao.insertAllPubs(
							bars.map {
								it.asDatabaseModel(userLocation)
							}
						)
					}
				} ?: onError(FAILED_TO_LOAD_BARS)
			} else {
				onError(FAILED_TO_READ_BARS)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_ERROR)
		}
	}

	/**
	 * Get Pub details from API.
	 *
	 * @param id Pub ID.
	 * @param onError Action to be triggered on error.
	 */
	suspend fun apiPubDetail(
		id: String,
		onError: (error: String) -> Unit
	): NearbyPub? {
		var nearby: NearbyPub? = null
		try {
			val q = "[out:json];node($id);out body;>;out skel;"
			val resp = service.pubDetail(q)
			if (resp.isSuccessful) {
				resp.body()?.let { bars ->
					if (bars.elements.isNotEmpty()) {
						val b = bars.elements[0]
						nearby = NearbyPub(
							b.id,
							b.tags.getOrDefault("name", ""),
							b.tags.getOrDefault("amenity", ""),
							b.lat,
							b.lon,
							b.tags
						)
					}
				} ?: onError(FAILED_TO_LOAD_BARS)
			} else {
				onError(FAILED_TO_READ_BARS)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_ERROR)
		}
		return nearby
	}

	/**
	 * Get all Nearby Pubs from API.
	 *
	 * @param lat Users current location Latitude.
	 * @param lon Users current location Longitude.
	 * @param onError Action to be triggered on error.
	 *
	 * @return List of Nearby Pubs.
	 */
	suspend fun apiNearbyPubs(
		lat: Double,
		lon: Double,
		onError: (error: String) -> Unit
	): List<NearbyPub> {
		var nearby = listOf<NearbyPub>()
		try {
			val q =
				"[out:json];node(around:250,$lat,$lon);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
			val resp = service.pubNearby(q)

			if (resp.isSuccessful) {
				resp.body()?.let { bars ->

					nearby = bars.elements.map {
						NearbyPub(
							it.id,
							it.tags.getOrDefault("name", ""),
							it.tags.getOrDefault("amenity", ""),
							it.lat,
							it.lon,
							it.tags
						).apply {
							distance = distanceTo(
								NightOutLocation(lat, lon),
								NightOutLocation(it.lat, it.lon)
							)
						}
					}

					nearby = nearby.filter { it.name.isNotBlank() }.sortedBy { it.distance }
				} ?: onError(FAILED_TO_LOAD_BARS)
			} else {
				onError(FAILED_TO_READ_BARS)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOAD_BARS_ERROR)
		}
		return nearby
	}

	/**
	 * Check in User to Pub through API.
	 *
	 * @param bar Nearby Pub to check into.
	 * @param onError Action to be triggered on error.
	 * @param onSuccess Action to be triggered on success.
	 */
	suspend fun apiPubCheckin(
		bar: NearbyPub,
		onError: (error: String) -> Unit,
		onSuccess: (success: Boolean) -> Unit
	) {
		try {
			val resp = service.pubMessage(
				PubMessageRequestBody(
					bar.id,
					bar.name,
					bar.type,
					bar.lat,
					bar.lon
				)
			)
			if (resp.isSuccessful) {
				resp.body()?.let { _ ->
					onSuccess(true)
				}
			} else {
				onError(FAILED_TO_LOG_IN)
			}
		} catch (ex: IOException) {
			ex.printStackTrace()
			onError(FAILED_TO_LOG_IN_INTERNET)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onError(FAILED_TO_LOG_IN_ERROR)
		}
	}

	/**
	 * Pull every Pub from Database.
	 *
	 * @return List of Pubs.
	 */
	fun databaseGetAllPubs(): LiveData<MutableList<Pub>?> {
		return database.pubDao.getAllPubs().map { it?.asModel() }
	}

	companion object {
		@Volatile
		private var INSTANCE: NightOutRepository? = null

		fun getInstance(service: RestApi, database: PubsDatabase): NightOutRepository =
			INSTANCE ?: synchronized(this) {
				INSTANCE
					?: NightOutRepository(service, database).also { INSTANCE = it }
			}
	}
}