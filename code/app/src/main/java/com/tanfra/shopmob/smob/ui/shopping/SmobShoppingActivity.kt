package com.tanfra.shopmob.smob.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityShoppingBinding
import com.tanfra.shopmob.smob.data.local.RefreshLocalDB
import com.tanfra.shopmob.smob.ui.details.createIntent


/**
 * The SmobActivity that holds the SmobShopping fragments
 */
class SmobShoppingActivity : AppCompatActivity() {

    // Intent gateway for lists which want their content to be displayed (generic details screen)
    companion object{
        // caller (typically outside this activity) can create an intent (no extra data)
        fun newIntent(context: Context): Intent {
            return context.createIntent<SmobShoppingActivity>()
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


    override fun onResume() {
        super.onResume()
        RefreshLocalDB.timer.start()
    }

    override fun onPause() {
        super.onPause()
        RefreshLocalDB.timer.cancel()
    }

}
