package com.tanfra.shopmob.smob.ui.planning.productList

import com.tanfra.shopmob.smob.ui.planning.utils.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import timber.log.Timber

// swiping action on RV - concrete implementation for smobProduct list
@Suppress("UNCHECKED_CAST")
class PlanningProductListSwipeActionHandler(adapter: PlanningProductListAdapter):
    BaseSwipeActionHandler<BaseRecyclerViewAdapter<Ato>>(adapter as BaseRecyclerViewAdapter<Ato>) {

    // apply side effect of swiping action
    override fun swipeActionSideEffect(listViewAdapter: BaseRecyclerViewAdapter<Ato>, position: Int) {
        Timber.i("Possibility to apply post-swipe side effects.")
    }

}