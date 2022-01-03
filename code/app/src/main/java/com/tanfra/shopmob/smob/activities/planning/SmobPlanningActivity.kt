package com.tanfra.shopmob.smob.activities.planning

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.activities.planning.productList.PlanningProductListFragment


/**
 * The SmobActivity that holds the SmobPlanning fragments
 */
class SmobPlanningActivity : AppCompatActivity() {

    // bind views
    private lateinit var binding: ActivityPlanningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planning)
        setContentView(binding.root)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.navHostFragmentPlanning.findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // get back to the fragment we came from
    // ... when returning from SmobDetailsActivity (uses extra 'smobActivityReturn'
    override fun onResume() {
        super.onResume()

        val intent = intent
        val frag = intent.extras!!.getString("smobActivityReturn")
        val fragManager = getSupportFragmentManager()

        when (frag) {
            "currProductList" ->
                fragManager.beginTransaction().replace(R.id.nav_host_fragment_planning, PlanningProductListFragment())
                    .commit()
        }
    }
}
