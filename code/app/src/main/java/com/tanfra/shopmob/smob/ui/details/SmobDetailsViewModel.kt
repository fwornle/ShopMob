package com.tanfra.shopmob.smob.ui.details

import android.app.Application
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.details.components.DetailsUiState
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class SmobDetailsViewModel(app: Application) : BaseViewModel(app),
    ContainerHost<DetailsUiState, Nothing> {

    // lifecycle constant parameters (set in Activity/Fragment, from launching intent data)
    lateinit var currNavSource: NavigationSource
    lateinit var currItem: Ato
    lateinit var currSendToShopOnMap: (SmobShopATO) -> Unit
    lateinit var currSendToShop: () -> Unit


    // instantiate orbit-mvi container as UI state holder (exposes flows: ui states + side effects)
    override val container = container<DetailsUiState, Nothing>(DetailsUiState())


    // set UI state with current display parameters (received from the Activity launching intent)
    // --> note: this merely sends an intent to reducer in orbit-mvi container (= ui state holder)
    fun setUiState() {
        intent {
            reduce {
                DetailsUiState(
                    isLoading = false,
                    navSource = currNavSource,
                    item = currItem,
                    sendToMap = currSendToShopOnMap,
                    sendToShop = currSendToShop
                )
            }
        }
    }

}