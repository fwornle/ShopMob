package com.tanfra.shopmob.smob.smoblist

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.types.SmobItem


// use data binding to show the smob item on the RV item
class SmobItemListAdapter(callBack: (selectedSmob: SmobItem) -> Unit) :
    BaseRecyclerViewAdapter<SmobItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_item
}