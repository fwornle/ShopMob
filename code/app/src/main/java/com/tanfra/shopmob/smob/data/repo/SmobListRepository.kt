package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.local.dao.SmobListDao
import com.tanfra.shopmob.smob.types.SmobList
import com.tanfra.shopmob.utils.asDatabaseModel
import com.tanfra.shopmob.utils.asDomainModel
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobListDao the dao that does the Room db operations for table smobLists
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobListRepository(
    private val smobListDao: SmobListDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobListDataSource {

    /**
     * Get the smob list list from the local db
     * @return Result the holds a Success with all the smob lists or an Error object with the error message
     */
    override suspend fun getSmobLists(): Resource<List<SmobList>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Resource.success(smobListDao.getSmobLists().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob list in the db.
     * @param smobList the smob list to be inserted
     */
    override suspend fun saveSmobList(smobList: SmobList) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobListDao.saveSmobList(smobList.asDatabaseModel())
            }
        }

    /**
     * Get a smob list by its id
     * @param id to be used to get the smob list
     * @return Result the holds a Success object with the SmobList or an Error object with the error message
     */
    override suspend fun getSmobList(id: String): Resource<SmobList> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobListDTO = smobListDao.getSmobListById(id)
                if (smobListDTO != null) {
                    return@withContext Resource.success(smobListDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobList not found!")
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob lists in the db
     */
    override suspend fun deleteAllSmobLists() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobListDao.deleteAllSmobLists()
            }
        }
    }
}
