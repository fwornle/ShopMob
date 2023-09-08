package com.tanfra.shopmob.smob.data.repo.`interface`

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Table specific functions beyond shared standard CRUD (if any)
 */
interface SmobUserRepository: SmobItemRepository<SmobUserATO> {

    fun getSmobMembersByGroupId(id: String): Flow<Resource<List<SmobUserATO>>>

}