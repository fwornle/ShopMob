package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobShopDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobShopApi
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
 * @param smobShopDao the dao that does the Room db operations for table smobShops
 * @param smobShopApi the api that does the network operations for table smobShops
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobShopRepository(
    private val smobShopDao: SmobShopDao,
    private val smobShopApi: SmobShopApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobShopDataSource, KoinComponent {

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

        // try to fetch data from the local DB
        var atoFlow: Flow<SmobShopATO?> = flowOf(null)
        return try {
            // fetch data from DB (and convert to ATO)
            atoFlow = smobShopDao.getSmobItemById(id).asDomainModel()
            // wrap data in Resource (--> error/success/[loading])
            atoFlow.asResource(null)
        } catch (e: Exception) {
            // handle exceptions --> error message returned in Resource.error
            atoFlow.asResource(e.localizedMessage)
        }

    }

    /**
     * Get the smob shop shop from the local db
     * @return Result holds a Success with all the smob shops or an Error object with the error message
     */
    override fun getAllSmobItems(): Flow<Resource<List<SmobShopATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobShopATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobShopDao.getSmobItems().asDomainModel()
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
    override suspend fun saveSmobItem(smobItemATO: SmobShopATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB first
            val dbShop = smobItemATO.asDatabaseModel()
            smobShopDao.saveSmobItem(dbShop)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(networkConnectionManager.isNetworkConnected) {
                val testRead = getSmobShopViaApi(dbShop.itemId)
                if (testRead.data?.itemId != dbShop.itemId) {
                    // item not found in backend --> use POST to create it
                    saveSmobShopViaApi(dbShop)
                } else {
                    // item already exists in backend DB --> use PUT to update it
                    smobShopApi.updateSmobItemById(dbShop.itemId, dbShop.asNetworkModel())
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

                // first store in local DB first
                val dbShop = smobItemATO.asDatabaseModel()
                smobShopDao.updateSmobItem(dbShop)

                // then push to backend DB
                // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    val testRead = getSmobShopViaApi(dbShop.itemId)
                    if (testRead.data?.itemId != dbShop.itemId) {
                        // item not found in backend --> use POST to create it
                        saveSmobShopViaApi(dbShop)
                    } else {
                        // item already exists in backend DB --> use PUT to update it
                        smobShopApi.updateSmobItemById(dbShop.itemId, dbShop.asNetworkModel())
                    }
                }

            }
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
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all shops from local DB
                smobShopDao.deleteAllSmobItems()

                // then delete all shops from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobShopsViaApi().let {
                        if (it.status == Status.SUCCESS) {
                            it.data?.map { smobShopApi.deleteSmobItemById(it.itemId) }
                        } else {
                            Timber.w("Unable to get SmobShop IDs from backend DB (via API) - not deleting anything.")
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
            Timber.i("Sending GET request for SmobShop data...")
            val response: Resource<List<SmobShopDTO>> = getSmobShopsViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobShop data GET request complete (success)")

                // store shop data in DB - if any
                response.data?.let {
                    it.map { smobShopDao.saveSmobItem(it) }
                    Timber.i("SmobShop data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobShopsInDB()

    /**
     * Synchronize an individual smob shop in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobShop data...")
        val response: Resource<SmobShopDTO> = getSmobShopViaApi(id)

        // got back any valid data?
        if (response.status == Status.SUCCESS) {

            Timber.i("SmobShop data GET request complete (success)")


            // send POST request to server - coroutine to avoid blocking the main (UI) thread
            withContext(Dispatchers.IO) {

                // store shop data in DB - if any
                response.data?.let {
                    smobShopDao.saveSmobItem(it)
                    Timber.i("SmobShop data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

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
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<SmobShopDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobShopsFromApi


    // net-facing getter: a specific shop
    private suspend fun getSmobShopViaApi(id: String): Resource<SmobShopDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobShopDTO = SmobShopDTO()
        var result = Resource.loading(dummySmobShopDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobShopDTO = smobShopApi.getSmobItemById(id)
                    .body()
                    ?.asRepoModel()
                    ?: dummySmobShopDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobShopDTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

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
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobShopDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: update a specific (existing) group
    private suspend fun updateSmobShopViaApi(
        id: String,
        smobShopDTO: SmobShopDTO,
    ) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobShopApi.updateSmobItemById(id, smobShopDTO.asNetworkModel())
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobShopDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }


    // net-facing setter: delete a specific (existing) group
    private suspend fun deleteSmobShopViaApi(id: String) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobShopApi.deleteSmobItemById(id)
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<SmobShopDTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }

}