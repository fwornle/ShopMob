package com.tanfra.shopmob.smob.domain

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductRepository
import timber.log.Timber

class FetchProductDetailsUseCase(
    private val productDataSource: SmobProductRepository,
) {
    operator fun invoke(id: String) {
        Timber.i("Fetching product details for product ID: $id")
    }
}