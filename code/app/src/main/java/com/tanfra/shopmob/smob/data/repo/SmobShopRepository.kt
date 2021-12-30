package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
import com.tanfra.shopmob.smob.data.local.dao.SmobShopDao
import com.tanfra.shopmob.smob.types.SmobShop
import com.tanfra.shopmob.utils.asDatabaseModel
import com.tanfra.shopmob.utils.asDomainModel
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobShopDao the dao that does the Room db operations for table smobShops
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobShopRepository(
    private val smobShopDao: SmobShopDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobShopDataSource {

    /**
     * Get the smob item list from the local db
     * @return Result the holds a Success with all the smob items or an Error object with the error message
     */
    override suspend fun getSmobShops(): Resource<List<SmobShop>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Resource.success(smobShopDao.getSmobShops().asDomainModel())
            } catch (ex: Exception) {
                Resource.error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob item in the db.
     * @param smobShop the smob item to be inserted
     */
    override suspend fun saveSmobShop(smobShop: SmobShop) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobShopDao.saveSmobShop(smobShop.asDatabaseModel())
            }
        }

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobShop or an Error object with the error message
     */
    override suspend fun getSmobShop(id: String): Resource<SmobShop> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobShopDTO = smobShopDao.getSmobShopById(id)
                if (smobShopDTO != null) {
                    return@withContext Resource.success(smobShopDTO.asDomainModel())
                } else {
                    return@withContext Resource.error("SmobShop not found!")
                }
            } catch (e: Exception) {
                return@withContext Resource.error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob items in the db
     */
    override suspend fun deleteAllSmobShops() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobShopDao.deleteAllSmobShops()
            }
        }
    }
}
