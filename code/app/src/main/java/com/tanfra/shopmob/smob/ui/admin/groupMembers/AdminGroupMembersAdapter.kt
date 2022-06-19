package com.tanfra.shopmob.smob.ui.admin.groupMembers

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class AdminGroupMembersAdapter(rootView: View, callBack: (selectedSmobATO: SmobUserATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobUserATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: AdminViewModel by inject()

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobUserATO>): List<SmobUserATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.itemStatus != SmobItemStatus.DELETED  }
            //.map { item -> consolidateListItem(item) }
            .sortedWith(
                compareBy(
                    { it.itemPosition },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_users_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobUserATO, rootView: View) {

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

            // collect SmobUser flow
            val updatedList = SmobUserATO(
                itemAdjusted.id,
                itemAdjusted.itemStatus,
                itemAdjusted.itemPosition,
                itemAdjusted.username,
                itemAdjusted.name,
                itemAdjusted.email,
                itemAdjusted.imageUrl,
            )

            // store updated smobUser in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.userDataSource.updateSmobItem(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed


    // recompute status & completion rate from linked list items
    private fun consolidateListItem(item: SmobUserATO): SmobUserATO {

        // return adjusted item
        return item

    }  // consolidateListItem

}
