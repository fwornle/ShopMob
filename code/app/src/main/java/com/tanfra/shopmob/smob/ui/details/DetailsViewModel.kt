package com.tanfra.shopmob.smob.ui.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductOnListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource


class DetailsViewModel(
    app: Application,
    private val smobProductDataSource: SmobProductDataSource,
    private val smobShopDataSource: SmobShopDataSource,
) : BaseViewModel(app) {

    // item to be displayed on the UI
    val smobShopDetailsItem = MutableLiveData<SmobShopATO?>()
    val smobProductDetailsItem = MutableLiveData<SmobProductOnListATO?>()

    init {
        smobShopDetailsItem.value = null
        smobProductDetailsItem.value = null
    }

}