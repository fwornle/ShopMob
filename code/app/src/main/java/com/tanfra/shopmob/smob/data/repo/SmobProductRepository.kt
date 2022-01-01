package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobProductDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductSubCategory
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobProductApi
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
    override suspend fun getSmobProduct(id: String): Resource<SmobProductATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to update local DB for the requested SmobProduct
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshSmobProductInDB(id)

            // now try to fetch data from the local DB
            try {
                val smobProductDTO = smobProductDao.getSmobProductById(id)
                if (smobProductDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Resource.success(smobProductDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobProduct not found!", null)
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage, null)
            }
        }
    }

    /**
     * Get the smob product list from the local db
     * @return Result holds a Success with all the smob products or an Error object with the error message
     */
    override suspend fun getAllSmobProducts(): Resource<List<SmobProductATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first try to refresh SmobProduct data in local DB
            // ... note: currently, this is also scheduled by WorkManager every 60 seconds
            //     --> not essential to re-run this here...
            // ... if the API call fails, the local DB remains untouched
            //     --> app still works, as we only work of the data in the local DB
            refreshDataInLocalDB()

            // now try to fetch data from the local DB
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Resource.success(smobProductDao.getSmobProducts().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage, null)
            }
        }
    }

    /**
     * Insert a smob product in the db. Replace a potentially existing smob product record.
     * @param smobProductATO the smob product to be inserted
     */
    override suspend fun saveSmobProduct(smobProductATO: SmobProductATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbProduct = smobProductATO.asDatabaseModel()
                smobProductDao.saveSmobProduct(dbProduct)

                // then push to backend DB
                // ... use 'update', as product may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobProductDao.getSmobProductById(dbProduct.id)?.let { smobProductApi.updateSmobProduct(it.id, it.asNetworkModel()) }
                smobProductApi.updateSmobProductById(dbProduct.id, dbProduct.asNetworkModel())

            }
        }


    /**
     * Insert several smob products in the db. Replace any potentially existing smob u?ser record.
     * @param smobProductsATO a list of smob products to be inserted
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
     * @param smobProductsATO the list of smob products to be updated
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
                    if (it.status.equals(Status.SUCCESS)) {
                        it.data?.map { smobProductApi.deleteSmobProductById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobProduct IDs from backend DB (via API) - not deleting anything.")
                    }
                }
            }

        }  // context: ioDispatcher
    }


    // TODO: should loop over all product pictures and move them to local storage
    // TODO: make refresSmogProductDataInDB sensitive to Product data relevant to this product only

    /**
     * Synchronize all smob products in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // set initial status
        _statusSmobProductDataSync.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobProduct data...")
            val response: Resource<List<SmobProductDTO>> = getSmobProductsViaApi()

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                // set status to keep UI updated
                _statusSmobProductDataSync.postValue(Status.SUCCESS)
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
    suspend fun refreshSmobProductInDB(id: String) {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobProduct data...")
            val response: Resource<SmobProductDTO> = getSmobProductViaApi(id)

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                Timber.i("SmobProduct data GET request complete (success)")

                // store product data in DB - if any
                response.data?.let {
                    smobProductDao.saveSmobProduct(it)
                    Timber.i("SmobProduct data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobProductInDB()


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

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        var result = Resource.loading(listOf<SmobProductDTO>())

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobProductApi.getSmobProducts()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobProductDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobProductsFromApi


    // net-facing getter: a specific product
    private suspend fun getSmobProductViaApi(id: String): Resource<SmobProductDTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        val dummySmobProductDTO = SmobProductDTO(
            "DUMMY",
            "",
            "",
            "",
            ProductMainCategory.OTHER,
            ProductSubCategory.OTHER,
            "",
            -1,
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

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobProductApi.saveSmobProduct(smobProductDTO.asNetworkModel())
        }

    }  // saveSmobProductToApi


    // net-facing setter: update a specific (existing) product
    private suspend fun updateSmobProductViaApi(
        id: String,
        smobProductDTO: SmobProductDTO,
    ) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobProductApi.updateSmobProductById(id, smobProductDTO.asNetworkModel())
        }

    }  // updateSmobProductToApi


    // net-facing setter: delete a specific (existing) product
    private suspend fun deleteSmobProductViaApi(id: String) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobProductApi.deleteSmobProductById(id)
        }

    }  // deleteSmobProductToApi




    // TODO: move this to viewModel?? replace LiveData by Flow??
    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobProductDataSync = MutableLiveData<Status>()
    val statusSmobProductDataSync: LiveData<Status>
        get() = _statusSmobProductDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values which are set using 'postValue', in
        // case the repository class is initialized from within a background task, e.g. when using
        // WorkManager to schedule a background update (and this happens to be the first access of
        // a repository service)
        // ... omitting proper initialization of LD can cause ('obscure') crashes
        //     - ... e.g. when Android calls the LD observer (to update the UI) and the
        //       BindingAdapter tries to de-reference a null pointer (invalid LD)
        _statusSmobProductDataSync.postValue(Status.SUCCESS)

    }

}