package com.tanfra.shopmob.features.smobAdmin.presentation.model

import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO

sealed interface AdminMutation {

    data object ShowLostConnection : AdminMutation
    data object DismissLostConnection : AdminMutation
    data object ShowLoader : AdminMutation
    data class ShowError(val exception: Exception) : AdminMutation

    data class ShowFormWithGroups(val groups: List<SmobGroupATO>) : AdminMutation
    data class ShowLists(val lists: List<SmobListATO>) : AdminMutation
    data class ShowGroupsOfList(
        val list: SmobListATO,
        val groups: List<SmobGroupATO>
    ) : AdminMutation

    data class ShowUsers(val users: List<SmobUserATO>) : AdminMutation
    data class ShowUserDetails(val user: SmobUserATO) : AdminMutation

    data class ShowGroups(val groups: List<SmobGroupATO>) : AdminMutation
    data class ShowGroupDetails(val group: SmobGroupATO) : AdminMutation

}