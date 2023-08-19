package com.tanfra.shopmob.smob.data.repo.dataSource

import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO

/**
 * Table specific functions beyond shared standard CRUD (if any)
 */
interface SmobListDataSource: SmobItemDataSource<SmobListATO> {

    suspend fun refreshSmobListInRemoteDB(smobItemATO: SmobListATO)

}