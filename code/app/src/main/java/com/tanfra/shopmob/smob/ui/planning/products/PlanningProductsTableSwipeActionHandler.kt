package com.tanfra.shopmob.smob.ui.planning.products

import android.os.Vibrator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.utils.ui.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.utils.ui.vibrateDevice

// swiping action on RV - concrete implementation for smobProduct list
@Suppress("UNCHECKED_CAST")
class PlanningProductsTableSwipeActionHandler(adapter: PlanningProductsTableAdapter):
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

                // swipe left ("un-purchase" item)
                when (item.status) {

                    ItemStatus.DONE -> {

                        // change item status
                        item.status = ItemStatus.IN_PROGRESS
                        adapter.setItem(position, item)

                        // restore RV item view (removing the animation effects)
                        adapter.restoreItemView(position)

                        // send status to DB/backend
                        adapter.uiActionConfirmed(item, viewHolder.itemView)

                    }
                    ItemStatus.IN_PROGRESS -> {

                        // change item status
                        item.status = ItemStatus.OPEN
                        adapter.setItem(position, item)

                        // restore RV item view (removing the animation effects)
                        adapter.restoreItemView(position)

                        // send status to DB/backend
                        adapter.uiActionConfirmed(item, viewHolder.itemView)

                    }
                    else -> {

                        // mark item as 'deleted'
                        item.status = ItemStatus.DELETED
                        adapter.setItem(position, item)

                        // throw item off the list
                        // --> swings by UNDO... communication to DB/backend from there
                        adapter.deleteItem(position, R.string.undo_delete)
                    }

                }  // when (status)

            } // LEFT

            ItemTouchHelper.RIGHT -> {

                // swipe right (purchase item)
                when (item.status) {
                    ItemStatus.NEW, ItemStatus.OPEN -> {
                        item.status = ItemStatus.IN_PROGRESS
                        adapter.setItem(position, item)
                    }
                    ItemStatus.IN_PROGRESS -> {
                        item.status = ItemStatus.DONE
                        adapter.setItem(position, item)
                    }
                    else -> {
                        // already "DONE" --> indicate haptically
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