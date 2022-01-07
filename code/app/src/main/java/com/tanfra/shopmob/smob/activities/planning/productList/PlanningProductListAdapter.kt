package com.tanfra.shopmob.smob.activities.planning.productList

import android.view.View
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductOnListATO
import timber.log.Timber


// use data binding to show the smob item on the RV item
class PlanningProductListAdapter(rootView: View, callBack: (selectedSmobATO: SmobProductOnListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobProductOnListATO>(rootView, callBack) {

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_products_item

    // called, when the "UNDO" snackbar has expired
    override fun leftSwipeConfirmed(item: SmobProductOnListATO) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

    }

}