package com.tanfra.shopmob.smob.activities.shopping

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.ActivityShoppingBinding

/**
 * The SmobActivity that holds the SmobShopping fragments
 */
class SmobShoppingActivity : AppCompatActivity() {

    // bind views
    private lateinit var binding: ActivityShoppingBinding

    // use navController activity wide
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping)
        setContentView(binding.root)

        // set-up navController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_planning) as NavHostFragment

        navController = navHostFragment.navController

        // use actionBar (instead of the system's ActivityBar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.navHostFragmentShopping.findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
