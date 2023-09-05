package com.tanfra.shopmob.smob.ui.details

import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource

/*
 * States, Events, SideEffects for FSM SmobDetails
 */
class DetailsState {

    sealed class State {
        object Idle : State()
        object Displaying : State()
    }

    sealed class Event {
        class OnDisplay(val navSource: NavigationSource, val item: Ato) : Event()
        object OnExit : Event()
        object OnEnterShop : Event()
        object OnOrientate : Event()
    }

    sealed class SideEffect {
        class SetDisplayItem(val navSource: NavigationSource, val item: Ato) : SideEffect()
        object LaunchShop : SideEffect()
        object LaunchMap : SideEffect()
    }

}