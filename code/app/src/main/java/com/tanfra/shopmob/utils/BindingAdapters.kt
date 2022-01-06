package com.tanfra.shopmob.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.tanfra.shopmob.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using StateFlow object
     * The StateFlow object is a Resource wrapped list
     * ... Resource.status = { SUCCESS | ERROR | LOADING }
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("app:stateFlowResource")
    @JvmStatic
    inline fun <reified T> setRecyclerViewDataFromStateFlowResource(
        recyclerView: RecyclerView,
        items: StateFlow<Resource<List<T>>>?
    ) {
        // collecting flow --> this must be on a coroutine
        recyclerView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            // collect flow items
            items?.collect { itemList ->
                (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                    clear()
                    itemList.data?.let { addData(it) }
                }
            }
        }  // coroutine scope
    }

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("app:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using the LOADING status of a Resource
     */
    @BindingAdapter("app:fadeVisibleOnLoading")
    @JvmStatic
    fun <T> setFadeVisibleOnLoading(view: View, resource: Resource<T>?) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (resource?.status == Status.LOADING) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (resource?.status == Status.LOADING) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("app:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

}