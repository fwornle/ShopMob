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
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.activities.planning.productList.PlanningProductListFragment
import timber.log.Timber


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

    // when the app is no longer visible...
    override fun onStop() {
        super.onStop()

        Timber.i("ShopMob in the background - cancelling fast polling")
        SmobApp.cancelRecurringWorkFast()

    }

    // when the app comes back into the foreground...
    override fun onRestart() {
        super.onRestart()

        Timber.i("ShopMob restarting - restarting fast polling")
        SmobApp.delayedInitRecurringWorkFast()

    }

    // when the app is switched off (= "destroyed")...
    // ... stop polling
    override fun onDestroy() {
        super.onDestroy()

        Timber.i("ShopMob switching off - cancelling all polling")
        SmobApp.cancelRecurringWorkFast()
        SmobApp.cancelRecurringWorkSlow()

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
