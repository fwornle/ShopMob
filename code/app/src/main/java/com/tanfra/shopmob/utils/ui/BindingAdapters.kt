package com.tanfra.shopmob.utils.ui

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.net.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.utils.fadeIn
import com.tanfra.shopmob.utils.fadeOut
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
        items: StateFlow<List<T>>
    ) {
        // collecting flow --> this must be on a coroutine
        recyclerView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect flow items
            items
                .take(1)
                .catch { Timber.i("error in flow/take(1): $it") }
                .collect { itemList ->
                (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                    clear()
                    addData(itemList)
                }
            }

        }  // coroutine scope
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
        items: StateFlow<Resource<List<T>>>
    ) {
        // collecting flow --> this must be on a coroutine
        recyclerView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

            // collect flow items
            items
                .take(1)
                .catch { Timber.i("error in flow/take(1): $it") }
                .collect { itemList ->
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
    fun setStatusColor(view: View, status: SmobItemStatus = SmobItemStatus.OPEN) {
        when(status) {
            SmobItemStatus.NEW -> view.setBackgroundResource(R.color.swipeSecondaryLightColor)
            SmobItemStatus.OPEN -> view.setBackgroundResource(R.color.swipeSecondaryLightColor)
            SmobItemStatus.IN_PROGRESS -> view.setBackgroundResource(R.color.swipePrimaryColor)
            SmobItemStatus.DONE -> view.setBackgroundResource(R.color.swipeSecondaryColor)
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


    // layout properties with attribute <... app:itemImage ...> call upon this code
    @BindingAdapter("itemImage")
    @JvmStatic
    fun bindItemImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            // load image using "coil" (https://github.com/coil-kt/coil#readme)
            // REF: https://betterprogramming.pub/how-to-use-coil-kotlins-native-image-loader-d6715dda7d26
            // ... suspends the current coroutine; non-blocking and thread safe
            // ... built-in image cache --> each product image only loaded once
            imgView
                // TEST images:
                //.load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTc9mTlmQ_nD24rgudSHzKPsAZdSKn861Z0bw&usqp=CAU")
                //.load("https://media.istockphoto.com/photos/sunrise-at-quiraing-isle-of-skye-scotland-picture-id143177040")
                //.load("https://placeimg.com/300/200/arch?1")
                // actual product image
                .load(it)
                {
                    crossfade(true)
                    placeholder(R.drawable.smob_1)      // during loading of actual image
                    error(R.drawable.ic_baseline_broken_image_24)  // retrieval of image failed
                }

        }

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