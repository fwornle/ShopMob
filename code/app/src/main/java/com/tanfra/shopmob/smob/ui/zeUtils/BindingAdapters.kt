package com.tanfra.shopmob.smob.ui.zeUtils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.app.utils.fadeIn
import com.tanfra.shopmob.app.utils.fadeOut
import kotlinx.coroutines.flow.*
import timber.log.Timber

object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using a (straight) StateFlow object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("stateFlow")
    @JvmStatic
    fun <T> setStateFlow(
        recyclerView: RecyclerView,
        items: List<T>
    ) {
        (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
            clear()
            addData(items)
        }
    }


    /**
     * Use binding adapter to set the recycler view data using a Resource wrapped StateFlow object
     * ... Resource.status = { SUCCESS | ERROR | LOADING }
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("stateFlowResource")
    @JvmStatic
    fun <T> setStateFlowResource(
        recyclerView: RecyclerView,
        items: Resource<List<T>>
    ) {
        when (items) {
            is Resource.Empty -> Timber.i("setStateFlowResource: Item list still loading")
            is Resource.Failure -> Timber.i("setStateFlowResource: Couldn't retrieve item list from local DB")
            is Resource.Success -> {
                (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                    clear()
                    addData(items.data)
                }
            }
        }
    }


    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("liveData")
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
    @BindingAdapter("fadeVisibleOnLoading")
    @JvmStatic
    fun <T> setFadeVisibleOnLoading(view: View, resource: Resource<T>) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = when (resource) {
                is Resource.Empty -> { View.VISIBLE }
                else -> { View.GONE }
            }
        } else {
            view.animate().cancel()
            when (resource) {
                is Resource.Empty -> if (view.visibility == View.GONE) view.fadeIn()
                else -> if (view.visibility == View.VISIBLE) view.fadeOut()
            }
        }
    }


    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("fadeVisible")
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

    /**
     * Use this binding adapter to control the background color of the RV item (card) depending
     * on the item status
     */
    @BindingAdapter("statusColor")
    @JvmStatic
    fun setStatusColor(view: View, status: ItemStatus = ItemStatus.OPEN) {
        when(status) {
            ItemStatus.NEW -> view.setBackgroundResource(R.color.swipeSecondaryLightColor)
            ItemStatus.OPEN -> view.setBackgroundResource(R.color.swipeSecondaryLightColor)
            ItemStatus.IN_PROGRESS -> view.setBackgroundResource(R.color.swipePrimaryColor)
            ItemStatus.DONE -> view.setBackgroundResource(R.color.swipeSecondaryColor)
            ItemStatus.INVALID -> view.setBackgroundResource(R.color.blue) // should never happen
            else -> view.setBackgroundResource(R.color.white) // should not happen
        }

    }


    /**
     * Use this binding adapter to control the background color of screen to indicate the
     * network connectivity status
     */
    @BindingAdapter("netConnectBackground")
    @JvmStatic
    fun setNetConnectBackground(view: View, networkConnectionManager: NetworkConnectionManager) {

        // determine lifeCycle scope of current view
        val lifeCycleScope = view.findViewTreeLifecycleOwner()?.lifecycleScope

        lifeCycleScope?.let { lcScope ->

            // collecting flow --> this must be on a coroutine
            networkConnectionManager.isNetworkConnectedFlow
                .onEach { netConnected ->
                    if(netConnected) {
                        view.setBackgroundResource(R.color.white)
//                        Timber.i( "Connected to the network.")
                    } else {
                        view.setBackgroundResource(R.color.colorNoNetwork)
//                        Timber.i("Disconnected from the network.")
                    }
                }
                .launchIn(lcScope)

        }  // lcScope valid

    }

    // layout properties with attribute <... app:profileImage ...> call upon this code
    @BindingAdapter("profileImage")
    @JvmStatic
    fun bindProfileImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            // load image using "coil" (https://github.com/coil-kt/coil#readme)
            // REF: https://betterprogramming.pub/how-to-use-coil-kotlins-native-image-loader-d6715dda7d26
            // ... suspends the current coroutine; non-blocking and thread safe
            // ... built-in image cache --> each product image only loaded once
            imgView
                // actual product image
                .load(it)
                {
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_person_24)  // during loading of actual image
                    error(R.drawable.ic_baseline_broken_image_24)  // retrieval of image failed
                }

        }

    }

}