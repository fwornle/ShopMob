package com.tanfra.shopmob.smob.ui.zeUtils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// programmatically open keyboard to allow focus on EditText box to be set automatically
// ... see: https://stackoverflow.com/questions/50743467/focus-edit-text-programmatically-kotlin
fun openSoftKeyboard(context: Context, view: View) {
    view.requestFocus()
    // open the soft keyboard
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

// programmatically close keyboard
// ... see: https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically
fun closeSoftKeyboard(context: Context, view: View) {
    // close the soft keyboard
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
