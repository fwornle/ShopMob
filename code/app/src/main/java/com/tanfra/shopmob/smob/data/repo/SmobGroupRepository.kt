package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobGroupDao
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobGroupApi
import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import com.tanfra.shopmob.smob.data.net.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.utils.asResource
import com.tanfra.shopmob.smob.work.SmobAppWork
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
 * @param smobGroupDao the dao that does the Room db operations for table smobGroups
 * @param smobGroupApi the api that does the network operations for table smobGroups
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobGroupRepository(
    private val smobGroupDao: SmobGroupDao,
    private val smobGroupApi: SmobGroupApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobGroupDataSource, KoinComponent {

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobGroupDataSource': CRUD, local DB data ---

    /**
     * Get a smob group by its id
     * @param id to be used to get the smob group
     * @return Result the holds a Success object with the SmobGroup or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobGroupATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<SmobGroupATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobGroupDao.getSmobItemById(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob group group from the local db
     * @return Result holds a Success with all the smob groups or an Error object with the error message
     */
    override fun getAllSmobItems(): Flow<Resource<List<SmobGroupATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobGroupATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobGroupDao.getSmobItems().asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob members of a particular smob group from the local db
     * @param id of the smob group
     * @return Result holds a Success with all the smob users or an Error object with the error message
     */
    override fun getSmobGroupsByListId(id: String): Flow<Resource<List<SmobGroupATO>>> {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobGroupATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobGroupDao.getSmobGroupsByListId(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)
    }


    /**
     * Insert a smob group in the db. Replace a potentially existing smob group record.
     * @param smobItemATO the smob group to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobGroupATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB first
            val dbGroup = smobItemATO.asDatabaseModel()
            smobGroupDao.saveSmobItem(dbGroup)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(wManager.netActive) {
                val testRead = getSmobGroupViaApi(dbGroup.id)
                if (testRead.data?.id != dbGroup.id) {
                    // item not found in backend --> use POST to create it
                    saveSmobGroupViaApi(dbGroup)
                } else {
                    // item already exists in backend DB --> use PUT to update it
                    smobGroupApi.updateSmobItemById(dbGroup.id, dbGroup.asNetworkModel())
                }
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob groups in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a group of smob groups to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<SmobGroupATO>) {
        // store all provided smob groups by repeatedly calling upon saveSmobGroup
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob group in the db. Do nothing, if the smob group does not exist.
     * @param smobItemATO the smob group to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: SmobGroupATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbGroup = smobItemATO.asDatabaseModel()
                smobGroupDao.updateSmobItem(dbGroup)

                // then push to backend DB
                // ... use 'update', as group may already exist (equivalent of REPLACE w/h local DB)
                if(wManager.netActive) {
                    smobGroupApi.updateSmobItemById(dbGroup.id, dbGroup.asNetworkModel())
                }

            }
        }

    /**
     * Update an set of existing smob groups in the db. Ignore smob groups which do not exist.
     * @param smobItemsATO the group of smob groups to be updated
     */
    override suspend fun updateSmobItems(smobItemsATO: List<SmobGroupATO>) {
        // update all provided smob groups by repeatedly calling upon updateSmobGroup
        withContext(ioDispatcher) {
            smobItemsATO.map { updateSmobItem(it) }
        }
    }

    /**
     * Delete a smob group in the db
     * @param id ID of the smob group to be deleted
     */
    override suspend fun deleteSmobItem(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobGroupDao.deleteSmobItemById(id)
                if(wManager.netActive) {
                    smobGroupApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob groups in the db
     */
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all groups from local DB
                smobGroupDao.deleteAllSmobItems()

                // then delete all groups from backend DB
                if(wManager.netActive) {
                    getSmobGroupsViaApi().let {
                        if (it.status == Status.SUCCESS) {
                            it.data?.map { smobGroupApi.deleteSmobItemById(it.id) }
                        } else {
                            Timber.w("Unable to get SmobGroup IDs from backend DB (via API) - not deleting anything.")
                        }
                    }
                }
            }

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob groups in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobGroup data...")
            val response: Resource<List<SmobGroupDTO>> = getSmobGroupsViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobGroup data GET request complete (success)")

                // store group data in DB - if any
                response.data?.let {
                    it.map { smobGroupDao.saveSmobItem(it) }
                    Timber.i("SmobGroup data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobGroupsInDB()

    /**
     * Synchronize an individual smob groups in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobGroupInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobGroup data...")
        val response: Resource<SmobGroupDTO> = getSmobGroupViaApi(id)

        // got back any valid data?
        if (response.status == Status.SUCCESS) {

            Timber.i("SmobGroup data GET request complete (success)")


            // send POST request to server - coroutine to avoid blocking the main (UI) thread
            withContext(Dispatchers.IO) {

                // store group data in DB - if any
                response.data?.let {
                    smobGroupDao.saveSmobItem(it)
                    Timber.i("SmobGroup data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

    }  // refreshSmobGroupInLocalDB()



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

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobGroupApi.getSmobItems()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<SmobGroupDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobGroupsFromApi


    // net-facing getter: a specific group
    private suspend fun getSmobGroupViaApi(id: String): Resource<SmobGroupDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobGroupDTO = SmobGroupDTO()
        var result = Resource.loading(dummySmobGroupDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobGroupDTO = smobGroupApi.getSmobItemById(id)
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
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobGroupApi.saveSmobItem(smobGroupDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobGroupDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: update a specific (existing) group
    private suspend fun updateSmobGroupViaApi(
        id: String,
        smobGroupDTO: SmobGroupDTO,
    ) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobGroupApi.updateSmobItemById(id, smobGroupDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobGroupDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: delete a specific (existing) group
    private suspend fun deleteSmobGroupViaApi(id: String) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobGroupApi.deleteSmobItemById(id)
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobGroupDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }

}