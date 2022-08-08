package com.tanfra.shopmob.smob.ui.admin.select.lists

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class AdminListsAdapter(rootView: View, callBack: (selectedSmobATO: SmobListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobListATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobListATO>, charSearch: String)
    : MutableList<SmobListATO> {
        // default: no filtering
        return items.toMutableList()
    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobListATO>): List<SmobListATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.members.map { member -> member.id }.contains(SmobApp.currUser?.id)  }
            .filter { item -> item.itemStatus != SmobItemStatus.DELETED  }
            //.map { item -> consolidateListItem(item) }
            .sortedWith(
                compareBy(
                    { it.itemPosition },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_admin_groups_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobListATO, rootView: View) {

        // consolidate list item data (prior to writing to the DB)
        val itemAdjusted = if(item.itemStatus != SmobItemStatus.DELETED) {
            // user swiped right --> marking all sub-entries as "IN_PROGRESS" + aggregating here
            consolidateListItem(item)
        } else {
            // user swiped left --> delete list (by marking it as DELETED)
            item
        }

        // update (PUT) adjusted smobList item
        // ... also used to "DELETE" a list (marked as DELETED, then filtered out)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobList flow
            val updatedList = SmobListATO(
                itemAdjusted.id,
                itemAdjusted.itemStatus,
                itemAdjusted.itemPosition,
                itemAdjusted.name,
                itemAdjusted.description,
                itemAdjusted.items,
                itemAdjusted.members,
                itemAdjusted.lifecycle,
            )

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.listDataSource.updateSmobItem(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed


    // recompute status & completion rate from linked list items
    private fun consolidateListItem(item: SmobListATO): SmobListATO {

        // return adjusted item
        return item

    }  // consolidateListItem

}
