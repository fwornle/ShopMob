package com.tanfra.shopmob.smob.ui.admin.groups.groupMemberSelect

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.SmobMemberItem
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


// use data binding to show the smob item on the RV item
class AdminGroupMemberSelectAdapter(rootView: View, callBack: (selectedSmobUserATO: SmobUserATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobUserATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobUserATO>, charSearch: String)
    : MutableList<SmobUserATO> {

        // ignore case
        val searchP = charSearch.lowercase(Locale.ROOT)

        // filter items list according to user provided search string (SearchView)
        return items.filter { item ->
            item.name.lowercase(Locale.ROOT).contains(searchP) ||
            item.username.lowercase(Locale.ROOT).contains(searchP) ||
            item.email.lowercase(Locale.ROOT).contains(searchP)
        }.toMutableList()

    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobUserATO>): List<SmobUserATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.status != ItemStatus.DELETED  }
            //.map { item -> consolidateListItem(item) }
            .sortedWith(
                compareBy { it.name }
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_admin_user_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun uiActionConfirmed(item: SmobUserATO, rootView: View) {

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // update currently selected group with new item
            _viewModel.currGroup?.let { daGroup ->

                // check if selected user is already part of the list
                if(!daGroup.members.map { member -> member.id }.contains(item.id)) {

                    // nope --> append member
                    val newMemberList = daGroup.members.toMutableList()
                    newMemberList.add(
                        SmobMemberItem(
                            item.id,
                            item.status,  // update list item status (from status set by user)
                            (daGroup.members.size).toLong(),
                        )
                    )

                    // assemble updated SmobGroup item
                    val updatedGroup = SmobGroupATO(
                        daGroup.id,
                        daGroup.status,
                        daGroup.position,
                        daGroup.name,
                        daGroup.description,
                        daGroup.type,
                        newMemberList,
                        daGroup.activity,
                    )

                    // store updated smobGroup in local DB
                    // ... this also triggers an immediate push to the backend (once stored locally)
                    _viewModel.groupDataSource.updateSmobItem(updatedGroup)

                    // update current group holder
                    _viewModel.currGroup = updatedGroup

                    // also add group to new member's own group list
                    _viewModel.currGroupMember?.let { newGroupMember ->

                        // append new group ID to new member's own group list
                        val updatedMemberGroups = newGroupMember.groups
                            // hygiene: filter out empty group entries
                            .filter { groupId -> groupId != "" }
                            // filter out this group (no longer a member of it)
                            .filter { groupId -> groupId != daGroup.id }

                        // replace member's own groups list with updated list
                        newGroupMember.groups = updatedMemberGroups

                        // update smob User in DB
                        _viewModel.updateSmobUserItem(newGroupMember)

                        // ensure this is updated too
                        _viewModel.currGroupMember = newGroupMember

                    }  // currGroupMember set

                }  // newly selected member not yet a member of this group

            }  // _viewModel.currGroup != null

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobUserATO) {}

}
