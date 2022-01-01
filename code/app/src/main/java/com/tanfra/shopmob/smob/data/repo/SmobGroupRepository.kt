package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobGroupDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobGroupApi
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
 * @param smobGroupDao the dao that does the Room db operations for table smobGroups
 * @param smobGroupApi the api that does the network operations for table smobGroups
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobGroupRepository(
    private val smobGroupDao: SmobGroupDao,
    private val smobGroupApi: SmobGroupApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobGroupDataSource, KoinComponent {


    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---

    /**
     * Get a smob group by its id
     * @param id to be used to get the smob group
     * @return Result the holds a Success object with the SmobGroup or an Error object with the error message
     */
    override suspend fun getSmobGroup(id: String): Resource<SmobGroupATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to update local DB for the requested SmobGroup
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshSmobGroupInDB(id)

            // now try to fetch data from the local DB
            try {
                val smobGroupDTO = smobGroupDao.getSmobGroupById(id)
                if (smobGroupDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Resource.success(smobGroupDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobGroup not found!", null)
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage, null)
            }
        }
    }

    /**
     * Get the smob group list from the local db
     * @return Result holds a Success with all the smob groups or an Error object with the error message
     */
    override suspend fun getAllSmobGroups(): Resource<List<SmobGroupATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to refresh SmobGroup data in local DB
            // ... note: currently, this is also scheduled by WorkManager every 60 seconds
            //     --> not essential to re-run this here...
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshDataInLocalDB()

            // now try to fetch data from the local DB
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Resource.success(smobGroupDao.getSmobGroups().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage, null)
            }
        }
    }

    /**
     * Insert a smob group in the db. Replace a potentially existing smob group record.
     * @param smobGroupATO the smob group to be inserted
     */
    override suspend fun saveSmobGroup(smobGroupATO: SmobGroupATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbGroup = smobGroupATO.asDatabaseModel()
                smobGroupDao.saveSmobGroup(dbGroup)

                // then push to backend DB
                // ... use 'update', as group may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobGroupDao.getSmobGroupById(dbGroup.id)?.let { smobGroupApi.updateSmobGroup(it.id, it.asNetworkModel()) }
                smobGroupApi.updateSmobGroupById(dbGroup.id, dbGroup.asNetworkModel())

            }
        }


    /**
     * Insert several smob groups in the db. Replace any potentially existing smob u?ser record.
     * @param smobGroupsATO a list of smob groups to be inserted
     */
    override suspend fun saveSmobGroups(smobGroupsATO: List<SmobGroupATO>) {
        // store all provided smob groups by repeatedly calling upon saveSmobGroup
        withContext(ioDispatcher) {
            smobGroupsATO.map { saveSmobGroup(it) }
        }
    }

    /**
     * Update an existing smob group in the db. Do nothing, if the smob group does not exist.
     * @param smobGroupATO the smob group to be updated
     */
    override suspend fun updateSmobGroup(smobGroupATO: SmobGroupATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbGroup = smobGroupATO.asDatabaseModel()
                smobGroupDao.updateSmobGroup(dbGroup)

                // then push to backend DB
                // ... use 'update', as group may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobGroupDao.getSmobGroupById(dbGroup.id)?.let { smobGroupApi.updateSmobGroup(it.id, it.asNetworkModel()) }
                smobGroupApi.updateSmobGroupById(dbGroup.id, dbGroup.asNetworkModel())

            }
        }

    /**
     * Update an set of existing smob groups in the db. Ignore smob groups which do not exist.
     * @param smobGroupsATO the list of smob groups to be updated
     */
    override suspend fun updateSmobGroups(smobGroupsATO: List<SmobGroupATO>) {
        // update all provided smob groups by repeatedly calling upon updateSmobGroup
        withContext(ioDispatcher) {
            smobGroupsATO.map { updateSmobGroup(it) }
        }
    }

    /**
     * Delete a smob group in the db
     * @param id ID of the smob group to be deleted
     */
    override suspend fun deleteSmobGroup(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobGroupDao.deleteSmobGroupById(id)
                smobGroupApi.deleteSmobGroupById(id)
            }
        }
    }

    /**
     * Deletes all the smob groups in the db
     */
    override suspend fun deleteAllSmobGroups() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all groups from local DB
                smobGroupDao.deleteAllSmobGroups()

                // then delete all groups from backend DB
                getSmobGroupsViaApi().let {
                    if (it.status.equals(Status.SUCCESS)) {
                        it.data?.map { smobGroupApi.deleteSmobGroupById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobGroup IDs from backend DB (via API) - not deleting anything.")
                    }
                }
            }

        }  // context: ioDispatcher
    }


    // TODO: should loop over all group pictures and move them to local storage
    // TODO: make refresSmogGroupDataInDB sensitive to Group data relevant to this group only

    /**
     * Synchronize all smob groups in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // set initial status
        _statusSmobGroupDataSync.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobGroup data...")
            val response: Resource<List<SmobGroupDTO>> = getSmobGroupsViaApi()

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                // set status to keep UI updated
                _statusSmobGroupDataSync.postValue(Status.SUCCESS)
                Timber.i("SmobGroup data GET request complete (success)")

                // store group data in DB - if any
                response.data?.let {
                    it.map { smobGroupDao.saveSmobGroup(it) }
                    Timber.i("SmobGroup data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobGroupsInDB()

    /**
     * Synchronize an individual smob groups in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobGroupInDB(id: String) {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobGroup data...")
            val response: Resource<SmobGroupDTO> = getSmobGroupViaApi(id)

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                Timber.i("SmobGroup data GET request complete (success)")

                // store group data in DB - if any
                response.data?.let {
                    smobGroupDao.saveSmobGroup(it)
                    Timber.i("SmobGroup data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobGroupInDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobGroupDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobGroupDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all groups
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobGroupsViaApi(): Resource<List<SmobGroupDTO>> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        var result = Resource.loading(listOf<SmobGroupDTO>())

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobGroupApi.getSmobGroups()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobGroupDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobGroupsFromApi


    // net-facing getter: a specific group
    private suspend fun getSmobGroupViaApi(id: String): Resource<SmobGroupDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobGroupDTO = SmobGroupDTO(
            "DUMMY",
            "",
            "",
            GroupType.OTHER,
            listOf(),
            "",
            -1,
        )
        var result = Resource.loading(dummySmobGroupDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobGroupDTO = smobGroupApi.getSmobGroupById(id)
                    .body()
                    ?.asRepoModel()
                    ?: dummySmobGroupDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobGroupDTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobGroupFromApi


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobGroupViaApi(smobGroupDTO: SmobGroupDTO) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobGroupApi.saveSmobGroup(smobGroupDTO.asNetworkModel())
        }

    }  // saveSmobGroupToApi


    // net-facing setter: update a specific (existing) group
    private suspend fun updateSmobGroupViaApi(
        id: String,
        smobGroupDTO: SmobGroupDTO,
    ) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobGroupApi.updateSmobGroupById(id, smobGroupDTO.asNetworkModel())
        }

    }  // updateSmobGroupToApi


    // net-facing setter: delete a specific (existing) group
    private suspend fun deleteSmobGroupViaApi(id: String) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobGroupApi.deleteSmobGroupById(id)
        }

    }  // deleteSmobGroupToApi




    // TODO: move this to viewModel?? replace LiveData by Flow??
    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobGroupDataSync = MutableLiveData<Status>()
    val statusSmobGroupDataSync: LiveData<Status>
        get() = _statusSmobGroupDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values which are set using 'postValue', in
        // case the repository class is initialized from within a background task, e.g. when using
        // WorkManager to schedule a background update (and this happens to be the first access of
        // a repository service)
        // ... omitting proper initialization of LD can cause ('obscure') crashes
        //     - ... e.g. when Android calls the LD observer (to update the UI) and the
        //       BindingAdapter tries to de-reference a null pointer (invalid LD)
        _statusSmobGroupDataSync.postValue(Status.SUCCESS)

    }

}