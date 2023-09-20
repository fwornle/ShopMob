package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.dataSource.SmobProductLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.remote.dataSource.SmobProductRemoteDataSource
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
 * @param smobProductDao data source for CRUD operations in local DB for table smobProducts
 * @param smobProductApi data source for network based access to remote table smobProducts
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobProductRepository(
    private val smobProductDao: SmobProductLocalDataSource,
    private val smobProductApi: SmobProductRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobProductRepository, KoinComponent {

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---

    /**
     * Get a smob product by its id
     * @param id to be used to get the smob product
     * @return Result the holds a Success object with the SmobProduct or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<SmobProductATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobProductDao.getSmobItemById(id)
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()

        }  // idlingResource (testing)

    }

    /**
     * Get the smob product from the local db
     * @return Result holds a Success with all the smob products or an Error object with the error message
     */
    override fun getSmobItems(): Flow<Resource<List<SmobProductATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            return smobProductDao.getSmobItems()
                .catch { ex -> Resource.Failure(Exception(ex.localizedMessage)) }
                .asDomainModel()
                .asResource()  // always Resource.Success

        }  // idlingResource (testing)

    }


    /**
     * Get the smob product of a particular smob list from the local db
     * @param id of the smob list
     * @return Result holds a Success with all the smob products or an Error object with the error message
     */
    override fun getSmobProductsByListId(id: String): Flow<Resource<List<SmobProductATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobProductATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobProductDao.getSmobProductsByListId(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource()
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.Error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob shop in the db. Replace a potentially existing smob product record.
     * @param smobItemATO the smob product to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobProductATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB
            val dbProduct = smobItemATO.asDatabaseModel()
            smobProductDao.saveSmobItem(dbProduct)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            if(networkConnectionManager.isNetworkConnected) {
                getSmobProductViaApi(dbProduct.id).let {
                    when (it) {
                        is Resource.Failure -> Timber.i("Couldn't retrieve SmobProduct from remote")
                        is Resource.Empty -> Timber.i("SmobProduct still loading")
                        is Resource.Success -> {
                            if (it.data.id != dbProduct.id) {
                                // item not found in backend --> use POST to create it
                                saveSmobProductViaApi(dbProduct)
                            } else {
                                // item already exists in backend DB --> use PUT to update it
                                smobProductApi.updateSmobItemById(dbProduct.id, dbProduct.asNetworkModel())
                            }
                        }
                    }
                }
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob products in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a product of smob products to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<SmobProductATO>) {
        // store all provided smob products by repeatedly calling upon saveSmobProduct
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob product in the db. Do nothing, if the smob product does not exist.
     * @param smobItemATO the smob product to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: SmobProductATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB
                val dbProduct = smobItemATO.asDatabaseModel()
                smobProductDao.updateSmobItem(dbProduct)

                // then push to backend DB
                // ... use 'update', as product may already exist (equivalent of REPLACE w/h local DB)
                if(networkConnectionManager.isNetworkConnected) {
                    smobProductApi.updateSmobItemById(dbProduct.id, dbProduct.asNetworkModel())
                }

            }
        }

    /**
     * Update an set of existing smob products in the db. Ignore smob products which do not exist.
     * @param smobItemsATO the product of smob products to be updated
     */
    override suspend fun updateSmobItems(smobItemsATO: List<SmobProductATO>) {
        // update all provided smob products by repeatedly calling upon updateSmobProduct
        withContext(ioDispatcher) {
            smobItemsATO.map { updateSmobItem(it) }
        }
    }

    /**
     * Delete a smob product in the db
     * @param id ID of the smob product to be deleted
     */
    override suspend fun deleteSmobItem(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobProductDao.deleteSmobItemById(id)
                if(networkConnectionManager.isNetworkConnected) {
                    smobProductApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob products in the db
     */
    override suspend fun deleteSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all products from local DB
                smobProductDao.deleteSmobItems()

                // then delete all products from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobProductsViaApi().let {
                        when (it) {
                            is Resource.Failure -> Timber.i("Couldn't retrieve SmobProduct from remote")
                            is Resource.Empty -> Timber.i("SmobProduct still loading")
                            is Resource.Success -> {
                                it.data.map { item -> smobProductApi.deleteSmobItemById(item.id) }
                            }
                        }
                    }
                }
                
            }  // wrapEspressoIdlingResource

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob products in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshItemsInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobProduct data...")

            // use async/await here to avoid premature "null" result of smobXyzApi.getSmobItems()
            getSmobProductsViaApi().let {
                when (it) {
                    is Resource.Failure -> Timber.i("Couldn't retrieve SmobProduct from remote")
                    is Resource.Empty -> Timber.i("SmobProduct still loading")
                    is Resource.Success -> {
                        Timber.i("SmobProduct data GET request complete (success)")

                        // store product data in DB - if any
                        it.data.let { daList ->
                            // delete current table from local DB (= clear local cache)
                            Timber.i("Deleting all SmobProduct data from local DB")
                            smobProductDao.deleteSmobItems()
                            Timber.i("Local DB table empty")

                            Timber.i("Storing newly retrieved data in local DB")
                            daList.map { item -> smobProductDao.saveSmobItem(item) }
                            Timber.i("All SmobProduct data items stored in local DB")
                        }
                    }
                }
            }
            
        }  // coroutine scope (IO)

    }  // refreshSmobProductsInDB()

    /**
     * Synchronize an individual smob product in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobProduct data...")
        getSmobProductViaApi(id).let {
            when (it) {
                is Resource.Failure -> Timber.i("Couldn't retrieve SmobProduct from remote")
                is Resource.Empty -> Timber.i("SmobProduct still loading")
                is Resource.Success -> {
                    Timber.i("SmobProduct data GET request complete (success)")

                    // send POST request to server - coroutine to avoid blocking the main (UI) thread
                    withContext(Dispatchers.IO) {

                        // store group data in DB - if any
                        it.data.let { daProduct ->
                            smobProductDao.saveSmobItem(daProduct)
                            Timber.i("SmobProduct data items stored in local DB")
                        }

                    }  // coroutine scope (IO)
                }
            }
        }

    }  // refreshSmobProductInLocalDB()


    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobProductDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobProductDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all products
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobProductsViaApi(): Resource<List<SmobProductDTO>> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobProductApi.getSmobItems()
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
                responseHandler.handleException<ArrayList<SmobProductDTO>>(ex)

            }

        }  // espresso: idlingResource

    }  // getSmobProductsFromApi


    // net-facing getter: a specific product
    private suspend fun getSmobProductViaApi(id: String): Resource<SmobProductDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobProductDTO = SmobProductDTO()
        var result: Resource<SmobProductDTO> = Resource.Empty

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobProductDTO = smobProductApi.getSmobItemById(id)
                    .getOrNull()
                    ?.asRepoModel()
                    ?: dummySmobProductDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // local logging
                Timber.e(ex.message)
                // return with exception --> handle it...
                responseHandler.handleException<SmobProductDTO>(ex)

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobProductFromApi


    // net-facing setter: save a specific (new) group
    private suspend fun saveSmobProductViaApi(smobProductDTO: SmobProductDTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobProductApi.saveSmobItem(smobProductDTO.asNetworkModel())
        } catch (ex: Exception) {
            Timber.e(ex.message)
            responseHandler.handleException<SmobProductDTO>(ex)
        }
    }


}