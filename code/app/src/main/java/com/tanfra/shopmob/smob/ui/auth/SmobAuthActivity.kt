package com.tanfra.shopmob.smob.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import timber.log.Timber
import com.google.firebase.auth.FirebaseAuth
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.local.RefreshLocalDB
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.ui.zeTheme.ShopMobTheme
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import org.koin.android.ext.android.inject


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and
 * redirects the signed in users to the SmobActivity.
 */
class SmobAuthActivity : AppCompatActivity() {

    // initialize activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // we're here first time or after having been logged out
        SmobApp.currUser = null

        // use FirebaseUI auth flow to sign in users
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {

            // already authenticated --> enter app directly
            wrapEspressoIdlingResource { sendAuthUserToMainApp(auth) }

        }

        // display AuthScreen with Login button
        setContent {
            ShopMobTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuthScreen {
                        // install onClickListener for "Login" button
                        launchSignInFlow()
                    }
                }
            }
        }

    }


    // firebaseUI auth flow
    private fun launchSignInFlow() {
        // firebaseUI: Choose authentication providers
        // https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().build(),
            AuthUI.IdpConfig.YahooBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().setPermissions(arrayListOf("user_friends")).build(),
        )

        // firebaseUI: create sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            //.setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
            .setTheme(R.style.AppTheme)
            .build()

        // firebaseUI: launch sign-in intent
        signInLauncher.launch(signInIntent)
        //wrapEspressoIdlingResource { signInLauncher.launch(signInIntent) }

    }

    // redirect successfully authenticated users to the main app
    private fun sendAuthUserToMainApp(auth: FirebaseAuth) {

        val user = auth.currentUser
        var isNewUser: Boolean?

        // user object returned?
        user?.let {

            // yup - extract meta information and send to starting the activity of the app
            isNewUser = it.metadata?.creationTimestamp == it.metadata?.lastSignInTimestamp

            // send authenticated user to "SmobItemsActivity"
            val intent = Intent(applicationContext, SmobPlanningActivity::class.java)
            wrapEspressoIdlingResource {
                startActivity(
                    intent
                        .putExtra("userId", it.uid)
                        .putExtra("userName", it.displayName)
                        .putExtra("userEmail", it.email)
                        .putExtra("userProfileUrl", it.photoUrl.toString())
                        .putExtra("isNewUser", isNewUser)
                )
            }

            // we will never get to here

        }

        // if we are here, the authentication failed
        Timber.i("Authentication via firebase failed.")

    }

    // firebaseUI auth flow - register callback 'onSignInResult'
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    // callback - will be called by the FirebaseUI auth flow upon sign-in
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {

        val response = result.idpResponse

        // check result code of returning firebaseUI auth flow
        if (result.resultCode == RESULT_OK) {

            // Successfully signed in
            val auth = FirebaseAuth.getInstance()
            sendAuthUserToMainApp(auth)

        } else {

            // Sign in failed
            // If response is null the user canceled the sign-in flow using the back button
            response?.let {
                // ...otherwise --> some error occurred
                Timber.i("Auth Error: ${it.error?.errorCode}")
            } ?: Timber.i("User cancelled sign-in flow.")

        }  // else: unsuccessful firebaseUI flow

    }  // onSignInResult


    override fun onResume() {
        super.onResume()
        RefreshLocalDB.timer.start()

        val networkConnectionManager: NetworkConnectionManager by inject()
        networkConnectionManager.startListenNetworkState()

    }

    override fun onPause() {
        super.onPause()
        RefreshLocalDB.timer.cancel()
    }

}
