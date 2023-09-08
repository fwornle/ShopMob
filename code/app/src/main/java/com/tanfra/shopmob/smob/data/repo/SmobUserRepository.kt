package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserRepository
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dataSource.SmobUserLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.remote.dataSource.SmobUserRemoteDataSource
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.remote.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.remote.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
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
 * @param smobUserDao data source for CRUD operations in local DB for table smobUsers
 * @param smobUserApi data source for network based access to remote table smobUsers
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobUserRepository(
    private val smobUserDao: SmobUserLocalDataSource,
    private val smobUserApi: SmobUserRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobUserRepository, KoinComponent {

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobUserDataSource': CRUD, local DB data ---

    /**
     * Get a smob user by its id
     * @param id to be used to get the smob user
     * @return Result the holds a Success object with the SmobUser or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobUserATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<SmobUserATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobUserDao.getSmobItemById(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource("item with id $id not found in local user table")
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.Error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob user shop from the local db
     * @return Result holds a Success with all the smob users or an Error object with the error message
     */
    override fun getSmobItems(): Flow<Resource<List<SmobUserATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobUserATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobUserDao.getSmobItems().asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource("local user table empty")
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.Error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Get the smob members of a particular smob group from the local db
     * @param id of the smob group
     * @return Result holds a Success with all the smob users or an Error object with the error message
     */
    override fun getSmobMembersByGroupId(id: String): Flow<Resource<List<SmobUserATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobUserATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobUserDao.getSmobMembersByGroupId(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource("local user table empty")
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.Error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob shop in the db. Replace a potentially existing smob shop record.
     * @param smobItemATO the smob shop to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobUserATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB
            val dbUser = smobItemATO.asDatabaseModel()
            smobUserDao.saveSmobItem(dbUser)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(networkConnectionManager.isNetworkConnected) {
                getSmobUserViaApi(dbUser.id).let {
                    when (it) {
                        is Resource.Error -> Timber.i("Couldn't retrieve SmobUser from remote")
                        is Resource.Loading -> Timber.i("SmobUser still loading")
                        is Resource.Success -> {
                            if (it.data.id != dbUser.id) {
                                // item not found in backend --> use POST to create it
                                saveSmobUserViaApi(dbUser)
                            } else {
                                // item already exists in backend DB --> use PUT to update it
                                smobUserApi.updateSmobItemById(dbUser.id, dbUser.asNetworkModel())
                            }
                        }
                    }
                }

            }  // wrapEspressoIdlingResource

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob shops in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a shop of smob shops to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<SmobUserATO>) {
        // store all provided smob shops by repeatedly calling upon saveSmobUser
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob shop in the db. Do nothing, if the smob shop does not exist.
     * @param smobItemATO the smob shop to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: SmobUserATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB
                val dbUser = smobItemATO.asDatabaseModel()
                smobUserDao.updateSmobItem(dbUser)

                // then push to backend DB
                // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobUserViaApi(dbUser.id).let {
                        when (it) {
                            is Resource.Error -> Timber.i("Couldn't retrieve SmobUser from remote")
                            is Resource.Loading -> Timber.i("SmobUser still loading")
                            is Resource.Success -> {
                                if (it.data.id != dbUser.id) {
                                    // item not found in backend --> use POST to create it
                                    saveSmobUserViaApi(dbUser)
                                } else {
                                    // item already exists in backend DB --> use PUT to update it
                                    smobUserApi.updateSmobItemById(dbUser.id, dbUser.asNetworkModel())
                                }
                            }
                        }
                    }
                }

            }  // wrapEspressoIdlingResource
        }

    /**
     * Update an set of existing smob shops in the db. Ignore smob shops which do not exist.
     * @param smobItemsATO the shop of smob shops to be updated
     */
    override suspend fun updateSmobItems(smobItemsATO: List<SmobUserATO>) {
        // update all provided smob shops by repeatedly calling upon updateSmobUser
        withContext(ioDispatcher) {
            smobItemsATO.map { updateSmobItem(it) }
        }
    }

    /**
     * Delete a smob shop in the db
     * @param id ID of the smob shop to be deleted
     */
    override suspend fun deleteSmobItem(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobUserDao.deleteSmobItemById(id)
                if(networkConnectionManager.isNetworkConnected) {
                    smobUserApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob shops in the db
     */
    override suspend fun deleteSmobItems() {
        withContext(ioDispatcher) {
            
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all shops from local DB
                smobUserDao.deleteAllSmobItems()

                // then delete all shops from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobUsersViaApi().let {
                        when (it) {
                            is Resource.Error -> Timber.i("Couldn't retrieve SmobUser from remote")
                            is Resource.Loading -> Timber.i("SmobUser still loading")
                            is Resource.Success -> {
                                it.data.map { item -> smobUserApi.deleteSmobItemById(item.id) }
                            }
                        }
                    }
                }
                
            }  // wrapEspressoIdlingResource

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob shops in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobUser data...")
            getSmobUsersViaApi().let {
                when (it) {
                    is Resource.Error -> Timber.i("Couldn't retrieve SmobUser from remote")
                    is Resource.Loading -> Timber.i("SmobUser still loading")
                    is Resource.Success -> {
                        Timber.i("SmobUser data GET request complete (success)")

                        // store group data in DB - if any
                        it.data.let { daList ->
                            daList.map { item -> smobUserDao.saveSmobItem(item) }
                            Timber.i("SmobUser data items stored in local DB")
                        }
                    }
                }
            }

        }  // coroutine scope (IO)

    }  // refreshSmobUsersInDB()

    /**
     * Synchronize an individual smob item in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobUser data...")
        getSmobUserViaApi(id).let {
            when (it) {
                is Resource.Error -> Timber.i("Couldn't retrieve SmobUser from remote")
                is Resource.Loading -> Timber.i("SmobUser still loading")
                is Resource.Success -> {
                    Timber.i("SmobUser data GET request complete (success)")

                    // send POST request to server - coroutine to avoid blocking the main (UI) thread
                    withContext(Dispatchers.IO) {

                        // store group data in DB - if any
                        it.data.let { daUser ->
                            smobUserDao.saveSmobItem(daUser)
                            Timber.i("SmobUser data items stored in local DB")
                        }

                    }  // coroutine scope (IO)
                }
            }
        }

    }  // refreshSmobUserInLocalDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobUserDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all shops
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobUsersViaApi(): Resource<List<SmobUserDTO>> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobUserApi.getSmobItems()
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
                responseHandler.handleException<ArrayList<SmobUserDTO>>(ex)

            }

        }  // espresso: idlingResource

    }  // getSmobUsersFromApi


    // net-facing getter: a specific shop
    private suspend fun getSmobUserViaApi(id: String): Resource<SmobUserDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobUserDTO = SmobUserDTO()
        var result: Resource<SmobUserDTO> = Resource.Loading

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobUserDTO = smobUserApi.getSmobItemById(id)
                    .getOrNull()
                    ?.asRepoModel()
                    ?: dummySmobUserDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // local logging
                Timber.e(ex.message)

                // return with exception --> handle it...
                responseHandler.handleException<SmobUserDTO>(ex)

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobUserFromApi


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobUserViaApi(smobUserDTO: SmobUserDTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobUserApi.saveSmobItem(smobUserDTO.asNetworkModel())
        } catch (ex: Exception) {
            Timber.e(ex.message)
            responseHandler.handleException<SmobUserDTO>(ex)
        }
    }

}