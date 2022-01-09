package com.tanfra.shopmob.smob.ui.planning.productList

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListItem
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductOnListATO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


// use data binding to show the smob item on the RV item
class PlanningProductListAdapter(rootView: View, callBack: (selectedSmobATO: SmobProductOnListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobProductOnListATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: PlanningProductListViewModel by inject()

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobProductOnListATO>): List<SmobProductOnListATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.listItemStatus != SmobItemStatus.DELETED  }
            .sortedWith(
                compareBy(
                    { it.productCategory.main },
                    { it.productCategory.sub },
                    { it.listItemPosition },
                    )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_products_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    override fun uiActionConfirmed(item: SmobProductOnListATO, rootView: View) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobList flow
            val updatedList = SmobListATO(
                item.listId,
                item.listName,
                item.listDescription,
                // replace list of products on smob list with updated list of products
                item.listItems.map { product ->
                    if(product.id == item.id) {
                        // set new status (list property)
                        SmobListItem(product.id, item.listItemStatus!!, product.listPosition)
                    } else {
                        product
                    }
                                   },
                item.listMembers,
                item.listLifecycle,
            )

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.listRepoFlow.updateSmobList(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // leftSwipeConfirmed

}