package com.tanfra.shopmob.smob.smoblist

import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO


// use data binding to show the smob item on the RV item
class SmobItemListAdapter(callBack: (selectedSmobATO: SmobItemATO) -> Unit) :
    BaseRecyclerViewAdapter<SmobItemATO>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.smob_item
}