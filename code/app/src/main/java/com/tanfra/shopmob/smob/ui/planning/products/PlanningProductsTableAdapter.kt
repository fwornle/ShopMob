package com.tanfra.shopmob.smob.ui.planning.products

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductWithListDataATO
import com.tanfra.shopmob.smob.data.types.SmobItemId
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class PlanningProductsTableAdapter(rootView: View, callBack: (selectedSmobATO: SmobProductWithListDataATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobProductWithListDataATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: PlanningViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobProductWithListDataATO>, charSearch: String)
    : MutableList<SmobProductWithListDataATO> {
        // default: no filtering
        return items.toMutableList()
    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobProductWithListDataATO>): List<SmobProductWithListDataATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.itemStatus != ItemStatus.DELETED  }
            .sortedWith(
                compareBy(
                    { it.productCategory.main },
                    { it.productCategory.sub },
                    { it.itemPosition },
                    )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_planning_products_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    override fun uiActionConfirmed(item: SmobProductWithListDataATO, rootView: View) {

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobList flow
            val updatedList = SmobListATO(
                SmobItemId(item.listId),
                item.listStatus,
                item.listPosition,
                item.listName,
                item.listDescription,
                // replace list of products on smob list with updated list of products
                item.listItems.map { product ->
                    if(product.id == item.itemId.value) {
                        // set new status (list property)
                        SmobListItem(
                            product.id,
                            item.itemStatus,  // update list item status (from status set by user)
                            product.listPosition,
                            product.mainCategory,
                        )
                    } else {
                        product  // not the manipulated product --> keep as is
                    }
                                   },
                item.listGroups,
                item.listLifecycle,
            )

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.listDataSource.updateSmobItem(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // leftSwipeConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobProductWithListDataATO) {}

}