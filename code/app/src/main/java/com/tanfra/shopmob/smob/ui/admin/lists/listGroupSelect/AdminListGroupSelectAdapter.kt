package com.tanfra.shopmob.smob.ui.admin.lists.listGroupSelect

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobGroupItem
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


// use data binding to show the smob item on the RV item
class AdminListGroupSelectAdapter(rootView: View, callBack: (selectedSmobGroupWithListDataATO: SmobGroupWithListDataATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobGroupWithListDataATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobGroupWithListDataATO>, charSearch: String)
    : MutableList<SmobGroupWithListDataATO> {

        // ignore case
        val searchP = charSearch.lowercase(Locale.ROOT)

        // filter items list according to user provided search string (SearchView)
        return items.filter { item ->
            item.groupName.lowercase(Locale.ROOT).contains(searchP) ||
            item.groupDescription?.lowercase(Locale.ROOT)?.contains(searchP) ?: false
        }.toMutableList()

    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobGroupWithListDataATO>): List<SmobGroupWithListDataATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.itemStatus != SmobItemStatus.DELETED  }
            //.map { item -> consolidateListItem(item) }
            .sortedWith(
                compareBy(
                    { it.groupName },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_admin_group_of_list_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobGroupWithListDataATO, rootView: View) {

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // update currently selected group with new item
            _viewModel.currGroupWithListData?.let {

                // check if selected group is already part of the list
                if(!it.listGroups.map { group -> group.id }.contains(item.itemId)) {

                    // nope --> append new group ID
                    val newGroupListItem = it.listGroups.toMutableList()
                    newGroupListItem.add(
                        SmobGroupItem(
                            item.itemId,
                            item.itemStatus,  // update list item status (from status set by user)
                            (it.listGroups.size + 1).toLong(),
                        )
                    )

                    // assemble updated SmobList item
                    val updatedList = SmobListATO(
                        it.itemId,
                        it.itemStatus,
                        it.itemPosition,
                        it.listName,
                        it.listDescription,
                        it.listItems,
                        newGroupListItem,
                        it.listLifecycle,
                    )

                    // store updated smobList in local DB
                    // ... this also triggers an immediate push to the backend (once stored locally)
                    _viewModel.listDataSource.updateSmobItem(updatedList)

                    // update current group holder
                    _viewModel.currList = updatedList

                }  // newly selected group & not yet part of the list

            }  // _viewModel.currGroup != null

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobGroupWithListDataATO) {}

}
