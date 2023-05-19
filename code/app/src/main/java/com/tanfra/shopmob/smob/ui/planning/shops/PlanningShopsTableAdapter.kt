package com.tanfra.shopmob.smob.ui.planning.shops

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// use data binding to show the smob item on the RV item
class PlanningShopsTableAdapter(rootView: View, callBack: (selectedSmobATO: SmobShopATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobShopATO>(rootView, callBack), KoinComponent {

    // inject _viewModel from Koin service locator
    private val _viewModel: PlanningViewModel by inject()

    // SearchView widget can be used to preFilter the list using user input
    override fun getSearchViewItems(items: List<SmobShopATO>, charSearch: String)
    : MutableList<SmobShopATO> {
        // default: no filtering
        return items.toMutableList()
    }

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobShopATO>): List<SmobShopATO> {

        // take out all items which have been deleted by swiping
        return items
            .filter { item -> item.itemStatus != ItemStatus.DELETED  }
            .sortedWith(
                compareBy(
                    { it.itemPosition },
                )
            )
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_planning_shops_item

    // called, when the user action has been confirmed and the local DB / backend needs updated
    override fun uiActionConfirmed(item: SmobShopATO, rootView: View) {

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // store updated smobList in local DB
            // ... this also triggers an immediate push to the backend (once stored locally)
            _viewModel.shopDataSource.updateSmobItem(item)

        }  // coroutine scope (lifecycleScope)

    }  // leftSwipeConfirmed

    // dynamically adjust view item content
    override fun adjustViewItem(binding: ViewDataBinding, item: SmobShopATO) {}

}