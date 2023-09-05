package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.local.dataSource.SmobItemLocalDataSource
import com.tanfra.shopmob.smob.data.local.dto.Dto
import com.tanfra.shopmob.smob.data.local.dto2ato._asDomainModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemRepository
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import com.tanfra.shopmob.smob.data.net.dataSource.SmobItemRemoteDataSource
import com.tanfra.shopmob.smob.data.net.nto.Nto
import com.tanfra.shopmob.smob.data.local.dto2ato._asDatabaseModel
import com.tanfra.shopmob.smob.data.net.nto2dto._asNetworkModel
import com.tanfra.shopmob.smob.data.net.nto2dto._asRepoModel
import com.tanfra.shopmob.smob.data.net.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.ato.Ato
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
 * @param smobItemDao the dao that does the Room db operations for table smobItems
 * @param smobItemApi the api that does the network operations for table smobItems
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
open class SmobItemRepository<DTO: Dto, NTO: Nto, ATO: Ato>(
    private val smobItemDao: SmobItemLocalDataSource<DTO>,
    private val smobItemApi: SmobItemRemoteDataSource<NTO>,
    private val dummySmobItemDTO: DTO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SmobItemRepository<ATO>, KoinComponent {

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // --- impl. of public, app facing data interface 'SmobItemDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobItemDataSource': CRUD, local DB data ---
    // --- impl. of public, app facing data interface 'SmobItemDataSource': CRUD, local DB data ---

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobItem or an Error object with the error message
     */
    override fun getSmobItem(id: String): Flow<Resource<ATO>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<ATO?> = flowOf(null)
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobItemDao.getSmobItemById(id)._asDomainModel(dummySmobItemDTO)
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }

    /**
     * Get the smob item item from the local db
     * @return Result holds a Success with all the smob groups or an Error object with the error message
     */
    override fun getAllSmobItems(): Flow<Resource<List<ATO>>> {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // try to fetch data from the local DB
            var atoFlow: Flow<List<ATO>> = flowOf(listOf())
            return try {
                // fetch data from DB (and convert to ATO)
                atoFlow = smobItemDao.getSmobItems()._asDomainModel(dummySmobItemDTO)
                // wrap data in Resource (--> error/success/[loading])
                atoFlow.asResource(null)
            } catch (e: Exception) {
                // handle exceptions --> error message returned in Resource.error
                atoFlow.asResource(e.localizedMessage)
            }

        }  // idlingResource (testing)

    }


    /**
     * Insert a smob item in the db. Replace a potentially existing smob item record.
     * @param smobItemATO the smob item to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: ATO): Unit = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // first store in local DB first
            smobItemATO._asDatabaseModel(dummySmobItemDTO)
                .also {

                smobItemDao.saveSmobItem(it)

                // then push to backend DB
                // ... PUT or POST? --> try a GET first to find out if item already exists in backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    val testRead = getSmobItemViaApi(it.id)
                    if (testRead.data?.id != it.id) {
                        // item not found in backend --> use POST to create it
                        saveSmobItemViaApi(it)
                    } else {
                        // item already exists in backend DB --> use PUT to update it
                        smobItemApi.updateSmobItemById(it.id, it._asNetworkModel(dummySmobItemDTO))
                    }
                }

            }

        }  // idlingResource (testing)

    }


    /**
     * Insert several smob groups in the db. Replace any potentially existing smob u?ser record.
     * @param smobItemsATO a item of smob groups to be inserted
     */
    override suspend fun saveSmobItems(smobItemsATO: List<ATO>) {
        // store all provided smob groups by repeatedly calling upon saveSmobItem
        withContext(ioDispatcher) {
            smobItemsATO.map { saveSmobItem(it) }
        }
    }

    /**
     * Update an existing smob item in the db. Do nothing, if the smob item does not exist.
     * @param smobItemATO the smob item to be updated
     */
    override suspend fun updateSmobItem(smobItemATO: ATO): Unit =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first store in local DB first
                smobItemATO._asDatabaseModel(dummySmobItemDTO)
                    .also {
                        smobItemDao.updateSmobItem(it)

                        // then push to backend DB
                        // ... use 'update', as item may already exist (equivalent of REPLACE w/h local DB)
                        if(networkConnectionManager.isNetworkConnected) {
                            smobItemApi.updateSmobItemById(it.id, it._asNetworkModel(dummySmobItemDTO))
                    }

                }

            }
        }

    /**
     * Update an set of existing smob groups in the db. Ignore smob groups which do not exist.
     * @param smobItemsATO the item of smob groups to be updated
     */
    override suspend fun updateSmobItems(smobItemsATO: List<ATO>) {
        // update all provided smob groups by repeatedly calling upon updateSmobItem
        withContext(ioDispatcher) {
            smobItemsATO.map { updateSmobItem(it) }
        }
    }

    /**
     * Delete a smob item in the db
     * @param id ID of the smob item to be deleted
     */
    override suspend fun deleteSmobItem(id: String) {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobItemDao.deleteSmobItemById(id)
                if(networkConnectionManager.isNetworkConnected) {
                    smobItemApi.deleteSmobItemById(id)
                }
            }
        }
    }

    /**
     * Deletes all the smob groups in the db
     */
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {

                // first delete all groups from local DB
                smobItemDao.deleteAllSmobItems()

                // then delete all groups from backend DB
                if(networkConnectionManager.isNetworkConnected) {
                    getSmobItemsViaApi().let { resList ->
                        if (resList.status == Status.SUCCESS) {
                            resList.data?.map { smobItemApi.deleteSmobItemById(it!!.id) }
                        } else {
                            Timber.w("Unable to get SmobItem IDs from backend DB (via API) - not deleting anything.")
                        }
                    }
                }
            }

        }  // context: ioDispatcher
    }


    /**
     * Synchronize all smob groups in the db by retrieval from the backend using the (net) API
     */
    override suspend fun refreshDataInLocalDB() {

        // send GET request to server - coroutine to avoid blocking the main (UI) thread
        withContext(Dispatchers.IO) {

            // initiate the (HTTP) GET request using the provided query parameters
            Timber.i("Sending GET request for SmobItem data...")
            val response: Resource<List<DTO?>> = getSmobItemsViaApi()

            // got any valid data back?
            if (response.status == Status.SUCCESS) {

                // set status to keep UI updated
                Timber.i("SmobItem data GET request complete (success)")

                // store item data in DB - if any
                response.data?.let {
                    it.map { smobItemDao.saveSmobItem(it!!) }
                    Timber.i("SmobItem data items stored in local DB")
                }

            }  // if (valid response)

        }  // coroutine scope (IO)

    }  // refreshSmobItemsInDB()

    /**
     * Synchronize an individual smob groups in the db by retrieval from the backend DB (API call)
     */
    override suspend fun refreshSmobItemInLocalDB(id: String) {

        // initiate the (HTTP) GET request using the provided query parameters
        Timber.i("Sending GET request for SmobItem data...")
        val response: Resource<DTO> = getSmobItemViaApi(id)

        // got back any valid data?
        if (response.status == Status.SUCCESS) {

            Timber.i("SmobItem data GET request complete (success)")


            // send POST request to server - coroutine to avoid blocking the main (UI) thread
            withContext(Dispatchers.IO) {

                // store item data in DB - if any
                response.data?.let {
                    smobItemDao.saveSmobItem(it)
                    Timber.i("SmobItem data items stored in local DB")
                }

            }  // coroutine scope (IO)

        }  // if (valid response)

    }  // refreshSmobItemInLocalDB()



    // --- use : CRUD, NET data ---
    // --- overrides of general data interface 'SmobItemDataSource': CRUD, NET data ---
    // --- overrides of general data interface 'SmobItemDataSource': CRUD, NET data ---


    // get singleton instance of network response handler from (Koin) service locator
    // ... so that we don't have to get a separate instance in every repository
    private val responseHandler: ResponseHandler by inject()

    // net-facing getter: all groups
    // ... wrap in Resource (as opposed to Result - see above) to also provide "loading" state
    // ... note: no 'override', as this is not exposed in the repository interface (network access
    //           is fully abstracted by the repo - all data access done via local DB)
    private suspend fun getSmobItemsViaApi(): Resource<List<DTO?>> = withContext(ioDispatcher) {

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            return@withContext try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult = smobItemApi.getSmobItems()
                    .getOrNull()
                    ?._asRepoModel(dummySmobItemDTO)
                    ?: listOf()  // GET request returned empty handed --> return empty list

                // return as successfully completed GET call to the backend
                // --> wraps data in Response type (success/error/loading)
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception
                // --> handle it... wraps data in Response type (success/error/loading)
                val daException = responseHandler.handleException<ArrayList<DTO>>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

    }  // getSmobItemsFromApi


    // net-facing getter: a specific item
    private suspend fun getSmobItemViaApi(id: String): Resource<DTO> = withContext(ioDispatcher) {

        // overall result - haven't got anything yet
        // ... this is useless here --> but needs to be done like this in the viewModel
//        val dummySmobItemDTO = DTO(
//            "DUMMY",
//            ItemStatus.NEW,
//            -1L,
//            "",
//            "",
//            ItemType.OTHER,
//            listOf(),
//            "",
//            0,
//        )
        var result = Resource.loading(dummySmobItemDTO)

        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {

            // network access - could fail --> handle consistently via ResponseHandler class
            result = try {
                // return successfully received data object (from Moshi --> PoJo)
                val netResult: DTO = smobItemApi.getSmobItemById(id)
                    .getOrNull()
                    ?._asRepoModel(dummySmobItemDTO)
                    ?: dummySmobItemDTO

                // return as successfully completed GET call to the backend
                responseHandler.handleSuccess(netResult)

            } catch (ex: Exception) {

                // return with exception --> handle it...
                val daException = responseHandler.handleException<DTO>(ex)

                // local logging
                Timber.e(daException.message)

                // return handled exception
                daException

            }

        }  // espresso: idlingResource

        return@withContext result

    }  // getSmobItemFromApi


    // net-facing setter: save a specific (new) item
    private suspend fun saveSmobItemViaApi(smobItemDTO: DTO) = withContext(ioDispatcher) {
        // network access - could fail --> handle consistently via ResponseHandler class
        try {
            // return successfully received data object (from Moshi --> PoJo)
            smobItemApi.saveSmobItem(smobItemDTO._asNetworkModel(dummySmobItemDTO))
        } catch (ex: Exception) {
            // return with exception --> handle it...
            val daException = responseHandler.handleException<DTO>(ex)
            // local logging
            Timber.e(daException.message)
        }
    }

}