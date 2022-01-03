package com.tanfra.shopmob.smob.activities.planning.productList

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO


// use data binding to show the smob item on the RV item
class PlanningProductListAdapter(callBack: (selectedSmobATO: SmobProductATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobProductATO>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_products_item
}