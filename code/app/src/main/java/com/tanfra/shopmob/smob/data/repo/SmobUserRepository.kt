package com.tanfra.shopmob.smob.data.repo

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.net.SmodUserProfilePicture
import com.tanfra.shopmob.utils.NetApiStatus
import com.tanfra.shopmob.utils.asDatabaseModel
import com.tanfra.shopmob.utils.asDomainModel
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobUserDao the dao that does the Room db operations for table smobUsers
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobUserRepository(
    private val smobUserDao: SmobUserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobUserDataSource {


    // CRUD OVERRIDES (abstracting interface: SmobUserDataSource) ------------------------
    // CRUD OVERRIDES (abstracting interface: SmobUserDataSource) ------------------------
    // CRUD OVERRIDES (abstracting interface: SmobUserDataSource) ------------------------

    /**
     * Get the smob user list from the local db
     * @return Result the holds a Success with all the smob users or an Error object with the error message
     */
    override suspend fun getSmobUsers(): Result<List<SmobUser>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Result.Success(smobUserDao.getSmobUsers().asDomainModel())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob user in the db.
     * @param smobUser the smob user to be inserted
     */
    override suspend fun saveSmobUser(smobUser: SmobUser) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobUserDao.saveSmobUser(smobUser.asDatabaseModel())
            }
        }

    /**
     * Get a smob user by its id
     * @param id to be used to get the smob user
     * @return Result the holds a Success object with the SmobUser or an Error object with the error message
     */
    override suspend fun getSmobUser(id: String): Result<SmobUser> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobUserDTO = smobUserDao.getSmobUserById(id)
                if (smobUserDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Result.Success(smobUserDTO.asDomainModel())
                } else {
                    return@withContext Result.Error("SmobUser not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob users in the db
     */
    override suspend fun deleteAllSmobUsers() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobUserDao.deleteAllSmobUsers()
            }
        }
    }


    // NET-2-LOCAL_DB update  ------------------------------------------------------------
    // NET-2-LOCAL_DB update  ------------------------------------------------------------
    // NET-2-LOCAL_DB update  ------------------------------------------------------------


/*

    // fetch API key from build config parameter SMOD_NET_API_KEY, see: build.gradle (:app)
    private val API_KEY = BuildConfig.SMOD_NET_API_KEY

    // TODO: move this to viewModel?? replace LiveData by Flow??
    // LiveData user profile picture / avatar
    private val _profilePicture= MutableLiveData<SmodUserProfilePicture?>()
    val profilePicture: LiveData<SmodUserProfilePicture?>
        get() = _profilePicture

    // LiveData for storing the status of the most recent RESTful API request - fetch profile pict.
    private val _statusProfilePicture = MutableLiveData<NetApiStatus>()
    val statusProfilePicture: LiveData<NetApiStatus>
        get() = _statusProfilePicture

//    // LiveData for storing the status of the most recent RESTful API request
//    private val _statusNeoWs = MutableLiveData<NetApiStatus>()
//    val statusNeoWs: LiveData<NetApiStatus>
//        get() = _statusNeoWs


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values
        // ... omitting this, appears to cause an ('obscure') crash
        //     - presumably caused by Android calling the LD observer (to update the UI) and
        //       receiving invalid data (null)
        //     - possibly the crash happens in the BindingAdapter, when fetching
        //       statusProfilePicture
        _statusProfilePicture.value = NetApiStatus.DONE
//        _statusNeoWs.value = NetApiStatus.DONE
        _profilePicture.value = null

    }

    // fetch different scopes of data from DB: all asteroids stored in DB
    fun fetchAsteroidsAll(): LiveData<List<Asteroid>> {
        return asteroidsDao.getAllAsteroids().map {
            it.asDomainModel()
        }
    }

    // sync method to retrieve list of users from backend and store it in the local DB
    suspend fun refreshUsersInDB() {

        // set initial status
        _statusProfilePicture.postValue(NetApiStatus.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // attempt to read data from server
            try{
                // initiate the (HTTP) GET request using the provided query parameters
                // (... the URL ends on '?start_date=<startDate.value>&end_date=<...>&...' )
                Timber.i("Sending GET request for NASA/NeoWs data from ${dateToday} to ${dateNextWeek}")
                val response: Response<String> = AsteroidsNeoWsApi.retrofitServiceScalars
                    .getAsteroids(dateToday, dateNextWeek, API_KEY)

                // got any valid data back?
                // ... see: https://johncodeos.com/how-to-parse-json-with-retrofit-converters-using-kotlin/
                if (response.isSuccessful) {
                    Timber.i("NeoWs GET response received (parsing...)")

                    // new network data
                    val netAsteroidData =
                        parseAsteroidsJsonResult(JSONObject(response.body()!!)).asDatabaseModel()

                    // set status to keep UI updated
                    _statusNeoWs.postValue(NetApiStatus.DONE)
                    Timber.i("NeoWs GET request complete (success)")

                    // store network data in DB
                    //
                    // DAO method 'insertAll' allows to be called with 'varargs'
                    // --> convert to (typed) array and use 'spread operator' to turn to 'varargs'
                    asteroidsDao.insertAll(*netAsteroidData.toTypedArray())
                    Timber.i("NeoWs data stored in DB")

                }  // if(response.isSuccessful)

            } catch (e: java.lang.Exception) {

                // something went wrong
                _statusNeoWs.postValue(NetApiStatus.ERROR)
                Timber.i("NeoWs GET request complete (failure)")
                Timber.i("Exception: ${e.message} // ${e.cause}")

            }

        }  // coroutine scope (IO)

    }  // refreshAsteroidsInDB()


    // sync method to update profile pictures in local storage (filesystem) from backend
    suspend fun fetchProfilePicture(smobUser: SmobUserDTO) {

        // send GET request to server - coroutine to avoid blocking the UI thread
        withContext(Dispatchers.IO) {

            // set initial status
            _statusApod.postValue(NetApiStatus.LOADING)

            // attempt to read data from server
            try{
                // initiate the (HTTP) GET request
                Timber.i("Sending GET request for NASA/APOD data")
                val response: Response<SmodUserProfilePicture> =
                    ApodApi.retrofitServiceMoshi.getSmodUserProfilePicture(API_KEY)


                // received anything useful?
                if (response.isSuccessful) {
                    response.body()?.let {
                        _profilePicture.postValue(it)
                    }
                }

                // set status to keep UI updated
                _statusApod.postValue(NetApiStatus.DONE)
                Timber.i("APOD GET request complete (success)")

            } catch (e: java.lang.Exception) {

                // something went wrong --> reset Picture of Day LiveData
                _profilePicture.postValue(null)
                _statusApod.postValue(NetApiStatus.ERROR)
                Timber.i("APOD GET request complete (failure)")
                Timber.i("Exception: ${e.message} // ${e.cause}")

            }

        }  // coroutine scope (IO)

    }  // refreshSmodUserProfilePicture()


    // determine start_date, end_date for download from NASA/NeoWs
    @SuppressLint("WeekBasedYear")
    private fun getNeoWsDownloadDates(): ArrayList<String> {
        val formattedDateList = ArrayList<String>()

        val calendar = Calendar.getInstance()

        // start_date, end_date
        for (i in 1..2) {
            val currentTime = calendar.time
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            formattedDateList.add(dateFormat.format(currentTime))
            calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        }

        // array with 2 strings: start_date, end_date
        return formattedDateList

    }  // getNeoWsDownloadDates()


*/

}
