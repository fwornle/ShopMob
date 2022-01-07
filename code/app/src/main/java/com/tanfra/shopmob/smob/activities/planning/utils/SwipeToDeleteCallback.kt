package com.tanfra.shopmob.smob.activities.planning.utils

import android.graphics.Canvas
import android.graphics.Color
import com.tanfra.shopmob.smob.activities.planning.productList.PlanningProductListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.R
import timber.log.Timber

// swiping action on RVs
// ref: https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
// ... code adapted from there

class SwipeToDeleteCallback(adapter: PlanningProductListAdapter) :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    // RV adapter to be acted upon
    private val mAdapter: PlanningProductListAdapter

    // drawing primitives
    private val iconTrash: Drawable?
    private val iconTick: Drawable?
    private val bgOnTheRight = ColorDrawable(Color.RED)
    private val bgOnTheLeft = ColorDrawable(Color.GREEN)

    // set initial values when instantiating the class
    init {

        // initialize reference to our RV adapter
        mAdapter = adapter

        // fetch trash can icon
        iconTrash = ContextCompat.getDrawable(
            mAdapter.rootView.context,
            R.drawable.ic_baseline_done_24
        )

        // fetch tick icon
        iconTick = ContextCompat.getDrawable(
            mAdapter.rootView.context,
            R.drawable.ic_baseline_delete_forever_24
        )

    }

    // up/down swipes (re-ordering)
    // ... disabled
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // used for up and down movements
        return false
    }

    // left/right
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition

        when(direction) {
            ItemTouchHelper.LEFT -> {
                // swipe left (delete from DB)
                mAdapter.deleteItem(position, R.string.undo_delete)
            }
            ItemTouchHelper.RIGHT -> {
                // swipe right (purchase item)
                mAdapter.deleteItem(position, R.string.undo_purchase)
            }
        }
    }

    // animation during swiping
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        // get the current item to perform some canvas magic on
        val itemView: View = viewHolder.itemView

        // allow the background to "under"lap items with round corners (assuming the corners have
        // a radius which is smaller than this offset - otherwise increase the value)
        val backgroundCornerOffset = 20

        // trash icon dimensions
        val iconTrashMargin: Int = (itemView.height - iconTrash!!.intrinsicHeight) / 2
        val iconTrashTop: Int = itemView.top + iconTrashMargin
        val iconTrashBottom = iconTrashTop + iconTrash.intrinsicHeight

        // tick icon dimensions
        val iconTickMargin: Int = (itemView.height - iconTick!!.intrinsicHeight) / 2
        val iconTickTop: Int = itemView.top + iconTickMargin
        val iconTickBottom = iconTickTop + iconTick.intrinsicHeight

        // right swipe:
        when {
            dX > 0 -> {

                // swipe right --> take item off the list (no purchase)
                val iconTrashLeft: Int = itemView.left + iconTrashMargin
                val iconTrashRight: Int = itemView.left + iconTrashMargin + iconTrash.intrinsicWidth
                iconTrash.setBounds(iconTrashLeft, iconTrashTop, iconTrashRight, iconTrashBottom)
                bgOnTheLeft.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                )
                bgOnTheRight.setBounds(0, 0, 0, 0)

            }
            dX < 0 -> {

                // swipe left --> mark item as purchased
                val iconTickLeft: Int = itemView.right - iconTickMargin - iconTick.intrinsicWidth
                val iconTickRight: Int = itemView.right - iconTickMargin
                iconTick.setBounds(iconTickLeft, iconTickTop, iconTickRight, iconTickBottom)
                bgOnTheRight.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
                bgOnTheLeft.setBounds(0, 0, 0, 0)

            }
            else -> {

                // no swipe
                iconTrash.setBounds(0, 0, 0, 0)
                iconTick.setBounds(0, 0, 0, 0)
                bgOnTheRight.setBounds(0, 0, 0, 0)
                bgOnTheLeft.setBounds(0, 0, 0, 0)

            }

        }  // when (dX)

        // draw now
        bgOnTheLeft.draw(c)
        bgOnTheRight.draw(c)
        iconTick.draw(c)
        iconTrash.draw(c)

    }

}
