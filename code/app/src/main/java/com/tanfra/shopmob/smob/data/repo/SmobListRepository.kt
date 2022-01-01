package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobListDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.local.utils.SmobEntryStatus
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobListApi
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
 * @param smobListDao the dao that does the Room db operations for table smobLists
 * @param smobListApi the api that does the network operations for table smobLists
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobListRepository(
    private val smobListDao: SmobListDao,
    private val smobListApi: SmobListApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobListDataSource, KoinComponent {


    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---

    /**
     * Get a smob list by its id
     * @param id to be used to get the smob list
     * @return Result the holds a Success object with the SmobList or an Error object with the error message
     */
    override suspend fun getSmobList(id: String): Resource<SmobListATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to update local DB for the requested SmobList
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshSmobListInDB(id)

            // now try to fetch data from the local DB
            try {
                val smobListDTO = smobListDao.getSmobListById(id)
                if (smobListDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Resource.success(smobListDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobList not found!", null)
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage, null)
            }
        }
    }

    /**
     * Get the smob list list from the local db
     * @return Result holds a Success with all the smob lists or an Error object with the error message
     */
    override suspend fun getAllSmobLists(): Resource<List<SmobListATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to refresh SmobList data in local DB
            // ... note: currently, this is also scheduled by WorkManager every 60 seconds
            //     --> not essential to re-run this here...
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshDataInLocalDB()

            // now try to fetch data from the local DB
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Resource.success(smobListDao.getSmobLists().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage, null)
            }
        }
    }

    /**
     * Insert a smob list in the db. Replace a potentially existing smob list record.
     * @param smobListATO the smob list to be inserted
     */
    override suspend fun saveSmobList(smobListATO: SmobListATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbList = smobListATO.asDatabaseModel()
                smobListDao.saveSmobList(dbList)

                // then push to backend DB
                // ... use 'update', as list may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobListDao.getSmobListById(dbList.id)?.let { smobListApi.updateSmobList(it.id, it.asNetworkModel()) }
                smobListApi.updateSmobListById(dbList.id, dbList.asNetworkModel())

            }
        }


    /**
     * Insert several smob lists in the db. Replace any potentially existing smob u?ser record.
     * @param smobListsATO a list of smob lists to be inserted
     */
    override suspend fun saveSmobLists(smobListsATO: List<SmobListATO>) {
        // store all provided smob lists by repeatedly calling upon saveSmobList
        withContext(ioDispatcher) {
            smobListsATO.map { saveSmobList(it) }
        }
    }

    /**
     * Update an existing smob list in the db. Do nothing, if the smob list does not exist.
     * @param smobListATO the smob list to be updated
     */
    override suspend fun updateSmobList(smobListATO: SmobListATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbList = smobListATO.asDatabaseModel()
                smobListDao.updateSmobList(dbList)

                // then push to backend DB
                // ... use 'update', as list may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobListDao.getSmobListById(dbList.id)?.let { smobListApi.updateSmobList(it.id, it.asNetworkModel()) }
                smobListApi.updateSmobListById(dbList.id, dbList.asNetworkModel())

            }
        }

    /**
     * Update an set of existing smob lists in the db. Ignore smob lists which do not exist.
     * @param smobListsATO the list of smob lists to be updated
     */
    override suspend fun updateSmobLists(smobListsATO: List<SmobListATO>) {
        // update all provided smob lists by repeatedly calling upon updateSmobList
        withContext(ioDispatcher) {
            smobListsATO.map { updateSmobList(it) }
        }
    }

    /**
     * Delete a smob list in the db
     * @param id ID of the smob list to be deleted
     */
    override suspend fun deleteSmobList(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobListDao.deleteSmobListById(id)
                smobListApi.deleteSmobListById(id)
            }
        }
    }

    /**
     * Deletes all the smob lists in the db
     */
    override suspend fun deleteAllSmobLists() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all lists from local DB
                smobListDao.deleteAllSmobLists()

                // then delete all lists from backend DB
                getSmobListsViaApi().let {
                    if (it.status.equals(Status.SUCCESS)) {
                        it.data?.map { smobListApi.deleteSmobListById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobList IDs from backend DB (via API) - not deleting anything.")
                    }
                }
            }

        }  // context: ioDispatcher
    }


    // TODO: should loop over all list pictures and move them to local storage
    // TODO: make refresSmogListDataInDB sensitive to List data relevant to this list only

    /**
     * Synchronize all smob lists in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // set initial status
        _statusSmobListDataSync.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobList data...")
            val response: Resource<List<SmobListDTO>> = getSmobListsViaApi()

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                // set status to keep UI updated
                _statusSmobListDataSync.postValue(Status.SUCCESS)
                Timber.i("SmobList data GET request complete (success)")

                // store list data in DB - if any
                response.data?.let {
                    it.map { smobListDao.saveSmobList(it) }
                    Timber.i("SmobList data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobListsInDB()

    /**
     * Synchronize an individual smob lists in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobListInDB(id: String) {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobList data...")
            val response: Resource<SmobListDTO> = getSmobListViaApi(id)

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                Timber.i("SmobList data GET request complete (success)")

                // store list data in DB - if any
                response.data?.let {
                    smobListDao.saveSmobList(it)
                    Timber.i("SmobList data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobListInDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobListDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobListDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all lists
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobListsViaApi(): Resource<List<SmobListDTO>> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        var result = Resource.loading(listOf<SmobListDTO>())

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobListApi.getSmobLists()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobListDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobListsFromApi


    // net-facing getter: a specific list
    private suspend fun getSmobListViaApi(id: String): Resource<SmobListDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobListDTO = SmobListDTO(
            "DUMMY",
            "",
            "",
            listOf(),
            listOf(),
            SmobEntryStatus.OPEN,
            -1.0,
        )
        var result = Resource.loading(dummySmobListDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobListDTO = smobListApi.getSmobListById(id)
                    .body()
                    ?.asRepoModel()
                    ?: dummySmobListDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobListDTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobListFromApi


    // net-facing setter: save a specific (new) list
    private suspend fun saveSmobListViaApi(smobListDTO: SmobListDTO) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobListApi.saveSmobList(smobListDTO.asNetworkModel())
        }

    }  // saveSmobListToApi


    // net-facing setter: update a specific (existing) list
    private suspend fun updateSmobListViaApi(
        id: String,
        smobListDTO: SmobListDTO,
    ) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobListApi.updateSmobListById(id, smobListDTO.asNetworkModel())
        }

    }  // updateSmobListToApi


    // net-facing setter: delete a specific (existing) list
    private suspend fun deleteSmobListViaApi(id: String) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobListApi.deleteSmobListById(id)
        }

    }  // deleteSmobListToApi




    // TODO: move this to viewModel?? replace LiveData by Flow??
    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobListDataSync = MutableLiveData<Status>()
    val statusSmobListDataSync: LiveData<Status>
        get() = _statusSmobListDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values which are set using 'postValue', in
        // case the repository class is initialized from within a background task, e.g. when using
        // WorkManager to schedule a background update (and this happens to be the first access of
        // a repository service)
        // ... omitting proper initialization of LD can cause ('obscure') crashes
        //     - ... e.g. when Android calls the LD observer (to update the UI) and the
        //       BindingAdapter tries to de-reference a null pointer (invalid LD)
        _statusSmobListDataSync.postValue(Status.SUCCESS)

    }

}