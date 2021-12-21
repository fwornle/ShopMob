package com.tanfra.shopmop.smob.data.local

import com.tanfra.shopmob.smob.data.SmobItemDataSource
import com.tanfra.shopmob.smob.data.dto.SmobItemDTO
import com.tanfra.shopmob.smob.data.dto.Result
import com.tanfra.shopmob.smob.data.local.SmobItemDao
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobItemDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobItemsLocalRepository(
    private val smobItemDao: SmobItemDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobItemDataSource {

    /**
     * Get the smob item list from the local db
     * @return Result the holds a Success with all the smob items or an Error object with the error message
     */
    override suspend fun getSmobItems(): Result<List<SmobItemDTO>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(smobItemDao.getSmobItems())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob item in the db.
     * @param smobItem the smob item to be inserted
     */
    override suspend fun saveSmobItem(smobItem: SmobItemDTO) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobItemDao.saveSmobItem(smobItem)
            }
        }

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobItem or an Error object with the error message
     */
    override suspend fun getSmobItem(id: String): Result<SmobItemDTO> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobItemDTO = smobItemDao.getSmobItemById(id)
                if (smobItemDTO != null) {
                    return@withContext Result.Success(smobItemDTO)
                } else {
                    return@withContext Result.Error("SmobItem not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
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
