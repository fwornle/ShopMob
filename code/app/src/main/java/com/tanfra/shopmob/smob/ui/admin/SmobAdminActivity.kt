package com.tanfra.shopmob.smob.ui.admin

import android.content.Context
import android.content.Intent
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
import com.tanfra.shopmob.smob.ui.details.createIntent
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.android.ext.android.inject
import timber.log.Timber


/**
 * Activity that collects administrative tasks (new/edit user, group, list)
 */
class SmobAdminActivity : AppCompatActivity() {

    // (singleton) object to facilitate navigation to this activity (and it's fragments)
    companion object {

        // intent 'extra' data specifier
        private const val EXTRA_SmobAdminTask = "EXTRA_SmobAdminTask"

        // caller (typically outside this activity) can create an intent with a SmobAdminTask
        fun newIntent(context: Context, smobTask: SmobAdminTask): Intent {
            return context.createIntent<SmobAdminActivity>(EXTRA_SmobAdminTask to smobTask)
        }

    }

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

        // fetch data from intent provided by triggering notification
        var intentTask = SmobAdminTask.UNKNOWN

        // attempt to read extra data from incoming intent
        val extras: Bundle? = intent.extras
        extras?.let {
            if (it.containsKey(EXTRA_SmobAdminTask)) {
                // extract the extra-data in the intent
                intentTask = it.get("EXTRA_SmobAdminTask") as SmobAdminTask
            }
        }

        // navigate to the requested fragment
        when(intentTask) {
            SmobAdminTask.NEW_LIST -> Timber.i("Create a new Smob list (one day).")
            SmobAdminTask.EDIT_LIST -> Timber.i("Edit an existing Smob list (one day).")
            SmobAdminTask.NEW_USER -> Timber.i("Create a new Smob user (one day).")
            SmobAdminTask.EDIT_USER -> Timber.i("Edit an existing Smob user (one day).")
            SmobAdminTask.NEW_GROUP -> Timber.i("Create a new Smob group (one day).")
            SmobAdminTask.EDIT_GROUP -> Timber.i("Edit an existing Smob group (one day).")
            else -> Timber.i("Show the Administration selection screen (one day).")
        }


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
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                true
            } else {
                super.onOptionsItemSelected(item)
            }
        } else {
            navController.popBackStack()
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
