package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Table specific functions beyond shared standard CRUD (if any)
 */
interface SmobGroupDataSource: SmobItemDataSource<SmobGroupATO> {

    fun getSmobGroupsByListId(id: String): Flow<Resource<List<SmobGroupATO>>>

}