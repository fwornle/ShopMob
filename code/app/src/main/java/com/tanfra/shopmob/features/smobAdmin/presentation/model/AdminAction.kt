package com.tanfra.shopmob.features.smobAdmin.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO

sealed interface AdminAction {

    // default actions
    data object CheckConnectivity : AdminAction
    data object IllegalSwipe : AdminAction
    data object LoadLists : AdminAction
    data object LoadUsers : AdminAction
    data object LoadGroups : AdminAction
    data object RefreshLists : AdminAction
    data object RefreshUsers : AdminAction
    data object RefreshGroups : AdminAction

    // lists actions
    data class ConfirmListSwipe(val item: SmobListATO) : AdminAction
    data class SaveNewListItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : AdminAction

    // users actions
    data class ConfirmUserSwipe(val item: SmobUserATO) : AdminAction
    data class LoadUser(val userId: String) : AdminAction
    data class SetCurrentUser(val user: SmobUserATO) : AdminAction
    data class NavigateToUserDetails(val user: SmobUserATO) : AdminAction
    data class SaveNewUserItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : AdminAction

    // groups actions
    data class ConfirmGroupSwipe(val item: SmobGroupATO) : AdminAction
    data class LoadGroup(val shopId: String) : AdminAction
    data class NavigateToGroupDetails(val shop: SmobGroupATO) : AdminAction
    data class SaveNewGroupItem(
        val name: String,
        val description: String,
        val group: Pair<String, String>
    ) : AdminAction

}