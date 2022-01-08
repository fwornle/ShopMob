package com.tanfra.shopmob.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

import com.google.android.material.snackbar.Snackbar


abstract class BaseRecyclerViewAdapter<T>(val rootView: View, private val callback: ((item: T) -> Unit)? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<T>>() {

    // the data...
    private var _items: MutableList<T> = mutableListOf()

    /**
     * Returns the _items data
     */
    private val items: List<T>
        get() = this._items

    override fun getItemCount() = _items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil
            .inflate<ViewDataBinding>(layoutInflater, getLayoutRes(viewType), parent, false)

        binding.lifecycleOwner = getLifecycleOwner()

        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.invoke(item)
        }
    }

    // public getter
    fun getItem(position: Int) = _items[position]

    // public setter
    fun setItem(position: Int, value: T) {
        _items[position] = value
    }

    /**
     * Adds data to the actual Dataset
     *
     * @param items to be merged
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addData(items: List<T>) {
        _items.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * Clears the _items data
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        _items.clear()
        notifyDataSetChanged()
    }

    @LayoutRes
    abstract fun getLayoutRes(viewType: Int): Int


    // declare abstract class 'uiActionConfirmed' - to be overridden in (specific) parent class
    abstract fun uiActionConfirmed(item: T, rootView: View)


    open fun getLifecycleOwner(): LifecycleOwner? {
        return null
    }


    // retain recently deleted position (to be able to undo the action)
    var mRecentlyDeletedItem: T? = null
    var mRecentlyDeletedItemPosition: Int = -1

    // swipe left on an OPEN item --> delete item (w/h possibility of undo)
    fun deleteItem(position: Int, textResId: Int) {

        // delete item from list
        _items.removeAt(position)
        notifyItemRemoved(position)

        // snackbar w/h undo button
        showUndoSnackbar(textResId)

    }

    // swipe left/right --> delete item (w/h possibility of undo)
    fun restoreItem(position: Int) {

        // set-up undo
        mRecentlyDeletedItem = items.get(position)
        mRecentlyDeletedItemPosition = position

        // delete item from list
        _items.removeAt(position)
        notifyItemRemoved(position)

        _items.add(
            mRecentlyDeletedItemPosition,
            mRecentlyDeletedItem!!
        )
        notifyItemInserted(mRecentlyDeletedItemPosition)

        // reset temporary undo memory
        mRecentlyDeletedItem = null
        mRecentlyDeletedItemPosition = -1

    }

    private fun showUndoSnackbar(textResId: Int) {
        val view: View = rootView.findViewById(com.tanfra.shopmob.R.id.smobItemsRecyclerView)

        Snackbar
            .make(
                view, textResId,
                Snackbar.LENGTH_LONG
            )
            .apply {
                setAction(com.tanfra.shopmob.R.string.undo) { _ -> undoDelete() }
                addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        // anything but clicking "UNDO" (= DISMISS_EVENT_ACTION) means that the
                        // item shall be deleted from the list in the database / server
                        if(event != DISMISS_EVENT_ACTION) {

                            // snackbar "expired" wht. the user clicking on UNDO
                            // call associated left-swipe action (to be overridden in parent class)
                            mRecentlyDeletedItem?. let { uiActionConfirmed(it, rootView) }

                            // reset temporary undo memory
                            mRecentlyDeletedItem = null
                            mRecentlyDeletedItemPosition = -1

                        }
                    } // onDismissed

                })
                show()
            }

    }  // showUndoSnackbar

    // revert the removal of the last item
    private fun undoDelete() {
        _items.add(
            mRecentlyDeletedItemPosition,
            mRecentlyDeletedItem!!
        )
        notifyItemInserted(mRecentlyDeletedItemPosition)

        // reset temporary undo memory
        mRecentlyDeletedItem = null
        mRecentlyDeletedItemPosition = -1

    }

}

