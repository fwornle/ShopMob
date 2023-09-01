package com.tanfra.shopmob.smob.ui.planning.shops

import android.os.Vibrator
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.utils.ui.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsNavSources
import com.tanfra.shopmob.utils.ui.vibrateDevice

// swiping action on RV - concrete implementation for smobList list
@Suppress("UNCHECKED_CAST")
class PlanningShopsTableSwipeActionHandler(adapter: PlanningShopsTableAdapter):
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
                item.status = ItemStatus.DELETED
                adapter.setItem(position, item)

                // throw item off the list
                // --> swings by UNDO... communication to DB/backend from there
                adapter.deleteItem(position, R.string.undo_delete)

            } // LEFT

            ItemTouchHelper.RIGHT -> {

                // create intent which starts activity SmobDetailsActivity, with clicked data item
                val context = adapter.rootView.context
                val intent = SmobDetailsActivity.newIntent(
                    context,
                    SmobDetailsNavSources.PLANNING_SHOP_LIST,
                    item
                )
                startActivity(context, intent, null)

                // mark all items on smobList as 'IN_PROGRESS' (only relevant on NEW/OPEN lists)
                when (item.status) {
                    ItemStatus.NEW, ItemStatus.OPEN -> {
                        item.status = ItemStatus.IN_PROGRESS
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