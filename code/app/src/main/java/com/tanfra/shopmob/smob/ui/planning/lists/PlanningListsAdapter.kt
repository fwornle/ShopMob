package com.tanfra.shopmob.smob.ui.planning.lists

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListItem
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


// use data binding to show the smob item on the RV item
class PlanningListsAdapter(rootView: View, callBack: (selectedSmobATO: SmobListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobListATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: PlanningListsViewModel by inject()

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobListATO>): List<SmobListATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.itemStatus != SmobItemStatus.DELETED  }
            .sortedWith(
                compareBy(
                    { it.itemPosition },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_lists_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    override fun uiActionConfirmed(item: SmobListATO, rootView: View) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobList flow
            val updatedList = SmobListATO(
                item.id,
                item.itemStatus,
                item.itemPosition,
                item.name,
                item.description,
                // replace list of products on smob list with updated list of products
                item.items.map { product ->
                    if(product.id == item.id) {
                        // set new status (list property)
                        SmobListItem(product.id, item.itemStatus, product.listPosition)
                    } else {
                        product
                    }
                },
                item.members,
                item.lifecycle,
            )

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.repoFlow.updateSmobList(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // leftSwipeConfirmed

}
