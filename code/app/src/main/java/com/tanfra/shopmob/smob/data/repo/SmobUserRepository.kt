package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.types.SmobUser
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.local.dao.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dao.asDomainModel
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.SmodUserProfilePicture
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import com.tanfra.shopmob.smob.data.net.utils.Status
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobUserDao the dao that does the Room db operations for table smobUsers
 * @param smobUserApi the api that does the network operations for table smobUsers
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobUserRepository(
    private val smobUserDao: SmobUserDao,
    private val smobUserApi: SmobUserApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobUserDataSource {


    // --- overrides of general data interface 'SmobUserDataSource': CRUD access to DB data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD access to DB data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD access to DB data ---

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



    // --- overrides of general data interface 'SmobUserDataSource': Refreshing of NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': Refreshing of NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': Refreshing of NET data ---

    // get singleton instance of network response handler from (Koin) service provider
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject(ResponseHandler::class.java)


    // net facing getter
    // ... wrap in Response (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobUsersFromApi(): Response<List<SmobUserDTO>> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                responseHandler.handleSuccess<ArrayList<SmobUserNTO>>(smobUserApi.getSmobUsers()).asRepoModel()
            } catch (ex: Exception) {
                responseHandler.handleException(ex.localizedMessage)
            }

        }  // espresso: idlingResource

    }






    // TODO: move this to viewModel?? replace LiveData by Flow??
    // LiveData user profile picture / avatar
    private val _profilePicture= MutableLiveData<SmodUserProfilePicture?>()
    val profilePicture: LiveData<SmodUserProfilePicture?>
        get() = _profilePicture

    // LiveData for storing the status of the most recent RESTful API request - fetch profile pict.
    private val _statusSmobUserProfilePicture = MutableLiveData<Status>()
    val statusNetApiSmobUserProfilePicture: LiveData<Status>
        get() = _statusSmobUserProfilePicture

    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobUserDataSync = MutableLiveData<Status>()
    val statusNetApiSmobUserDataSync: LiveData<Status>
        get() = _statusSmobUserDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values
        // ... omitting this, appears to cause an ('obscure') crash
        //     - presumably caused by Android calling the LD observer (to update the UI) and
        //       receiving invalid data (null)
        //     - possibly the crash happens in the BindingAdapter, when fetching
        //       statusProfilePicture
        _statusSmobUserProfilePicture.value = Status.SUCCESS
        _statusSmobUserDataSync.value = Status.SUCCESS
        _profilePicture.value = null

    }

    // sync method to retrieve UserData from backend and store it in the local DB
    override suspend fun refreshSmobUserDataInDB() {

        // set initial status
        _statusSmobUserProfilePicture.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // attempt to read data from server
            try{
                // initiate the (HTTP) GET request using the provided query parameters
                // (... the URL ends on '?start_date=<startDate.value>&end_date=<...>&...' )
                Timber.i("Sending GET request for UserData data...")
                val response: Response<ArrayList<SmobUserDTO>> = smobUserApi.getSmobUsers()

                // got any valid data back?
                // ... see: https://johncodeos.com/how-to-parse-json-with-retrofit-converters-using-kotlin/
                if (response.isSuccessful) {
                    Timber.i("UserData GET response received (parsing...)")

                    // new network data
//                    val netSmobUserData =
//                        parseSmobUserJsonResult(JSONObject(response.body()!!)).asDatabaseModel()

                    // set status to keep UI updated
                    _statusSmobUserDataSync.postValue(Status.SUCCESS)
                    Timber.i("UserData GET request complete (success)")

                    // store network data in DB
                    //
                    // DAO method 'insertAll' allows to be called with 'varargs'
                    // --> convert to (typed) array and use 'spread operator' to turn to 'varargs'
//                    smobUserDao.insertAll(*netSmobUserData.toTypedArray())
                    smobUserDao.insertAll(*response.body()!!.toTypedArray())
                    Timber.i("UserData items stored in local DB")

                }  // if(response.isSuccessful)

            } catch (e: java.lang.Exception) {

                // something went wrong
                _statusSmobUserDataSync.postValue(Status.ERROR)
                Timber.i("SmobUser GET request complete (failure)")
                Timber.i("Exception: ${e.message} // ${e.cause}")

            }

        }  // coroutine scope (IO)

    }  // refreshAsteroidsInDB()


    /*

    // sync method to update SmobUser profile pictures in local storage (filesystem) from backend
    suspend fun refreshSmobUserProfilePictures(smobUser: SmobUserDTO) {

        // TODO: should loop over all pictures in local storage

        // send GET request to server - coroutine to avoid blocking the UI thread
        withContext(Dispatchers.IO) {

            // set initial status
            _statusSmobUserProfilePicture.postValue(NetApiStatus.LOADING)

            // attempt to read data from server
            try{
                // initiate the (HTTP) GET request
                Timber.i("Sending GET request for SmobUser Profile Picture / Avatar")
                val response: Response<SmodUserProfilePicture> =
                    ApodApi.retrofitServiceMoshi.getSmodUserProfilePicture(API_KEY)


                // received anything useful?
                if (response.isSuccessful) {
                    response.body()?.let {
                        _profilePicture.postValue(it)
                    }
                }

                // set status to keep UI updated
                _statusSmobUserProfilePicture.postValue(NetApiStatus.SUCCESS)
                Timber.i("SmobUser Profile Picture GET request complete (success)")

            } catch (e: java.lang.Exception) {

                // something went wrong --> reset Profile Picture LiveData
                _profilePicture.postValue(null)
                _statusSmobUserProfilePicture.postValue(NetApiStatus.ERROR)
                Timber.i("SmobUser Profile Picture GET request complete (failure)")
                Timber.i("Exception: ${e.message} // ${e.cause}")

            }

        }  // coroutine scope (IO)

    }  // refreshSmobUserProfilePictures()


     */

}
