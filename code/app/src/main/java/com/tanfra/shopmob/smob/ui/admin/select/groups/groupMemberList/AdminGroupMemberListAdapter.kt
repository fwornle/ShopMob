package com.tanfra.shopmob.smob.ui.admin.select.groups.groupMemberList

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobMemberItem
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class AdminGroupMemberListAdapter(rootView: View, callBack: (selectedSmobATO: SmobGroupMemberWithGroupDataATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobGroupMemberWithGroupDataATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobGroupMemberWithGroupDataATO>, charSearch: String)
    : MutableList<SmobGroupMemberWithGroupDataATO> {
        // default: no filtering
        return items.toMutableList()
    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobGroupMemberWithGroupDataATO>): List<SmobGroupMemberWithGroupDataATO> {

        // figure out who hasn't been deleted yet
        val validMemberIds =
            if (items.isNotEmpty()) items.first().groupMembers
                .filter { member -> member.status != SmobItemStatus.DELETED }
                .map { member -> member.id }
            else listOf()

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> validMemberIds.contains(item.id) }
            .sortedWith(
                compareBy(
                    { it.memberName },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_users_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobGroupMemberWithGroupDataATO, rootView: View) {

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobGroup flow
            val updatedGroup = SmobGroupATO(
                item.groupId,
                item.groupStatus,
                item.groupPosition,
                item.groupName,
                item.groupDescription,
                item.groupType,
                // replace list of group members with updated list of members
                item.groupMembers.map { member ->
                    if(member.id == item.id) {
                        // set new status (group property)
                        SmobMemberItem(
                            member.id,
                            item.itemStatus,  // update list item status (from status set by user)
                            member.listPosition,
                        )
                    } else {
                        member  // not the manipulated product --> keep as is
                    }
                },
                item.groupActivity,
            )

            // store updated smobGroup in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.groupDataSource.updateSmobItem(updatedGroup)

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed

}
