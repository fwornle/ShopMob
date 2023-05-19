package com.tanfra.shopmob.smob.ui.admin.lists.listGroupsTable

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class AdminListGroupsTableAdapter(rootView: View, callBack: (selectedSmobATO: SmobGroupWithListDataATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobGroupWithListDataATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobGroupWithListDataATO>, charSearch: String)
    : MutableList<SmobGroupWithListDataATO> {
        // default: no filtering
        return items.toMutableList()
    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobGroupWithListDataATO>): List<SmobGroupWithListDataATO> {

        // figure out which group hasn't been removed from the list yet
        val validGroupIds =
            if (items.isNotEmpty()) {
                items
                    .first().listGroups
                    .filter { group -> group.status != ItemStatus.DELETED }
                    .map { group -> group.id }
            } else listOf()

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> validGroupIds.contains(item.itemId) }
            .sortedWith(
                compareBy(
                    { it.itemPosition },
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

            // collect SmobList flow
            val updatedList = SmobListATO(
                item.listId,
                item.listStatus,
                item.listPosition,
                item.listName,
                item.listDescription,
                item.listItems,
                // replace list of Smob groups with updated list of Smob groups
                item.listGroups.map { groupItem ->
                    if(groupItem.id == item.itemId) {
                        // set new status (list property)
                        SmobGroupItem(
                            groupItem.id,
                            item.itemStatus,  // update list item status (from status set by user)
                            groupItem.listPosition,
                        )
                    } else {
                        groupItem  // not the manipulated product --> keep as is
                    }
                },
                item.listLifecycle,
            )

            // store updated smobGroup in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.listDataSource.updateSmobItem(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobGroupWithListDataATO) {}

}
