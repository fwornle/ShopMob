package com.tanfra.shopmob.smob.ui.planning.lists

import android.view.View
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import timber.log.Timber


// use data binding to show the smob item on the RV item
class PlanningListsAdapter(rootView: View, callBack: (selectedSmobATO: SmobListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobListATO>(rootView, callBack) {

    // filter (and sort) list - straight through, if not needed
    override fun listFilter(items: List<SmobListATO>): List<SmobListATO> {
        return items
    }

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_lists_item

    // called, when the "UNDO" snackbar has expired
    override fun uiActionConfirmed(item: SmobListATO, rootView: View) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

    }

}