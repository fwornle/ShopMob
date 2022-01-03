package com.tanfra.shopmob.smob.activities.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailsViewModel(
    app: Application,
    private val smobProductDataSource: SmobProductDataSource,
    private val smobShopDataSource: SmobShopDataSource,
) : BaseViewModel(app) {

    // item to be displayed on the UI
    val smobShopDetailsItem = MutableLiveData<SmobShopATO?>()
    val smobProductDetailsItem = MutableLiveData<SmobProductATO?>()

    init {
        smobShopDetailsItem.value = null
        smobProductDetailsItem.value = null
    }

}