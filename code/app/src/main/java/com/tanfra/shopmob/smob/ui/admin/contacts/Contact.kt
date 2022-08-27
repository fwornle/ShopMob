package com.tanfra.shopmob.smob.ui.admin.contacts
// adapted from: https://medium.com/@kednaik/android-contacts-fetching-using-coroutines-aa0129bffdc4

import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus


data class Contact(
    val id: String,
    val name:String,
    var itemStatus: SmobItemStatus,
    val numbers: MutableList<String> = mutableListOf(),
    val emails: MutableList<String> = mutableListOf(),
)
