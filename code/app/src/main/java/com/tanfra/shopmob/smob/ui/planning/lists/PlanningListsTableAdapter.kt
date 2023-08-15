package com.tanfra.shopmob.smob.ui.planning.lists

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt


// use data binding to show the smob item on the RV item
class PlanningListsTableAdapter(rootView: View, callBack: (selectedSmobATO: SmobListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobListATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: PlanningViewModel by inject()

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
                //
            .filter { item -> item.groups.map { group -> group.id }.intersect((SmobApp.currUser?.groups ?: listOf()).toSet()).any() }
            .filter { item -> item.status != ItemStatus.DELETED  }
            .map { item -> consolidateListItem(item) }
            .sortedWith(
                compareBy(
                    { it.position },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_planning_lists_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    // ... this is the point where the list can be consolidated, if needed (eg. aggregate status)
    override fun uiActionConfirmed(item: SmobListATO, rootView: View) {

        // consolidate list item data (prior to writing to the DB)
        val itemAdjusted = if(item.status != ItemStatus.DELETED) {
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
                itemAdjusted.status,
                itemAdjusted.position,
                itemAdjusted.name,
                itemAdjusted.description,
                // replace list of products on smob list with updated list of products
                itemAdjusted.items.map { product ->
                    if(product.id == itemAdjusted.id) {
                        // set new status (list property)
                        SmobListItem(
                            product.id,
                            itemAdjusted.status,
                            product.listPosition,
                            product.mainCategory,
                        )
                    } else {
                        product
                    }
                },
                itemAdjusted.groups,
                itemAdjusted.lifecycle,
            )

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.listDataSource.updateSmobItem(updatedList)

        }  // coroutine scope (lifecycleScope)

    }  // uiActionConfirmed


    // recompute status & completion rate from linked list items
    private fun consolidateListItem(item: SmobListATO): SmobListATO {

        // consolidate smobList...
        val valItems = item.items.filter { itm -> itm.status != ItemStatus.DELETED }
        val nValItems = valItems.size

        // ... status
        val aggListStatus =
            valItems.fold(0) { sum, daItem -> sum + daItem.status.ordinal }
        item.status = when (aggListStatus) {
            in 0..nValItems -> ItemStatus.OPEN
            nValItems * ItemStatus.DONE.ordinal -> ItemStatus.DONE
            else -> ItemStatus.IN_PROGRESS
        }

        // ... completion rate (= nDONE/nTOTAL)
        item.lifecycle.completion = when(nValItems) {
            0 -> 0.0
            else -> {
                val doneItems = valItems.filter { daItem -> daItem.status == ItemStatus.DONE }.size
                (100.0 * doneItems / nValItems).roundToInt().toDouble()
            }
        }

        // return adjusted item
        return item

    }  // consolidateListItem

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobListATO) {}

}
