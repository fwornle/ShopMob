package com.tanfra.shopmob.smob.ui.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.tanfra.shopmob.R



abstract class BaseRecyclerViewAdapter<T>(val rootView: View, private val callback: ((item: T) -> Unit)? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<T>>(), Filterable {

    // the data...
    private var _items: MutableList<T> = mutableListOf()


    // abstract declaration of list specific functions --------------------------------
    // ... to be implemented in the concrete adapter (--> parent class)

    @LayoutRes
    // fetch the specific layout of the list items to be displayed in the RV
    abstract fun getLayoutRes(viewType: Int): Int

    // confirmed user action --> communication with local DB and backend
    abstract fun uiActionConfirmed(item: T, rootView: View)

    // possibility to filter and sort a list --> can be straight through
    abstract fun listFilter(items: List<T>): List<T>

    // fetch string array for SearchView widget
    abstract fun getSearchViewItems(items: List<T>, charSearch: String): MutableList<T>

    // dynamically adjust view item - providing access to binding of item to be adjusted
    abstract fun adjustViewItem(binding: ViewDataBinding, item: T)

    // generic adapter functionality --------------------------------------------------

    // filtering of the list (from user input in SearchView)
    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                val resultList = if (charSearch.isNotEmpty()) {
                    getSearchViewItems(items, charSearch)
                } else {
                    // no user input --> show everything
                    items
                }
                val filterResults = FilterResults()
                filterResults.values = resultList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                _items = results?.values as MutableList<T>
                notifyDataSetChanged()
            }

        }

    }  // getFilter


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

        // fetch current item
        val item = getItem(position)

        // bind to viewHolder & set onClick listener
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.invoke(item)
        }

        // optional: dynamically customize each item
        adjustViewItem(holder.binding, item)

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

        // call item filter (e.g. remove items in status 'DELETED', consolidate changes, etc.)
        // ... list specific --> call function from concrete adapter
        _items.addAll(listFilter(items))
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

    // dummy function... replace by something better at some stage
    open fun getLifecycleOwner(): LifecycleOwner? {
        return null
    }



    // list manipulation via swiping action (incl. animation)  --------------------------------

    // retain recently deleted position (to be able to undo the action)
    var mSnapshotItem: T? = null
    var mSnapshotItemPosition: Int = -1

    // swipe left on an OPEN item --> delete item (w/h possibility of undo)
    fun deleteItem(position: Int, textResId: Int) {

        // delete item from list
        _items.removeAt(position)
        notifyItemRemoved(position)

        // snackbar w/h undo button
        showUndoSnackbar(textResId)

    }

    // restore RV item view (at the end of a 'right' swipe or after 'undo')
    // ... i. e. whenever the item is not deleted and remains on the list (only status has changed)
    fun restoreItemView(position: Int) {

        // delete item from list (to remove all swiping animation effects)
        _items.removeAt(position)
        notifyItemRemoved(position)

        // (re)-add from snapshot memory
        _items.add(
            mSnapshotItemPosition,
            mSnapshotItem!!
        )
        notifyItemInserted(mSnapshotItemPosition)

        // reset temporary snapshot memory
        mSnapshotItem = null
        mSnapshotItemPosition = -1

    }

    private fun showUndoSnackbar(textResId: Int) {
        val view: View = rootView.findViewById(R.id.smobItemsRecyclerView)

        Snackbar
            .make(
                view, textResId,
                Snackbar.LENGTH_LONG
            )
            .apply {
                setAction(R.string.undo) { undoDelete() }
                addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    // detect an "expiring" UNDO query
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        // anything but clicking "UNDO" (= DISMISS_EVENT_ACTION) means that the
                        // item shall be deleted from the list in the database / server
                        if(event != DISMISS_EVENT_ACTION) {

                            // snackbar "expired" wht. the user clicking on UNDO
                            // call associated left-swipe action (to be overridden in parent class)
                            mSnapshotItem?. let { uiActionConfirmed(it, rootView) }

                            // reset temporary undo memory
                            mSnapshotItem = null
                            mSnapshotItemPosition = -1

                        }
                    } // onDismissed

                })
                show()
            }

    }  // showUndoSnackbar

    // revert the removal of the last item
    private fun undoDelete() {
        _items.add(
            mSnapshotItemPosition,
            mSnapshotItem!!
        )
        notifyItemInserted(mSnapshotItemPosition)

        // reset temporary undo memory
        mSnapshotItem = null
        mSnapshotItemPosition = -1

    }

}

