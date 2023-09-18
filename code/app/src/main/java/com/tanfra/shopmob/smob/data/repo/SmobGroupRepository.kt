package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dataSource.SmobGroupLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.remote.dataSource.SmobGroupRemoteDataSource
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.remote.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.remote.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.asResource
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
 * @param smobGroupDao data source for CRUD operations in local DB for table smobGroups
 * @param smobGroupApi data source for network based access to remote table smobGroups
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobGroupRepository(
    private val smobGroupDao: SmobGroupLocalDataSource,
    private val smobGroupApi: SmobGroupRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobGroupRepository, KoinComponent {

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // CRUD access functions to local DB - accesses remote data source, if needed
    // CRUD access functions to local DB - accesses remote data source, if needed
    // CRUD access functions to local DB - accesses remote data source, if needed

    /**
     * Get a smob group by its id
     * @param id to be used to get the smob group
     * @return Result the holds a Success object with the SmobGroup or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobGroupATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobGroupDao.getSmobItemById(id)
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()

        }  // idlingResource (testing)

    }

    /**
     * Get the smob group group from the local db
     * @return Result holds a Success with all the smob groups or an Error object with the error message
     */
    override fun getSmobItems(): Flow<Resource<List<SmobGroupATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobGroupDao.getSmobItems()
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()

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
                atoFlow.asResource()
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.Error
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

            // first store in local DB
            val dbGroup = smobItemATO.asDatabaseModel()
            smobGroupDao.saveSmobItem(dbGroup)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(networkConnectionManager.isNetworkConnected) {
                getSmobGroupViaApi(dbGroup.id).let {
                    when (it) {
                        is Resource.Failure -> Timber.i("Couldn't retrieve SmobGroup from remote")
                        is Resource.Empty -> Timber.i("SmobGroup still loading")
                        is Resource.Success -> {
                            if (it.data.id != dbGroup.id) {
                                // item not found in backend --> use POST to create it
                                saveSmobGroupViaApi(dbGroup)
                            } else {
                                // item already exists in backend DB --> use PUT to update it
                                smobGroupApi.updateSmobItemById(dbGroup.id, dbGroup.asNetworkModel())
                            }
                        }
                    }
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

                // first store in local DB
                val dbGroup = smobItemATO.asDatabaseModel()
                smobGroupDao.updateSmobItem(dbGroup)

                // then push to backend DB
                // ... use 'update', as group may already exist (equivalent of REPLACE w/h local DB)
                if(networkConnectionManager.isNetworkConnected) {
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
                if(networkConnectionManager.isNetworkConnected) {
                    smobGroupApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob groups in the db
     */
    override suspend fun deleteSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all groups from local DB
                smobGroupDao.deleteSmobItems()

                // then delete all groups from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobGroupsViaApi().let {
                        when (it) {
                            is Resource.Failure -> Timber.i("Couldn't retrieve SmobGroup from remote")
                            is Resource.Empty -> Timber.i("SmobGroup still loading")
                            is Resource.Success -> {
                                it.data.map { item -> smobGroupApi.deleteSmobItemById(item.id) }
                            }
                        }
                    }
                }

            }  // wrapEspressoIdlingResource

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob groups in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshItemsInLocalDB() {

        // initiate the (HTTP) GET request using the provided query parameters
        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            Timber.i("Sending GET request for SmobGroup data...")

            // use async/await here to avoid premature "null" result of smobXyzApi.getSmobItems()
            async { getSmobGroupsViaApi() }.await().let {
                when (it) {
                    is Resource.Failure -> Timber.i("Couldn't retrieve SmobGroup from remote")
                    is Resource.Empty -> Timber.i("SmobGroup still loading")
                    is Resource.Success -> {
                        Timber.i("SmobGroup data GET request complete (success)")

                        // store list data in DB - if any
                        it.data.let { daList ->
                            // delete current table from local DB (= clear local cache)
                            Timber.i("Deleting all SmobGroup data from local DB")
                            smobGroupDao.deleteSmobItems()
                            Timber.i("Local DB table empty")

                            Timber.i("Storing newly retrieved data in local DB")
                            daList.map { item -> smobGroupDao.saveSmobItem(item) }
                            Timber.i("All SmobGroup data items stored in local DB")
                        }
                    }
                }
            }

        }  // coroutine scope (IO)

    }  // refreshSmobGroupsInDB()

    /**
     * Synchronize an individual smob group in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobGroup data...")
        getSmobGroupViaApi(id).let {
            when (it) {
                is Resource.Failure -> Timber.i("Couldn't retrieve SmobGroup from remote")
                is Resource.Empty -> Timber.i("SmobGroup still loading")
                is Resource.Success -> {
                    Timber.i("SmobGroup data GET request complete (success)")

                    // send POST request to server - coroutine to avoid blocking the main (UI) thread
                    withContext(Dispatchers.IO) {

                        // store group data in DB - if any
                        it.data.let { daGroup ->
                            smobGroupDao.saveSmobItem(daGroup)
                            Timber.i("SmobGroup data items stored in local DB")
                        }

                    }  // coroutine scope (IO)
                }
            }
        }

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
    private suspend fun getSmobGroupsViaApi(): Resource<List<SmobGroupDTO>> =
        withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobGroupApi.getSmobItems()
                    .getOrNull()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // local logging
                Timber.e(ex.message)

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                responseHandler.handleException<ArrayList<SmobGroupDTO>>(ex)

            }

        }  // espresso: idlingResource

    }  // getSmobGroupsFromApi


    // net-facing getter: a specific group
    private suspend fun getSmobGroupViaApi(id: String): Resource<SmobGroupDTO> =
        withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobGroupDTO = SmobGroupDTO()
        var result: Resource<SmobGroupDTO> = Resource.Empty

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobGroupDTO = smobGroupApi.getSmobItemById(id)
                    .getOrNull()
                    ?.asRepoModel()
                    ?: dummySmobGroupDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // local logging
                Timber.e(ex.message)

                // return with exception --> handle it...
                responseHandler.handleException(ex)

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
            // local logging
            Timber.e(ex.message)
            // return with exception --> handle it...
            responseHandler.handleException<SmobGroupDTO>(ex)
        }
    }


}