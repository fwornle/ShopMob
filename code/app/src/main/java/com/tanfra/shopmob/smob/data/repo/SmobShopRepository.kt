package com.tanfra.shopmob.smob.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobShopDao
import com.tanfra.shopmob.smob.data.local.dto2ato.asDatabaseModel
import com.tanfra.shopmob.smob.data.local.dto2ato.asDomainModel
import com.tanfra.shopmob.smob.data.net.ResponseHandler
import com.tanfra.shopmob.smob.data.net.api.SmobShopApi
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
     * Get the smob shop list from the local db
     * @return Result holds a Success with all the smob shops or an Error object with the error message
     */
    override suspend fun getAllSmobShops(): Resource<List<SmobShopATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                // success --> turn DB data type (DTO) to domain data type
                Resource.success(smobShopDao.getSmobShops().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage, null)
            }
        }
    }

    /**
     * Insert a smob shop in the db. Replace a potentially existing smob shop record.
     * @param smobShopATO the smob shop to be inserted
     */
    override suspend fun saveSmobShop(smobShopATO: SmobShopATO) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobShopDao.saveSmobShop(smobShopATO.asDatabaseModel())
            }
        }


    /**
     * Insert several smob shops in the db. Replace any potentially existing smob shop record.
     * @param smobShopsATO a list of smob shops to be inserted
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
    override suspend fun updateSmobShop(smobShopATO: SmobShopATO) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobShopDao.updateSmobShop(smobShopATO.asDatabaseModel())
            }
        }

    /**
     * Update an set of existing smob shops in the db. Ignore smob shops which do not exist.
     * @param smobShopsATO the list of smob shops to be updated
     */
    override suspend fun updateSmobShops(smobShopsATO: List<SmobShopATO>) {
        // update all provided smob shops by repeatedly calling upon updateSmobShop
        withContext(ioDispatcher) {
            smobShopsATO.map { updateSmobShop(it) }
        }
    }

    /**
     * Get a smob shop by its id
     * @param id to be used to get the smob shop
     * @return Result the holds a Success object with the SmobShop or an Error object with the error message
     */
    override suspend fun getSmobShop(id: String): Resource<SmobShopATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobShopDTO = smobShopDao.getSmobShopById(id)
                if (smobShopDTO != null) {
                    // success --> turn DB data type (DTO) to domain data type
                    return@withContext Resource.success(smobShopDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobShop not found!", null)
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage, null)
            }
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
                smobShopDao.deleteAllSmobShops()
            }
        }
    }


    // TODO: should loop over all shop pictures and move them to local storage
    // TODO: make refresSmogShopDataInDB sensitive to Shop data relevant to this shop only

    /**
     * Synchronize all smob shops in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshSmobShopDataInDB() {

        // set initial status
        _statusSmobShopDataSync.postValue(Status.LOADING)

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobShop data...")
            val response: Resource<List<SmobShopDTO>> = getSmobShopsViaApi()

            // smoke test for net-CRUD (quick workaround... to avoid having to set up proper tests)
//            val response2: SmobShopDTO? = getSmobShopFromApi("07c295ad-b286-41f7-b2ea-e81a75875d02")
//            Timber.i(response2?.toString())
//
//            val testTxShop: SmobShopDTO = SmobShopDTO(
//                shopname = "maMu",
//                name = "Max Mustermann",
//                email = "max@mustermann.org",
//                imageUrl = null
//            )
//            saveSmobShopViaApi(testTxShop)
//
//            // read back 'first' max mustermann
//            testTxShop.imageUrl = Date().time.toString()
//            updateSmobShopViaApi(
//                "0794b744-7fa4-4440-b284-8c72012ed6cf",
//                testTxShop
//            )
//
//            deleteSmobShopViaApi("1bbd2da1-e028-4e42-b6b8-7d944013abca")

            // got any valid data back?
            if (response.status.equals(Status.SUCCESS)) {

                // set status to keep UI updated
                _statusSmobShopDataSync.postValue(Status.SUCCESS)
                Timber.i("SmobShop data GET request complete (success)")

                // store shop data in DB - if any
                response.data?.let {
                    it.map { smobShopDao.saveSmobShop(it) }
                    Timber.i("SmobShop data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobShopDataInDB()



    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobShopDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobShopDataSource': CRUD, NET data ---

    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all shops
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" status
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobShopsViaApi(): Resource<List<SmobShopDTO>> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
        var result = Resource.loading(listOf<SmobShopDTO>())

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobShopApi.getSmobShops()
                    .body()
                    ?.asRepoModel()
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<ArrayList<SmobShopDTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobShopsFromApi


    // net-facing getter: a specific shop
    private suspend fun getSmobShopViaApi(id: String): SmobShopDTO? = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                responseHandler.handleSuccess(smobShopApi.getSmobShopById(id))
                    .data
                    ?.body()
                    ?.asRepoModel()    // returns 'null' if requested shop cannot be found

            } catch (ex: Exception) {
                // return with exception --> handle it...
                val daException = responseHandler.handleException<SmobShopDTO>(ex)
                Timber.e(daException.message)

                // ... then return empty handed
                null
            }

        }  // espresso: idlingResource

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



    // LiveData for storing the status of the most recent RESTful API request
    private val _statusSmobShopDataSync = MutableLiveData<Status>()
    val statusNetApiSmobShopDataSync: LiveData<Status>
        get() = _statusSmobShopDataSync


    // upon instantiating the repository class...
    init {

        // make sure all LiveData elements have defined values which are set using 'postValue', in
        // case the repository class is initialized from within a background task, e.g. when using
        // WorkManager to schedule a background update (and this happens to be the first access of
        // a repository service)
        // ... omitting proper initialization of LD can cause ('obscure') crashes
        //     - ... e.g. when Android calls the LD observer (to update the UI) and the
        //       BindingAdapter tries to de-reference a null pointer (invalid LD)
        _statusSmobShopDataSync.postValue(Status.SUCCESS)

    }

}