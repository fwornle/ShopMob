package com.tanfra.shopmob.smob.ui.admin.groups.groupMembersTable

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class AdminGroupMembersTableAdapter(rootView: View, callBack: (selectedSmobATO: SmobGroupMemberWithGroupDataATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobGroupMemberWithGroupDataATO>(rootView, callBack), KoinComponent {

    // inject viewModel from Koin service locator
    private val viewModel: AdminViewModel by inject()

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
                .filter { member -> member.status != ItemStatus.DELETED }
                .map { member -> member.id }
            else listOf()

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> validMemberIds.contains(item.id) }
            .sortedWith(
                compareBy { it.memberName }
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_admin_member_with_group_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun uiActionConfirmed(item: SmobGroupMemberWithGroupDataATO, rootView: View) {

        // last "touched" item = the swiped item (= group member)
        viewModel.currGroupMemberWithGroupData = item
        viewModel.currGroupMember = item.member().apply {
            // for "DELETED" items --> reset status to OPEN
            // (as this action handler only purges users from group lists)
            if(status == ItemStatus.DELETED) status = ItemStatus.OPEN
        }

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
                            item.status,  // update list item status (from status set by user)
                            member.listPosition,
                        )
                    } else {
                        member  // not the manipulated product --> keep as is
                    }
                },
                item.groupActivity,
            )

            // sort and re-number adjusted member list (in place)
            updatedGroup.members.toMutableList().sortBy { it.listPosition }
            updatedGroup.members
                .forEachIndexed { idx, member -> member.listPosition = (idx + 1).toLong() }


            // store updated smobGroup in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            viewModel.groupDataSource.updateSmobItem(updatedGroup)

            // also update swiped user's groups (in case the user just got thrown of a group)
            viewModel.currGroupMember?.let { daMember ->

                val updatedGroupMemberIds =
                    daMember.groups
                    // hygiene: remove accidental empty entries
                    .filter { groupId -> groupId != "" }
                    // remove this group's ID from (purged) member's group list
                    .filter { groupId -> groupId != item.groupId }

                // update purged member's group list
                daMember.groups = updatedGroupMemberIds

                // update smob User in DB
                viewModel.updateSmobUserItem(daMember)

                // ensure this is updated too
                viewModel.currGroupMember = daMember

            }

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobGroupMemberWithGroupDataATO) {}

}
