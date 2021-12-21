package com.tanfra.shopmob.smob.smoblist

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter


// use data binding to show the smob item on the RV item
class SmobItemListAdapter(callBack: (selectedSmob: SmobDataItem) -> Unit) :
    BaseRecyclerViewAdapter<SmobDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_item
}