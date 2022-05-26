package com.tanfra.shopmob.smob.ui.admin

// this is what an outside caller can communicate via an intent when launching the admin activity
enum class SmobAdminTask {
    UNKNOWN, NEW_LIST, EDIT_LIST, NEW_USER, EDIT_USER, NEW_GROUP, EDIT_GROUP
}