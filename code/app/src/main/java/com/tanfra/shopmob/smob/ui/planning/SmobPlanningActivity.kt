package com.tanfra.shopmob.smob.ui.planning

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.ActivityPlanningBinding
import com.tanfra.shopmob.smob.data.local.RefreshLocalDB
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.types.SmobItemId
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

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    // bind views
    private lateinit var binding: ActivityPlanningBinding

    // use navController activity wide
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    // Subscription to FCM topics requires Android to be allowed to open a notification channel
    // --> ask for permissions... only necessary from API level >= 33 (TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this,
                getString(R.string.notification_permissions_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.notification_permissions_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // check for permissions - launches the system permissions requester
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                // FCM SDK (and your app) can post notifications.
                // ... (nothing special to be done --> proceed invisibly)

            } else
                // ensure we are only asking ONCE (and don't pester the user with repeated checks)
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

                    // first time here --> explain, why this permission is required

                    // in an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission
                    Snackbar.make(
                        binding.navHostFragmentPlanning,  // valid for all "planning" fragments
                        R.string.notification_permission_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings) {
                            // displays system dialog (settings) to invite the user to set the
                            // right permissions
                            requestPermissionLauncher
                                .launch(Manifest.permission.POST_NOTIFICATIONS)
                        }.show()

                } else {

                    // directly ask for the permission (without issuing a rational via the Snackbar)
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planning)
        setContentView(binding.root)

        // need permissions to let FCM send us notifications (of SmobList item updates, etc.)
        askNotificationPermission()

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
                    userId = it.getString("userId") ?: ""
                    userName = it.getString("userName") ?: ""
                    userEmail = it.getString("userEmail") ?: ""
                    userProfileUrl = it.getString("userProfileUrl") ?: ""
                    isNewUser = it.getBoolean("isNewUser")
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
                        val daUser: SmobUserATO? = allUsers?.find { it.itemId.value == userId }

                        // determine position of user item in DB
                        userItemPos = if(daUser == null) {
                            // user new to ShopMob app --> append at the end
                            // indicate that this is a new user (new to ShopMob)
                            isNewUser = true

                            // determine highest user position (plus one)
                            allUsers?.maxOf { it.itemPosition + 1 } ?: -1

                        } else {
                            // user already in ShopMob DB --> use current position
                            daUser.itemPosition
                        }


                        // define user object
                        SmobApp.currUser = SmobUserATO(
                            SmobItemId(userId),
                            if(isNewUser) ItemStatus.NEW else ItemStatus.OPEN,
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


    override fun onResume() {
        super.onResume()
        RefreshLocalDB.timer.start()
    }

    override fun onPause() {
        super.onPause()
        RefreshLocalDB.timer.cancel()
    }

}
