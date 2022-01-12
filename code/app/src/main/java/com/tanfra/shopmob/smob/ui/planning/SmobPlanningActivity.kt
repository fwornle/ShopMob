package com.tanfra.shopmob.smob.ui.planning

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.android.ext.android.inject


/**
 * The SmobActivity that holds the SmobPlanning fragments
 */
class SmobPlanningActivity : AppCompatActivity() {

    // bind views
    private lateinit var binding: ActivityPlanningBinding

    // use navController activity wide
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planning)
        setContentView(binding.root)

        // enable drawer (navbar)
        drawerLayout = binding.drawerLayout

        // set-up navController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_planning) as NavHostFragment

        navController = navHostFragment.navController

        // use actionBar (instead of the system's ActivityBar) - with drawer layout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)


        // lock drawer layout on all pages/destinations but the start destination
        navController.addOnDestinationChangedListener {
                nc: NavController,
                nd: NavDestination,
                args: Bundle?
            ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // configure drawer layout
        setupActionBarWithNavController(this, navController, drawerLayout)
        setupWithNavController(binding.navView, navController)

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(drawerLayout) || super.onSupportNavigateUp()
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
