package com.tanfra.shopmob.smob.domain.fsm

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

/*
 * States, Events, SideEffects for FSM SmobDetails
 */
class SmobDetailsState {

    sealed class State {
        data object Moving: State()
        data object Browsing: State()
        data object Displaying: State()
        data object Shopping: State()
    }

    sealed class Event {
        data object OnShopDisplay : Event()
        data object OnItemDisplay : Event()
        data object OnItemsBrowse : Event()
        data object OnShopEnter : Event()
        data object OnShopExit : Event()
        data object OnOrientate : Event()
    }


    sealed class SideEffect {
        class SetShop(val shop: SmobShopATO) : SideEffect()
        class SetItem(val item: Ato): SideEffect()
        class LaunchMap(val shop: SmobShopATO) : SideEffect()
        data object LaunchShop : SideEffect()
    }


}