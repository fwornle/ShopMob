package com.tanfra.shopmob.smob.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityShoppingBinding
import com.tanfra.shopmob.smob.ui.admin.SmobAdminTask
import com.tanfra.shopmob.smob.ui.details.createIntent
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.android.ext.android.inject

/**
 * The SmobActivity that holds the SmobShopping fragments
 */
class SmobShoppingActivity : AppCompatActivity() {

    // Intent gateway for lists which want their content to be displayed (generic details screen)
    companion object{
        // intent 'extra' data specifier
        private const val EXTRA_SmobAdminTask = "EXTRA_SmobAdminTask"

        // caller (typically outside this activity) can create an intent with a SmobAdminTask
        fun newIntent(context: Context, smobTask: SmobAdminTask): Intent {
            return context.createIntent<SmobShoppingActivity>(EXTRA_SmobAdminTask to smobTask)
        }
    }


    // bind views
    private lateinit var binding: ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping)
        setContentView(binding.root)

    }


    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    override fun onResume() {
        super.onResume()
        wManager.delayedInitRecurringWorkFast()
    }

    override fun onPause() {
        super.onPause()
        wManager.cancelRecurringWorkFast()
    }

}
