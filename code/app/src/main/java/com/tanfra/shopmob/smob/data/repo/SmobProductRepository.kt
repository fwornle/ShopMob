package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.local.dao.SmobProductDao
import com.tanfra.shopmob.smob.types.SmobProduct
import com.tanfra.shopmob.utils.asDatabaseModel
import com.tanfra.shopmob.utils.asDomainModel
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param smobProductDao the dao that does the Room db operations for table smobProducts
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class SmobProductRepository(
    private val smobProductDao: SmobProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SmobProductDataSource {

    /**
     * Get the smob item list from the local db
     * @return Result the holds a Success with all the smob items or an Error object with the error message
     */
    override suspend fun getSmobProducts(): Result<List<SmobProduct>> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(smobProductDao.getSmobProducts().asDomainModel())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a smob item in the db.
     * @param smobProduct the smob item to be inserted
     */
    override suspend fun saveSmobProduct(smobProduct: SmobProduct) =
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobProductDao.saveSmobProduct(smobProduct.asDatabaseModel())
            }
        }

    /**
     * Get a smob item by its id
     * @param id to be used to get the smob item
     * @return Result the holds a Success object with the SmobProduct or an Error object with the error message
     */
    override suspend fun getSmobProduct(id: String): Result<SmobProduct> = withContext(ioDispatcher) {
        // support espresso testing (w/h coroutines)
        wrapEspressoIdlingResource {
            try {
                val smobProductDTO = smobProductDao.getSmobProductById(id)
                if (smobProductDTO != null) {
                    return@withContext Result.Success(smobProductDTO.asDomainModel())
                } else {
                    return@withContext Result.Error("SmobProduct not found!")
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the smob items in the db
     */
    override suspend fun deleteAllSmobProducts() {
        withContext(ioDispatcher) {
            // support espresso testing (w/h coroutines)
            wrapEspressoIdlingResource {
                smobProductDao.deleteAllSmobProducts()
            }
        }
    }
}
