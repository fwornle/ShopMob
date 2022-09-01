package com.tanfra.shopmob.smob.ui.planning

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
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.work.SmobAppWork
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber


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

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planning)
        setContentView(binding.root)


        // newly logged in?
        if(SmobApp.currUser == null) {

            // auth sent us here...
            // attempt to read extra data from incoming intent (auth)
            var userName = "user-without-username"
            var userId = "user-without-firebase-id"
            var userEmail = "user-without-email"
            var userProfileUrl: String? = null
            var isNewUser = false

            val extras: Bundle? = intent.extras
            extras?.let {
                if (it.containsKey("userName")) {
                    // extract the extra-data in the intent
                    userId = it.get("userId") as String
                    userName = it.get("userName") as String
                    userEmail = it.get("userEmail") as String
                    userProfileUrl = it.get("userProfileUrl") as String
                    isNewUser = it.get("isNewUser") as Boolean
                }
            }

            // update/store user in local DB of the app (as well as the backend)
            wManager.applicationScope.launch {

                // fetch user repo to allow storing/updating of logged-in user
                val userRepo: SmobUserDataSource by inject()

                // fetch latest user collection from BE (to ensure the app starts up
                // reliably, even after a fresh install - and, consequently, no local DB)
                userRepo.refreshDataInLocalDB()

                // determine highest item position
                userRepo.getAllSmobItems().take(1).collectLatest { daResList ->
                    daResList.data.let { allUsers ->

                        Timber.i("Number of users: ${allUsers?.size ?: -1}")

                        val userItemPos: Long
                        val daUser: SmobUserATO? = allUsers?.find { it.id == userId }

                        // determine position of user item in DB
                        userItemPos = if(daUser == null) {
                            // user new to ShopMob app --> append at the end
                            // indicate that this is a new user (new to ShopMob)
                            isNewUser = true

                            // determine highest user position (plus one)
                            allUsers?.maxOf { it -> it.itemPosition + 1 } ?: -1

                        } else {
                            // user already in ShopMob DB --> use current position
                            daUser.itemPosition
                        }


                        // define user object
                        SmobApp.currUser = SmobUserATO(
                            userId,
                            if(isNewUser) SmobItemStatus.NEW else SmobItemStatus.OPEN,
                            userItemPos,
                            userName.trim().replace(" ", "."),
                            userName,
                            userEmail,
                            userProfileUrl.toString(),
                            daUser?.groups ?: listOf(),
                        )

                        Timber.i("User has groups: ${daUser?.hasGroupRefs()}")

                        // attempt to update user data in local/backend DB (or store, if new)
                        // --> create new co-routine (also in 'wManager.applicationScope')
                        // --> forces the parent co-routine to await completion of the 'child' CR
                        //     ... and, as such, ensures a reliable start-up of the app - as
                        //     SmobApp.currUser is set before the fragment is initialized (thereby
                        //     guaranteeing the correct display of the user's smobLists)
                        launch { userRepo.saveSmobItem(SmobApp.currUser!!) }

                    }

                }

            }  //  applicationScope


        }  // just logged-in


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
            .findFragmentById(R.id.nav_host_fragment_planning) as NavHostFragment

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
        setupActionBarWithNavController(this, navController, drawerLayout)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_planning) as NavHostFragment
        val navController = navHostFragment.navController
        return navigateUp(navController, drawerLayout)
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
