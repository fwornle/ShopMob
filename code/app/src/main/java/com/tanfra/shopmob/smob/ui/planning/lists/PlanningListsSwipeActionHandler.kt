package com.tanfra.shopmob.smob.ui.planning.lists

import com.tanfra.shopmob.smob.ui.planning.utils.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import timber.log.Timber

// swiping action on RV - concrete implementation for smobList list
@Suppress("UNCHECKED_CAST")
class PlanningListsSwipeActionHandler(adapter: PlanningListsAdapter):
    BaseSwipeActionHandler<BaseRecyclerViewAdapter<Ato>>(adapter as BaseRecyclerViewAdapter<Ato>) {

    // apply side effect of swiping action
    override fun swipeActionSideEffect(listViewAdapter: BaseRecyclerViewAdapter<Ato>, position: Int) {
        Timber.i("Applying side effects to item with status: (${listViewAdapter.getItem(position).itemStatus}")
    }

}