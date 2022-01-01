package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.SmodUserProfilePicture
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.net.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
) : SmobUserDataSource, KoinComponent {


    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---

    /**
     * Get a smob user by its id
     * @param id to be used to get the smob user
     * @return Result the holds a Success object with the SmobUser or an Error object with the error message
     */
    override suspend fun getSmobUser(id: String): Resource<SmobUserATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to update local DB for the requested SmobUser
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshSmobUserInDB(id)

            // now try to fetch data from the local DB
            try {
                val smobUserDTO = smobUserDao.getSmobUserById(id)
                if (smobUserDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Resource.success(smobUserDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobUser not found!", null)
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage, null)
            }
        }
    }

    /**
     * Get the smob user list from the local db
     * @return Result holds a Success with all the smob users or an Error object with the error message
     */
    override suspend fun getAllSmobUsers(): Resource<List<SmobUserATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to refresh SmobUser data in local DB
            // ... note: currently, this is also scheduled by WorkManager every 60 seconds
            //     --> not essential to re-run this here...
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshDataInLocalDB()

            // now try to fetch data from the local DB
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Resource.success(smobUserDao.getSmobUsers().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage, null)
            }
        }
    }

    /**
     * Insert a smob user in the db. Replace a potentially existing smob user record.
     * @param smobUserATO the smob user to be inserted
     */
    override suspend fun saveSmobUser(smobUserATO: SmobUserATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbUser = smobUserATO.asDatabaseModel()
                smobUserDao.saveSmobUser(dbUser)

                // then push to backend DB
                // ... use 'update', as user may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobUserDao.getSmobUserById(dbUser.id)?.let { smobUserApi.updateSmobUser(it.id, it.asNetworkModel()) }
                smobUserApi.updateSmobUserById(dbUser.id, dbUser.asNetworkModel())

            }
        }


    /**
     * Insert several smob users in the db. Replace any potentially existing smob u?ser record.
     * @param smobUsersATO a list of smob users to be inserted
     */
    override suspend fun saveSmobUsers(smobUsersATO: List<SmobUserATO>) {
        // store all provided smob users by repeatedly calling upon saveSmobUser
        withContext(ioDispatcher) {
            smobUsersATO.map { saveSmobUser(it) }
        }
    }

    /**
     * Update an existing smob user in the db. Do nothing, if the smob user does not exist.
     * @param smobUserATO the smob user to be updated
     */
    override suspend fun updateSmobUser(smobUserATO: SmobUserATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbUser = smobUserATO.asDatabaseModel()
                smobUserDao.updateSmobUser(dbUser)

                // then push to backend DB
                // ... use 'update', as user may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobUserDao.getSmobUserById(dbUser.id)?.let { smobUserApi.updateSmobUser(it.id, it.asNetworkModel()) }
                smobUserApi.updateSmobUserById(dbUser.id, dbUser.asNetworkModel())

            }
        }

    /**
     * Update an set of existing smob users in the db. Ignore smob users which do not exist.
     * @param smobUsersATO the list of smob users to be updated
     */
    override suspend fun updateSmobUsers(smobUsersATO: List<SmobUserATO>) {
        // update all provided smob users by repeatedly calling upon updateSmobUser
        withContext(ioDispatcher) {
            smobUsersATO.map { updateSmobUser(it) }
        }
    }

    /**
     * Delete a smob user in the db
     * @param id ID of the smob user to be deleted
     */
    override suspend fun deleteSmobUser(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobUserDao.deleteSmobUserById(id)
                smobUserApi.deleteSmobUserById(id)
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

                // first delete all users from local DB
                smobUserDao.deleteAllSmobUsers()

                // then delete all users from backend DB
                getSmobUsersViaApi().let {
                    if (it.status.equals(Status.SUCCESS)) {
                        it.data?.map { smobUserApi.deleteSmobUserById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobUser IDs from backend DB (via API) - not deleting anything.")
                    }
                }
            }

        }  // context: ioDispatcher
    }


    // TODO: should loop over all user pictures and move them to local storage
    // TODO: make refresSmogUserDataInDB sensitive to User data relevant to this user only

    /**
     * Synchronize all smob users in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // set initial status
        _statusSmobUserDataSync.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobUser data...")
            val response: Resource<List<SmobUserDTO>> = getSmobUsersViaApi()

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                // set status to keep UI updated
                _statusSmobUserDataSync.postValue(Status.SUCCESS)
                Timber.i("SmobUser data GET request complete (success)")

                // store user data in DB - if any
                response.data?.let {
                    it.map { smobUserDao.saveSmobUser(it) }
                    Timber.i("SmobUser data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobUsersInDB()

    /**
     * Synchronize an individual smob users in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobUserInDB(id: String) {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobUser data...")
            val response: Resource<SmobUserDTO> = getSmobUserViaApi(id)

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                Timber.i("SmobUser data GET request complete (success)")

                // store user data in DB - if any
                response.data?.let {
                    smobUserDao.saveSmobUser(it)
                    Timber.i("SmobUser data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobUserInDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all users
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobUsersViaApi(): Resource<List<SmobUserDTO>> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        var result = Resource.loading(listOf<SmobUserDTO>())

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobUserApi.getSmobUsers()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobUserDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobUsersFromApi


    // net-facing getter: a specific user
    private suspend fun getSmobUserViaApi(id: String): Resource<SmobUserDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobUserDTO = SmobUserDTO(
            "DUMMY",
            "",
            "",
            "",
            null
        )
        var result = Resource.loading(dummySmobUserDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobUserDTO = smobUserApi.getSmobUserById(id)
                    .body()
                    ?.asRepoModel()
                    ?: dummySmobUserDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobUserDTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

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
    val statusSmobUserProfilePicture: LiveData<Status>
        get() = _statusSmobUserProfilePicture

    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobUserDataSync = MutableLiveData<Status>()
    val statusSmobUserDataSync: LiveData<Status>
        get() = _statusSmobUserDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values which are set using 'postValue', in
        // case the repository class is initialized from within a background task, e.g. when using
        // WorkManager to schedule a background update (and this happens to be the first access of
        // a repository service)
        // ... omitting proper initialization of LD can cause ('obscure') crashes
        //     - ... e.g. when Android calls the LD observer (to update the UI) and the
        //       BindingAdapter tries to de-reference a null pointer (invalid LD)
        _statusSmobUserProfilePicture.postValue(Status.SUCCESS)
        _statusSmobUserDataSync.postValue(Status.SUCCESS)
        _profilePicture.postValue(null)

    }

}
