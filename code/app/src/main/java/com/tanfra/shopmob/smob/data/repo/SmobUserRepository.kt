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
import com.tanfra.shopmob.smob.data.net.api.asNetworkModel
import com.tanfra.shopmob.smob.data.net.api.asRepoModel
import com.tanfra.shopmob.smob.data.net.utils.Status
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
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


    // --- overrides of general data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, local DB data ---

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



    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject(ResponseHandler::class.java)



    // TODO: this needs to go outside the repo
    // TODO: this needs to go outside the repo
    // TODO: this needs to go outside the repo
    // TODO: this needs to go outside the repo
    // TODO: this needs to go outside the repo

    // net-facing getter: all users
    // ... wrap in Response (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobUsersFromApi(): List<SmobUserDTO> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                responseHandler.handleSuccess(smobUserApi.getSmobUsers())
                    .data
                    ?.body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

            } catch (ex: Exception) {
                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobUserDTO>>(ex)
                Timber.e(daException.message)

                // ... then return empty handed
                listOf()
            }

        }  // espresso: idlingResource

    }  // getSmobUsersFromApi


    // net-facing getter: a specific user
    private suspend fun getSmobUserFromApi(id: String): SmobUserDTO? = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                responseHandler.handleSuccess(smobUserApi.getSmobUserById(id))
                    .data
                    ?.body()
                    ?.asRepoModel()    // returns 'null' if requested user cannot be found

            } catch (ex: Exception) {
                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobUserDTO>(ex)
                Timber.e(daException.message)

                // ... then return empty handed
                null
            }

        }  // espresso: idlingResource

    }  // getSmobUserFromApi


    // net-facing setter: save a specific (new) user
    private suspend fun saveSmobUserViaApi(smobUserDTO: SmobUserDTO) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobUserApi.saveSmobUser(smobUserDTO.asNetworkModel())
        }

    }  // saveSmobUserToApi


    // net-facing setter: update a specific (existing) user
    private suspend fun updateSmobUserViaApi(
        id: String,
        smobUserDTO: SmobUserDTO,
    ) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobUserApi.updateSmobUserById(id, smobUserDTO.asNetworkModel())
        }

    }  // updateSmobUserToApi


    // net-facing setter: delete a specific (existing) user
    private suspend fun deleteSmobUserViaApi(id: String) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobUserApi.deleteSmobUserById(id)
        }

    }  // deleteSmobUserToApi




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
        //     - possibly the crash happens in the BindingAdapter, when trying to use this null ref
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

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobUser data...")
            val response: List<SmobUserDTO> = getSmobUsersFromApi()

            // smoke test for net-CRUD (quick workaround... to avoid having to set up proper tests)
//            val response2: SmobUserDTO? = getSmobUserFromApi("07c295ad-b286-41f7-b2ea-e81a75875d02")
//            Timber.i(response2?.toString())
//
//            val testTxUser: SmobUserDTO = SmobUserDTO(
//                username = "maMu",
//                name = "Max Mustermann",
//                email = "max@mustermann.org",
//                imageUrl = null
//            )
//            saveSmobUserViaApi(testTxUser)
//
//            // read back 'first' max mustermann
//            testTxUser.imageUrl = Date().time.toString()
//            updateSmobUserViaApi(
//                "0794b744-7fa4-4440-b284-8c72012ed6cf",
//                testTxUser
//            )
//
//            deleteSmobUserViaApi("1bbd2da1-e028-4e42-b6b8-7d944013abca")

            // got any valid data back?
            if (!response.isEmpty()) {

                // set status to keep UI updated
                _statusSmobUserDataSync.postValue(Status.SUCCESS)
                Timber.i("SmobUser data GET request complete (success)")

                // store user data in DB
                //
                // DAO method 'insertAll' allows to be called with 'varargs'
                // --> convert to (typed) array and use 'spread operator' to turn to 'varargs'
                smobUserDao.insertAll(*response.toTypedArray())
                Timber.i("SmobUser data items stored in local DB")

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobUserDataInDB()


    // TODO: should loop over all user pictures and move them to local storage
    // TODO: make refresSmogUserDataInDB sensitive to User data relevant to this user only

}
