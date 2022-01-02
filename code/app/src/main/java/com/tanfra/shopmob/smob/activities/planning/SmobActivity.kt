package com.tanfra.shopmob.smob.activities.planning

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivitySmobItemsBinding

/**
 * The SmobActivity that holds the SmobItem fragments
 */
class SmobActivity : AppCompatActivity() {

    // bind views
    private lateinit var binding: ActivitySmobItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_smob_items)
        setContentView(binding.root)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.navHostFragment.findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
