package com.tanfra.shopmob.smob.domain

import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import timber.log.Timber

class FetchShopDetailsUseCase(
    private val shopDataSource: SmobShopRepository,
) {
    operator fun invoke(id: String) {
        Timber.i("Fetching shop details for shop ID: $id")
    }
}