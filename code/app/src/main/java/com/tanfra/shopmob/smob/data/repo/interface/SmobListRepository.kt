package com.tanfra.shopmob.smob.data.repo.`interface`

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

/**
 * Table specific functions beyond shared standard CRUD (if any)
 */
interface SmobListRepository: SmobItemRepository<SmobListATO> {

    suspend fun refreshSmobListInRemoteDB(smobItemATO: SmobListATO)

}