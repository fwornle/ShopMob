package com.tanfra.shopmob.smob.activities.planning

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.activities.planning.productList.PlanningProductListFragment


/**
 * The SmobActivity that holds the SmobPlanning fragments
 */
class SmobPlanningActivity : AppCompatActivity() {

    // bind views
    private lateinit var binding: ActivityPlanningBinding

    // use navController activity wide
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planning)
        setContentView(binding.root)

        // set-up navController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_planning) as NavHostFragment

        navController = navHostFragment.navController

        // use actionBar (instead of the system's ActivityBar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(this, navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
