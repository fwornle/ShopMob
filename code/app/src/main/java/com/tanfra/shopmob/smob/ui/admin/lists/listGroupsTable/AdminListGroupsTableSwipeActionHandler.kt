package com.tanfra.shopmob.smob.ui.admin.lists.listGroupsTable

import android.os.Vibrator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseSwipeActionHandler
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupWithListDataATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice

// swiping action on RV - concrete implementation for smobList list
@Suppress("UNCHECKED_CAST")
class AdminListGroupsTableSwipeActionHandler(adapter: AdminListGroupsTableAdapter):
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

        // cast to actual item of this instance
        // ... by reference --> changes on these objects affect the original object (item)
        val daList: SmobListATO = (item as SmobGroupWithListDataATO).list()
        val daGroupItem = daList.groups.find { group -> group.id == item.id }
        val daItemStatus = daGroupItem?.status

        // avoid "null" status (should never happen)
        daItemStatus?.let {

            when (direction) {

                ItemTouchHelper.LEFT -> {

                    // daItemStatus
                    when (it) {

                        // delete
                        ItemStatus.NEW, ItemStatus.OPEN,
                        ItemStatus.IN_PROGRESS, ItemStatus.DONE -> {
                            // mark smobGroup as 'deleted'
                            daGroupItem.status = ItemStatus.DELETED
                            item.status = ItemStatus.DELETED  //  (item filtering in RV)
                            adapter.setItem(position, item)

                            // throw item off the list
                            // --> swings by UNDO... communication to DB/backend from there
                            adapter.deleteItem(position, R.string.undo_delete)
                        }

                        // ItemStatus.DELETE (--> never reached, as DEL items are filtered out)
                        else -> {
                            // return to 'group inactive'
                            daGroupItem.status = ItemStatus.OPEN
                            item.status = ItemStatus.OPEN  //  (item filtering in RV)
                            adapter.setItem(position, item)

                            // restore RV item view (removing the animation effects)
                            adapter.restoreItemView(position)

                            // send status to DB/backend
                            adapter.uiActionConfirmed(item, viewHolder.itemView)
                        }

                    }  // when

                } // LEFT

                ItemTouchHelper.RIGHT -> {

                    // no swipe right allowed --> vibrate
                    val vib = adapter.rootView.context.getSystemService(Vibrator::class.java)
                    vibrateDevice(vib, 150)

                    // restore RV item view (removing the animation effects)
                    adapter.restoreItemView(position)

                }  // RIGHT

            }  // when (direction)

        }  // let (daItemStatus == null)

    }  // SwipeActionStateMachine

}