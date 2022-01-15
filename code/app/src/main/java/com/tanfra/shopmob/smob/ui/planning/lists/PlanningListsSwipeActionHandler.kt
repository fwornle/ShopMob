package com.tanfra.shopmob.smob.ui.planning.lists

import android.os.Vibrator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.local.utils.SmobListLifecycle
import com.tanfra.shopmob.smob.ui.planning.utils.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.planning.utils.vibrateDevice

// swiping action on RV - concrete implementation for smobList list
@Suppress("UNCHECKED_CAST")
class PlanningListsSwipeActionHandler(adapter: PlanningListsAdapter):
    BaseSwipeActionHandler<BaseRecyclerViewAdapter<Ato>>(adapter as BaseRecyclerViewAdapter<Ato>) {

    // apply side effect of swiping action
    // swipe action state machine
    override fun swipeActionStateMachine(
        direction: Int,
        item: Ato,
        position: Int,
        viewHolder: RecyclerView.ViewHolder,
        adapter: BaseRecyclerViewAdapter<Ato>
    ) {
        when (direction) {

            ItemTouchHelper.LEFT -> {

                // mark smobList as 'deleted'
                item.itemStatus = SmobItemStatus.DELETED
                adapter.setItem(position, item)

                // throw item off the list
                // --> swings by UNDO... communication to DB/backend from there
                adapter.deleteItem(position, R.string.undo_delete)

            } // LEFT

            ItemTouchHelper.RIGHT -> {

                // mark all items on smobList as 'IN_PROGRESS' (only relevant on NEW/OPEN lists)
                when (item.itemStatus) {
                    SmobItemStatus.NEW, SmobItemStatus.OPEN -> {
                        (item as SmobListATO).items.map { itm -> itm.status= SmobItemStatus.IN_PROGRESS }
                        adapter.setItem(position, item)
                    }
                    else -> {
                        // smobList already is "IN_PROGRESS" or "DONE" --> indicate haptically
                        val vib = adapter.rootView.context.getSystemService(Vibrator::class.java)
                        vibrateDevice(vib, 150)
                    }

                }  // when (status)

                // restore RV item view (removing the animation effects)
                adapter.restoreItemView(position)

                // send status to DB/backend
                adapter.uiActionConfirmed(item, viewHolder.itemView)

            }  // RIGHT

        }  // when (direction)

    }  // SwipeActionStateMachine

}