package com.tanfra.shopmob.smob.ui.shopping

import android.app.Application
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource


class SmobShoppingViewModel(
    app: Application,
    private val smobProductDataSource: SmobProductDataSource,
    private val smobShopDataSource: SmobShopDataSource,
) : BaseViewModel(app) {

    // item to be displayed on the UI

}