package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dataSource.SmobShopLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.remote.dataSource.SmobShopRemoteDataSource
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.remote.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.remote.nto2dto.asRepoModel
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.asResource
import com.tanfra.shopmob.app.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import kotlin.collections.ArrayList


/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobShopDao data source for CRUD operations in local DB for table smobShops
 * @param smobShopApi data source for network based access to remote table smobShops
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobShopRepository(
    private val smobShopDao: SmobShopLocalDataSource,
    private val smobShopApi: SmobShopRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobShopRepository, KoinComponent {

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---

    /**
     * Get a smob shop by its id
     * @param id to be used to get the smob shop
     * @return Result the holds a Success object with the SmobShop or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobShopATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobShopDao.getSmobItemById(id)
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()

        }  // idlingResource (testing)
    }

    /**
     * Get the smob shop shop from the local db
     * @return Result holds a Success with all the smob shops or an Error object with the error message
     */
    override fun getSmobItems(): Flow<Resource<List<SmobShopATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobShopDao.getSmobItems()
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob shop in the db. Replace a potentially existing smob shop record.
     * @param smobItemATO the smob shop to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobShopATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB
            val dbShop = smobItemATO.asDatabaseModel()
            smobShopDao.saveSmobItem(dbShop)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(networkConnectionManager.isNetworkConnected) {
                getSmobShopViaApi(dbShop.id).let {
                    when (it) {
                        is Resource.Failure -> Timber.i("Couldn't retrieve SmobShop from remote")
                        is Resource.Empty -> Timber.i("SmobShop still loading")
                        is Resource.Success -> {
                            if (it.data.id != dbShop.id) {
                                // item not found in backend --> use POST to create it
                                saveSmobShopViaApi(dbShop)
                            } else {
                                // item already exists in backend DB --> use PUT to update it
                                smobShopApi.updateSmobItemById(dbShop.id, dbShop.asNetworkModel())
                            }
                        }
                    }
                }
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob shops in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a shop of smob shops to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<SmobShopATO>) {
        // store all provided smob shops by repeatedly calling upon saveSmobShop
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob shop in the db. Do nothing, if the smob shop does not exist.
     * @param smobItemATO the smob shop to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: SmobShopATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB
                val dbShop = smobItemATO.asDatabaseModel()
                smobShopDao.updateSmobItem(dbShop)

                // then push to backend DB
                // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobShopViaApi(dbShop.id).let {
                        when (it) {
                            is Resource.Failure -> Timber.i("Couldn't retrieve SmobShop from remote")
                            is Resource.Empty -> Timber.i("SmobShop still loading")
                            is Resource.Success -> {
                                if (it.data.id != dbShop.id) {
                                    // item not found in backend --> use POST to create it
                                    saveSmobShopViaApi(dbShop)
                                } else {
                                    // item already exists in backend DB --> use PUT to update it
                                    smobShopApi.updateSmobItemById(dbShop.id, dbShop.asNetworkModel())
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
    override suspend fun updateSmobItems(smobItemsATO: List<SmobShopATO>) {
        // update all provided smob shops by repeatedly calling upon updateSmobShop
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
                smobShopDao.deleteSmobItemById(id)
                if(networkConnectionManager.isNetworkConnected) {
                    smobShopApi.deleteSmobItemById(id)
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
                smobShopDao.deleteSmobItems()

                // then delete all shops from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobShopsViaApi().let {
                        when (it) {
                            is Resource.Failure -> Timber.i("Couldn't retrieve SmobShop from remote")
                            is Resource.Empty -> Timber.i("SmobShop still loading")
                            is Resource.Success -> {
                                it.data.map { item -> smobShopApi.deleteSmobItemById(item.id) }
                            }
                        }
                    }
                }
            }

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob shops in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshItemsInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobShop data...")

            // use async/await here to avoid premature "null" result of smobXyzApi.getSmobItems()
            getSmobShopsViaApi().let {
                when (it) {
                    is Resource.Failure -> Timber.i("Couldn't retrieve SmobShop from remote")
                    is Resource.Empty -> Timber.i("SmobShop still loading")
                    is Resource.Success -> {
                        Timber.i("SmobShop data GET request complete (success)")

                        // store shop data in DB - if any
                        it.data.let { daList ->
                            // delete current table from local DB (= clear local cache)
                            Timber.i("Deleting all SmobShop data from local DB")
                            smobShopDao.deleteSmobItems()
                            Timber.i("Local shop DB table empty")

                            Timber.i("Storing newly retrieved shop data in local DB")
                            daList.map { item -> smobShopDao.saveSmobItem(item) }
                            Timber.i("All SmobShop data items stored in local DB")
                        }
                    }
                }
            }

        }  // coroutine scope (IO)

    }  // refreshSmobShopsInDB()

    /**
     * Synchronize an individual smob shop in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobShop data...")
        getSmobShopViaApi(id).let {
            when (it) {
                is Resource.Failure -> Timber.i("Couldn't retrieve SmobShop from remote")
                is Resource.Empty -> Timber.i("SmobShop still loading")
                is Resource.Success -> {
                    Timber.i("SmobShop data GET request complete (success)")

                    // send POST request to server - coroutine to avoid blocking the main (UI) thread
                    withContext(Dispatchers.IO) {

                        // store group data in DB - if any
                        it.data.let { daShop ->
                            smobShopDao.saveSmobItem(daShop)
                            Timber.i("SmobShop data items stored in local DB")
                        }

                    }  // coroutine scope (IO)
                }
            }
        }

    }  // refreshSmobShopInLocalDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobShopDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobShopDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all shops
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobShopsViaApi(): Resource<List<SmobShopDTO>> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobShopApi.getSmobItems()
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
                responseHandler.handleException<ArrayList<SmobShopDTO>>(ex)

            }

        }  // espresso: idlingResource

    }  // getSmobShopsFromApi


    // net-facing getter: a specific shop
    private suspend fun getSmobShopViaApi(id: String): Resource<SmobShopDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobShopDTO = SmobShopDTO()
        var result: Resource<SmobShopDTO> = Resource.Empty

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobShopDTO = smobShopApi.getSmobItemById(id)
                    .getOrNull()
                    ?.asRepoModel()
                    ?: dummySmobShopDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // local logging
                Timber.e(ex.message)

                // return with exception --> handle it...
                responseHandler.handleException<SmobShopDTO>(ex)

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobShopFromApi


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobShopViaApi(smobShopDTO: SmobShopDTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobShopApi.saveSmobItem(smobShopDTO.asNetworkModel())
        } catch (ex: Exception) {
            // local logging
            Timber.e(ex.message)
            // return with exception --> handle it...
            responseHandler.handleException<SmobShopDTO>(ex)
        }
    }

}