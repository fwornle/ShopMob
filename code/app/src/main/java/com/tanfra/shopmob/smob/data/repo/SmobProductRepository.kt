package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobProductDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductSubCategory
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobProductApi
import com.tanfra.shopmob.smob.data.net.nto2dto.asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto.asRepoModel
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
import java.util.*
import kotlin.collections.ArrayList


/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobProductDao the dao that does the Room db operations for table smobProducts
 * @param smobProductApi the api that does the network operations for table smobProducts
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobProductRepository(
    private val smobProductDao: SmobProductDao,
    private val smobProductApi: SmobProductApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobProductDataSource, KoinComponent {


    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobProductDataSource': CRUD, local DB data ---

    /**
     * Get a smob product by its id
     * @param id to be used to get the smob product
     * @return Result the holds a Success object with the SmobProduct or an Error object with the error message
     */
    override fun getSmobProduct(id: String): Flow<Resource<SmobProductATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<SmobProductATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobProductDao.getSmobProductById(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob product from the local db
     * @return Result holds a Success with all the smob products or an Error object with the error message
     */
    override fun getAllSmobProducts(): Flow<Resource<List<SmobProductATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobProductATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobProductDao.getSmobProducts().asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

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
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob shop in the db. Replace a potentially existing smob product record.
     * @param smobProductATO the smob product to be inserted
     */
    override suspend fun saveSmobProduct(smobProductATO: SmobProductATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB first
            val dbProduct = smobProductATO.asDatabaseModel()
            smobProductDao.saveSmobProduct(dbProduct)

            // then push to backend DB
            // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
            val testRead = getSmobProductViaApi(dbProduct.id)
            if (testRead.data?.id != dbProduct.id) {
                // item not found in backend --> use POST to create it
                saveSmobProductViaApi(dbProduct)
            } else {
                // item already exists in backend DB --> use PUT to update it
                smobProductApi.updateSmobProductById(dbProduct.id, dbProduct.asNetworkModel())
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob products in the db. Replace any potentially existing smob u?ser record.
     * @param smobProductsATO a product of smob products to be inserted
     */
    override suspend fun saveSmobProducts(smobProductsATO: List<SmobProductATO>) {
        // store all provided smob products by repeatedly calling upon saveSmobProduct
        withContext(ioDispatcher) {
            smobProductsATO.map { saveSmobProduct(it) }
        }
    }

    /**
     * Update an existing smob product in the db. Do nothing, if the smob product does not exist.
     * @param smobProductATO the smob product to be updated
     */
    override suspend fun updateSmobProduct(smobProductATO: SmobProductATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbProduct = smobProductATO.asDatabaseModel()
                smobProductDao.updateSmobProduct(dbProduct)

                // then push to backend DB
                // ... use 'update', as product may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobProductDao.getSmobProductById(dbProduct.id)?.let { smobProductApi.updateSmobProduct(it.id, it.asNetworkModel()) }
                smobProductApi.updateSmobProductById(dbProduct.id, dbProduct.asNetworkModel())

            }
        }

    /**
     * Update an set of existing smob products in the db. Ignore smob products which do not exist.
     * @param smobProductsATO the product of smob products to be updated
     */
    override suspend fun updateSmobProducts(smobProductsATO: List<SmobProductATO>) {
        // update all provided smob products by repeatedly calling upon updateSmobProduct
        withContext(ioDispatcher) {
            smobProductsATO.map { updateSmobProduct(it) }
        }
    }

    /**
     * Delete a smob product in the db
     * @param id ID of the smob product to be deleted
     */
    override suspend fun deleteSmobProduct(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobProductDao.deleteSmobProductById(id)
                smobProductApi.deleteSmobProductById(id)
            }
        }
    }

    /**
     * Deletes all the smob products in the db
     */
    override suspend fun deleteAllSmobProducts() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all products from local DB
                smobProductDao.deleteAllSmobProducts()

                // then delete all products from backend DB
                getSmobProductsViaApi().let {
                    if (it.status == Status.SUCCESS) {
                        it.data?.map { smobProductApi.deleteSmobProductById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobProduct IDs from backend DB (via API) - not deleting anything.")
                    }
                }
            }

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob products in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobProduct data...")
            val response: Resource<List<SmobProductDTO>> = getSmobProductsViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobProduct data GET request complete (success)")

                // store product data in DB - if any
                response.data?.let {
                    it.map { smobProductDao.saveSmobProduct(it) }
                    Timber.i("SmobProduct data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobProductsInDB()

    /**
     * Synchronize an individual smob products in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobProductInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobProduct data...")
        val response: Resource<SmobProductDTO> = getSmobProductViaApi(id)

        // got back any valid data?
        if (response.status == Status.SUCCESS) {

            Timber.i("SmobProduct data GET request complete (success)")


            // send POST request to server - coroutine to avoid blocking the main (UI) thread
            withContext(Dispatchers.IO) {

                // store product data in DB - if any
                response.data?.let {
                    smobProductDao.saveSmobProduct(it)
                    Timber.i("SmobProduct data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

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
                val netResult = smobProductApi.getSmobProducts()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<SmobProductDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobProductsFromApi


    // net-facing getter: a specific product
    private suspend fun getSmobProductViaApi(id: String): Resource<SmobProductDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobProductDTO = SmobProductDTO(
            "DUMMY",
            SmobItemStatus.NEW,
            -1L,
            "",
            "",
            "",
            ProductMainCategory.OTHER,
            ProductSubCategory.OTHER,
            "",
            0,
        )
        var result = Resource.loading(dummySmobProductDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobProductDTO = smobProductApi.getSmobProductById(id)
                    .body()
                    ?.asRepoModel()
                    ?: dummySmobProductDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobProductDTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobProductFromApi


    // net-facing setter: save a specific (new) product
    private suspend fun saveSmobProductViaApi(smobProductDTO: SmobProductDTO) = withContext(ioDispatcher) {
        smobProductApi.saveSmobProduct(smobProductDTO.asNetworkModel())
    }


    // net-facing setter: update a specific (existing) product
    private suspend fun updateSmobProductViaApi(
        id: String,
        smobProductDTO: SmobProductDTO,
    ) = withContext(ioDispatcher) {
        smobProductApi.updateSmobProductById(id, smobProductDTO.asNetworkModel())
    }


    // net-facing setter: delete a specific (existing) product
    private suspend fun deleteSmobProductViaApi(id: String) = withContext(ioDispatcher) {
        smobProductApi.deleteSmobProductById(id)
    }

}