package com.tanfra.shopmob.smob.ui.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityAdminBinding
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.android.ext.android.inject


/**
 * Activity that collects administrative tasks (new/edit user, groups, lists)
 */
class SmobAdminActivity : AppCompatActivity() {

    // data binding
    private lateinit var binding: ActivityAdminBinding

    // use navController activity wide
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate layout
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_admin
        )

        // enable drawer (navbar)
        drawerLayout = binding.drawerLayout

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // set-up navController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_admin) as NavHostFragment

        navController = navHostFragment.navController

        // use actionBar (instead of the system's ActivityBar) - with drawer layout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // lock drawer layout on all pages/destinations but the start destination
        navController.addOnDestinationChangedListener {
                nc: NavController,
                nd: NavDestination,
                _: Bundle?
            ->
            if (nd.id == nc.graph.startDestinationId) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // configure drawer layout
        binding.navView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_admin) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    // handle home button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (navController.currentDestination?.id == navController.graph.startDestinationId) {

            // start destination - we have a Drawer menu here...

            // already handled by class ActionBarDrawerToggle
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                // yes --> prevent further handlers from being called (eg. Fragment, etc.)
                true
            } else {
                // nope --> call parent's handler or (in case there is no parent) indicate
                // 'unhandled' (false)
                super.onOptionsItemSelected(item)
            }

        } else {

            // not the start destination --> indicate 'unhandled' (false) to delegate further
            // handling to subsequently called handlers (Fragment)
            false
        }
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
