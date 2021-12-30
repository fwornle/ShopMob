package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.local.dao.SmobItemDao
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobItemDao the dao that does the Room db operations for table smobItems
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobItemRepository(
    private val smobItemDao: SmobItemDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobItemDataSource {

    /**
     * Get the smob item list from the local db
     * @return Result the holds a Success with all the smob items or an Error object with the error message
     */
    override suspend fun getSmobItems(): Resource<List<SmobItemATO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Resource.success(smobItemDao.getSmobItems().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob item in the db.
     * @param smobItemATO the smob item to be inserted
     */
    override suspend fun saveSmobItem(smobItemATO: SmobItemATO) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobItemDao.saveSmobItem(smobItemATO.asDatabaseModel())
            }
        }

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobItem or an Error object with the error message
     */
    override suspend fun getSmobItem(id: String): Resource<SmobItemATO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobItemDTO = smobItemDao.getSmobItemById(id)
                if (smobItemDTO != null) {
                    return@withContext Resource.success(smobItemDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobItem not found!")
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob items in the db
     */
    override suspend fun deleteAllSmobItems() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobItemDao.deleteAllSmobItems()
            }
        }
    }
}
