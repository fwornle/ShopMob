package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobGroupDao
import com.tanfra.shopmob.smob.types.SmobGroup
import com.tanfra.shopmob.utils.asDatabaseModel
import com.tanfra.shopmob.utils.asDomainModel
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobGroupDao the dao that does the Room db operations for table smobGroups
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobGroupRepository(
    private val smobGroupDao: SmobGroupDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobGroupDataSource {

    /**
     * Get the smob item list from the local db
     * @return Result the holds a Success with all the smob items or an Error object with the error message
     */
    override suspend fun getSmobGroups(): Result<List<SmobGroup>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(smobGroupDao.getSmobGroups().asDomainModel())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob item in the db.
     * @param smobGroup the smob item to be inserted
     */
    override suspend fun saveSmobGroup(smobGroup: SmobGroup) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobGroupDao.saveSmobGroup(smobGroup.asDatabaseModel())
            }
        }

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobGroup or an Error object with the error message
     */
    override suspend fun getSmobGroup(id: String): Result<SmobGroup> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobGroupDTO = smobGroupDao.getSmobGroupById(id)
                if (smobGroupDTO != null) {
                    return@withContext Result.Success(smobGroupDTO.asDomainModel())
                } else {
                    return@withContext Result.Error("SmobGroup not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob items in the db
     */
    override suspend fun deleteAllSmobGroups() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobGroupDao.deleteAllSmobGroups()
            }
        }
    }
}
