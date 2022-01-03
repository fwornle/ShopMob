package com.tanfra.shopmob.smob.activities.planning.shopList

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO


// use data binding to show the smob item on the RV item
class PlanningShopListAdapter(callBack: (selectedSmobATO: SmobShopATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobShopATO>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_shops_item
}