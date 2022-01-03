package com.tanfra.shopmob.smob.activities.planning.lists

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO


// use data binding to show the smob item on the RV item
class PlanningListsAdapter(callBack: (selectedSmobATO: SmobListATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobListATO>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_lists_item
}