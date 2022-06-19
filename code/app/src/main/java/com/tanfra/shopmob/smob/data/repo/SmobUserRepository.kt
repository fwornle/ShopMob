package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobItemApi
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import com.tanfra.shopmob.smob.data.net.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
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
 * @param smobUserDao the dao that does the Room db operations for table smobUsers
 * @param smobUserApi the api that does the network operations for table smobUsers
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobUserRepository(
    private val smobUserDao: SmobUserDao,
    private val smobUserApi: SmobUserApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobUserDataSource, KoinComponent {

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

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
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob user shop from the local db
     * @return Result holds a Success with all the smob users or an Error object with the error message
     */
    override fun getAllSmobItems(): Flow<Resource<List<SmobUserATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobUserATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobUserDao.getSmobItems().asDomainModel()
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
    override fun getSmobMembersByGroupId(id: String): Flow<Resource<List<SmobUserATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobUserATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobUserDao.getSmobMembersByGroupId(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
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

            // first store in local DB first
            val dbUser = smobItemATO.asDatabaseModel()
            smobUserDao.saveSmobItem(dbUser)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(wManager.netActive) {
                val testRead = getSmobUserViaApi(dbUser.id)
                if (testRead.data?.id != dbUser.id) {
                    // item not found in backend --> use POST to create it
                    saveSmobUserViaApi(dbUser)
                } else {
                    // item already exists in backend DB --> use PUT to update it
                    smobUserApi.updateSmobItemById(dbUser.id, dbUser.asNetworkModel())
                }
            }

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

                // first store in local DB first
                val dbUser = smobItemATO.asDatabaseModel()
                smobUserDao.updateSmobItem(dbUser)

                // then push to backend DB
                // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
                if(wManager.netActive) {
                    val testRead = getSmobUserViaApi(dbUser.id)
                    if (testRead.data?.id != dbUser.id) {
                        // item not found in backend --> use POST to create it
                        saveSmobUserViaApi(dbUser)
                    } else {
                        // item already exists in backend DB --> use PUT to update it
                        smobUserApi.updateSmobItemById(dbUser.id, dbUser.asNetworkModel())
                    }
                }

            }
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
                if(wManager.netActive) {
                    smobUserApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob shops in the db
     */
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all shops from local DB
                smobUserDao.deleteAllSmobItems()

                // then delete all shops from backend DB
                if(wManager.netActive) {
                    getSmobUsersViaApi().let {
                        if (it.status == Status.SUCCESS) {
                            it.data?.map { smobUserApi.deleteSmobItemById(it.id) }
                        } else {
                            Timber.w("Unable to get smobItem IDs from backend DB (via API) - not deleting anything.")
                        }
                    }
                }
            }

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
            val response: Resource<List<SmobUserDTO>> = getSmobUsersViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobUser data GET request complete (success)")

                // store shop data in DB - if any
                response.data?.let {
                    it.map { smobUserDao.saveSmobItem(it) }
                    Timber.i("SmobUser data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobUsersInDB()

    /**
     * Synchronize an individual smob shops in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobUserInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobUser data...")
        val response: Resource<SmobUserDTO> = getSmobUserViaApi(id)

        // got back any valid data?
        if (response.status == Status.SUCCESS) {

            Timber.i("SmobUser data GET request complete (success)")


            // send POST request to server - coroutine to avoid blocking the main (UI) thread
            withContext(Dispatchers.IO) {

                // store shop data in DB - if any
                response.data?.let {
                    smobUserDao.saveSmobItem(it)
                    Timber.i("SmobUser data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

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
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<SmobUserDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobUsersFromApi


    // net-facing getter: a specific shop
    private suspend fun getSmobUserViaApi(id: String): Resource<SmobUserDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobUserDTO = SmobUserDTO()
        var result = Resource.loading(dummySmobUserDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobUserDTO = smobUserApi.getSmobItemById(id)
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


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobUserViaApi(smobUserDTO: SmobUserDTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobUserApi.saveSmobItem(smobUserDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobUserDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: update a specific (existing) group
    private suspend fun updateSmobUserViaApi(
        id: String,
        smobUserDTO: SmobUserDTO,
    ) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobUserApi.updateSmobItemById(id, smobUserDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobUserDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: delete a specific (existing) group
    private suspend fun deleteSmobUserViaApi(id: String) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobUserApi.deleteSmobItemById(id)
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobUserDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }

}