package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobListDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobListApi
import com.tanfra.shopmob.smob.data.net.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.net.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.utils.asResource
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
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

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobListDataSource': CRUD, local DB data ---

    /**
     * Get a smob list by its id
     * @param id to be used to get the smob list
     * @return Result the holds a Success object with the SmobList or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobListATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<SmobListATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobListDao.getSmobItemById(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob list from the local db
     * @return Result holds a Success with all the smob lists or an Error object with the error message
     */
    override fun getAllSmobItems(): Flow<Resource<List<SmobListATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobListATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobListDao.getSmobItems().asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob list in the db. Replace a potentially existing smob list record.
     * @param smobItemATO the smob list to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobListATO): Unit = withContext(ioDispatcher) {

        // first store in local DB first
        val dbList = smobItemATO.asDatabaseModel()
        //Timber.i("about to save new list in DB +++++++++++++++++++++++++")
        smobListDao.saveSmobItem(dbList)
        //Timber.i("just saved new list in DB +++++++++++++++++++++++++")

        // then push to backend DB
        // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
        if(networkConnectionManager.isNetworkConnected) {
            val testRead = getSmobListViaApi(dbList.itemId)
            if (testRead.data?.itemId != dbList.itemId) {
                // item not found in backend --> use POST to create it
                saveSmobListViaApi(dbList)
            } else {
                // item already exists in backend DB --> use PUT to update it
                smobListApi.updateSmobItemById(dbList.itemId, dbList.asNetworkModel())
            }
        }

    }


    /**
     * Insert several smob lists in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a list of smob lists to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<SmobListATO>) {
        // store all provided smob lists by repeatedly calling upon saveSmobList
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob list in the db. Do nothing, if the smob list does not exist.
     * @param smobItemATO the smob list to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: SmobListATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbList = smobItemATO.asDatabaseModel()
                smobListDao.updateSmobItem(dbList)

                // then push to backend DB
                // ... use 'update', as list may already exist (equivalent of REPLACE w/h local DB)
                if(networkConnectionManager.isNetworkConnected) {
                    smobListApi.updateSmobItemById(dbList.itemId, dbList.asNetworkModel())
                }

            }
        }

    /**
     * Update an set of existing smob lists in the db. Ignore smob lists which do not exist.
     * @param smobItemsATO the list of smob lists to be updated
     */
    override suspend fun updateSmobItems(smobItemsATO: List<SmobListATO>) {
        // update all provided smob lists by repeatedly calling upon updateSmobList
        withContext(ioDispatcher) {
            smobItemsATO.map { updateSmobItem(it) }
        }
    }

    /**
     * Delete a smob list in the db
     * @param id ID of the smob list to be deleted
     */
    override suspend fun deleteSmobItem(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobListDao.deleteSmobItemById(id)
                if(networkConnectionManager.isNetworkConnected) {
                    smobListApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob lists in the db
     */
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all lists from local DB
                smobListDao.deleteAllSmobItems()

                // then delete all lists from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobListsViaApi().let { resList ->
                        if (resList.status.equals(Status.SUCCESS)) {
                            resList.data?.map { smobListApi.deleteSmobItemById(it.itemId) }
                        } else {
                            Timber.w("Unable to get SmobList IDs from backend DB (via API) - not deleting anything.")
                        }
                    }
                }
            }

        }  // context: ioDispatcher
    }


    /**
     * Update all smob lists in the local db by retrieving them from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobList data...")
            val response: Resource<List<SmobListDTO>> = getSmobListsViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobList data GET request complete (success)")

                // store list data in DB - if any
                response.data?.let {
                    it.map { smobListDao.saveSmobItem(it) }
                    Timber.i("SmobList data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobListsInDB()


    /**
     * Update an individual smob list in the local db by retrieving it from the backend (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobList data...")
        val response: Resource<SmobListDTO> = getSmobListViaApi(id)

        // got back any valid data?
        if (response.status.equals(Status.SUCCESS)) {

            Timber.i("SmobList data GET request complete (success)")

            // store data in local DB
            withContext(Dispatchers.IO) {

                // store list data in DB - if any
                response.data?.let {
                    smobListDao.saveSmobItem(it)
                    Timber.i("SmobList data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

    }  // refreshSmobListInLocalDB()


    /**
     * Synchronize an individual smob lists in the remote db by sending it to the backend (API call)
     */
    override suspend fun refreshSmobListInRemoteDB(smobItemATO: SmobListATO) {

        // initiate the (HTTP) PUT request
        Timber.i("Sending PUT request for SmobList data...")
        updateSmobListViaApi(smobItemATO.id, smobItemATO.asDatabaseModel())

    }  // refreshSmobListInLocalDB()


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

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobListApi.getSmobItems()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<SmobListDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobListsFromApi


    // net-facing getter: a specific list
    private suspend fun getSmobListViaApi(id: String): Resource<SmobListDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobListDTO = SmobListDTO()
        var result = Resource.loading(dummySmobListDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobListDTO = smobListApi.getSmobItemById(id)
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


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobListViaApi(smobListDTO: SmobListDTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobListApi.saveSmobItem(smobListDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobListDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: update a specific (existing) group
    private suspend fun updateSmobListViaApi(
        id: String,
        smobListDTO: SmobListDTO,
    ) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobListApi.updateSmobItemById(id, smobListDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobListDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: delete a specific (existing) group
    private suspend fun deleteSmobListViaApi(id: String) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobListApi.deleteSmobItemById(id)
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobListDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }

}