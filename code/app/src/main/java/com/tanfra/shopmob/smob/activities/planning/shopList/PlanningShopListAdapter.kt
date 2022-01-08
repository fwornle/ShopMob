package com.tanfra.shopmob.smob.activities.planning.shopList

import android.view.View
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import timber.log.Timber


// use data binding to show the smob item on the RV item
class PlanningShopListAdapter(rootView: View, callBack: (selectedSmobATO: SmobShopATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobShopATO>(rootView, callBack) {

    // allow the BaseRecyclerViewAdapter to access the item layout for this particular RV list
    override fun getLayoutRes(viewType: Int) = R.layout.smob_shops_item

    // called, when the "UNDO" snackbar has expired
    override fun uiActionConfirmed(item: SmobShopATO, rootView: View) {

        // left-swipe confirmed --> purge item from local DB & server
        Timber.i("Left-swipe confirmed: purging item from server")

    }

}