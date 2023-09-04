package com.tanfra.shopmob.smob.ui.details

import android.app.Application
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SmobDetailsViewModel(app: Application) : BaseViewModel(app) {

    // define UI state as flow
    private val _viewState = MutableStateFlow(SmobDetailsViewState())
    val viewState = _viewState.asStateFlow()

    // holder for callback functions, which sends user to SmobShopActivity or to map (Google Maps)
    var sendToShop: () -> Unit = { }
    var sendToMap: () -> Unit = { }


    // set UI state with (activity intent) received item
    fun setDisplayItem(navSource: NavigationSource, item: Ato) {
        _viewState.update { currentState ->
            currentState.copy(
                isLoading = false,
                navSource = navSource,
                item = item
            )
        }
    }

}