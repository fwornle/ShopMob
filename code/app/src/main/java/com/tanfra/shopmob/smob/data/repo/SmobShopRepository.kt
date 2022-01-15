package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobShopDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.data.local.utils.ShopType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobShopApi
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
import java.lang.Double.NaN
import java.util.*
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


    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobShopDataSource': CRUD, local DB data ---

    /**
     * Get a smob shop by its id
     * @param id to be used to get the smob shop
     * @return Result the holds a Success object with the SmobShop or an Error object with the error message
     */
    override fun getSmobShop(id: String): Flow<Resource<SmobShopATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<SmobShopATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobShopDao.getSmobShopById(id).asDomainModel()
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob shop shop from the local db
     * @return Result holds a Success with all the smob shops or an Error object with the error message
     */
    override fun getAllSmobShops(): Flow<Resource<List<SmobShopATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<SmobShopATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobShopDao.getSmobShops().asDomainModel()
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
     * @param smobShopATO the smob shop to be inserted
     */
    override suspend fun saveSmobShop(smobShopATO: SmobShopATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB first
            val dbShop = smobShopATO.asDatabaseModel()
            smobShopDao.saveSmobShop(dbShop)

            // then push to backend DB
            // ... use 'update', as shop may already exist (equivalent of REPLACE w/h local DB)
            //
            // ... could do a read back first, if we're anxious...
            //smobShopDao.getSmobShopById(dbShop.id)?.let { smobShopApi.updateSmobShop(it.id, it.asNetworkModel()) }
            smobShopApi.updateSmobShopById(dbShop.id, dbShop.asNetworkModel())

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob shops in the db. Replace any potentially existing smob u?ser record.
     * @param smobShopsATO a shop of smob shops to be inserted
     */
    override suspend fun saveSmobShops(smobShopsATO: List<SmobShopATO>) {
        // store all provided smob shops by repeatedly calling upon saveSmobShop
        withContext(ioDispatcher) {
            smobShopsATO.map { saveSmobShop(it) }
        }
    }

    /**
     * Update an existing smob shop in the db. Do nothing, if the smob shop does not exist.
     * @param smobShopATO the smob shop to be updated
     */
    override suspend fun updateSmobShop(smobShopATO: SmobShopATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                val dbShop = smobShopATO.asDatabaseModel()
                smobShopDao.updateSmobShop(dbShop)

                // then push to backend DB
                // ... use 'update', as shop may already exist (equivalent of REPLACE w/h local DB)
                //
                // ... could do a read back first, if we're anxious...
                //smobShopDao.getSmobShopById(dbShop.id)?.let { smobShopApi.updateSmobShop(it.id, it.asNetworkModel()) }
                smobShopApi.updateSmobShopById(dbShop.id, dbShop.asNetworkModel())

            }
        }

    /**
     * Update an set of existing smob shops in the db. Ignore smob shops which do not exist.
     * @param smobShopsATO the shop of smob shops to be updated
     */
    override suspend fun updateSmobShops(smobShopsATO: List<SmobShopATO>) {
        // update all provided smob shops by repeatedly calling upon updateSmobShop
        withContext(ioDispatcher) {
            smobShopsATO.map { updateSmobShop(it) }
        }
    }

    /**
     * Delete a smob shop in the db
     * @param id ID of the smob shop to be deleted
     */
    override suspend fun deleteSmobShop(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobShopDao.deleteSmobShopById(id)
                smobShopApi.deleteSmobShopById(id)
            }
        }
    }

    /**
     * Deletes all the smob shops in the db
     */
    override suspend fun deleteAllSmobShops() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all shops from local DB
                smobShopDao.deleteAllSmobShops()

                // then delete all shops from backend DB
                getSmobShopsViaApi().let {
                    if (it.status == Status.SUCCESS) {
                        it.data?.map { smobShopApi.deleteSmobShopById(it.id) }
                    } else {
                        Timber.w("Unable to get SmobShop IDs from backend DB (via API) - not deleting anything.")
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
                    it.map { smobShopDao.saveSmobShop(it) }
                    Timber.i("SmobShop data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobShopsInDB()

    /**
     * Synchronize an individual smob shops in the db by retrieval from the backend DB (API call)
     */
    suspend fun refreshSmobShopInLocalDB(id: String) {

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
                    smobShopDao.saveSmobShop(it)
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
                val netResult = smobShopApi.getSmobShops()
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
        val dummySmobShopDTO = SmobShopDTO(
            "DUMMY",
            SmobItemStatus.NEW,
            -1L,
            "",
            "",
            "",
            NaN,
            NaN,
            ShopType.INDIVIDUAL,
            ShopCategory.OTHER,
            listOf()
        )
        var result = Resource.loading(dummySmobShopDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: SmobShopDTO = smobShopApi.getSmobShopById(id)
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


    // net-facing setter: save a specific (new) shop
    private suspend fun saveSmobShopViaApi(smobShopDTO: SmobShopDTO) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobShopApi.saveSmobShop(smobShopDTO.asNetworkModel())
        }

    }  // saveSmobShopToApi


    // net-facing setter: update a specific (existing) shop
    private suspend fun updateSmobShopViaApi(
        id: String,
        smobShopDTO: SmobShopDTO,
    ) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobShopApi.updateSmobShopById(id, smobShopDTO.asNetworkModel())
        }

    }  // updateSmobShopToApi


    // net-facing setter: delete a specific (existing) shop
    private suspend fun deleteSmobShopViaApi(id: String) = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            smobShopApi.deleteSmobShopById(id)
        }

    }  // deleteSmobShopToApi

}