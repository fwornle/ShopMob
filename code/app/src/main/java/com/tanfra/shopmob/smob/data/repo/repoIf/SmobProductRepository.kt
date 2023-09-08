package com.tanfra.shopmob.smob.data.repo.repoIf

import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Table specific functions beyond shared standard CRUD (if any)
 */
interface SmobProductRepository : SmobItemRepository<SmobProductATO> {

    fun getSmobProductsByListId(id: String): Flow<Resource<List<SmobProductATO>>>

}