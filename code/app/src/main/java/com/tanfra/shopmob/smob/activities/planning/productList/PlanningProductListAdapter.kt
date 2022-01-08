package com.tanfra.shopmob.smob.activities.planning.productList

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
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

    // inject ViewModel from Koin service locator
    val _viewModel: PlanningProductListViewModel by inject()

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_products_item

    // called, when the "UNDO" snackbar has expired
    override fun leftSwipeConfirmed(position: Int, items: List<SmobProductOnListATO>, rootView: View) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

        // collect current list from smobList (flow)
        rootView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect SmobList flow
            val daList = _viewModel.smobList
            daList.collect { daResource ->
                val updatedList = daResource.data?.let {
                    SmobListATO(
                        it.id,
                        it.name,
                        it.description,
                        // replace list of products on smob list with updated list of products
                        items.map { item -> SmobListItem(item.id, item.status!!) },
                        it.members,
                        it.lifecycle,
                    )
                }

                // store updated smobList in local DB
                _viewModel.listRepoFlow.updateSmobList(updatedList!!)

                // also trigger an update of the remote DB
                _viewModel.listRepoFlow.refreshSmobListInRemoteDB(updatedList)

            }  // collect flow

        }  // coroutine scope (lifecycleScope)

    }  // leftSwipeConfirmed

}