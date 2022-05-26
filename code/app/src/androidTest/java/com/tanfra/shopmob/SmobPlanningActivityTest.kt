package com.tanfra.shopmob

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.*
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.smob.data.local.LocalDB
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.ui.planning.shopEdit.PlanningShopEditViewModel
import com.tanfra.shopmob.util.DataBindingIdlingResource
import com.tanfra.shopmob.util.monitorActivity
import com.tanfra.shopmob.utils.EspressoIdlingResource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.ui.planning.productList.PlanningProductListViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject
import java.util.*


@RunWith(AndroidJUnit4::class)
@LargeTest
// END TO END test to black box test the app
class SmobPlanningActivityTest: AutoCloseKoinTest() {

    private lateinit var repository: SmobItemDataSource
    private lateinit var _viewModel: PlanningShopEditViewModel
    private lateinit var appContext: Application

    // an idling resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // test data
    private lateinit var testSmobItemATO: SmobItemATO

    // UI Automator - used to click on system elements during test
    // ... see: https://alexzh.com/ui-testing-of-android-runtime-permissions/ for some background
    private val device: UiDevice

    init {
        // set-up UI Automator
        val instrumentation = getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    // introduce a short delay in test execution, after having clicked on user account (using
    // UI Automator) --> need some time to pass, otherwise the test is flaky
    // https://stackoverflow.com/questions/52818524/delay-test-in-espresso-android-without-freezing-main-thread
    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }


    // get activity from a fragmentScenario created with launchInContainer
    // adapted from answer of tanfra question https://knowledge.udacity.com/questions/663647
    private fun getActivity(smobPlanningActivityScenario: ActivityScenario<SmobPlanningActivity>): Activity? {
        var activity: Activity? = null
        smobPlanningActivityScenario.onActivity {
            activity = it
        }
        return activity
    }


    // all permissions granted...
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
    )


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {

        // stop the original app koin
        stopKoin()

        appContext = getApplicationContext()

        val myModule = module {
            viewModel {
                PlanningProductListViewModel(
                    appContext,
                    get() as SmobListDataSource,
                    get() as SmobProductDataSource,
                    get() as SmobShopDataSource
                )
            }
            single {
                PlanningShopEditViewModel(
                    appContext,
                    get() as SmobShopDataSource
                )
            }
            single { SmobItemRepository(get()) }

            // ... expose SmobItemDataSource, so that the fetching ot the repository works with
            //     a simple 'get()' (see below)
            single<SmobItemDataSource> {
                get<SmobItemRepository>()
            }

            // Room DB ----------------------------------------------------------------

            // local DB singleton "Room" object representing smob database
            // ... used as (local) data source for all repositories of the app
            single { LocalDB.createSmobDatabase(appContext) }

            // DAOs -------------------------------------------------------------------

            // DAO to access table smobItems in the above DB (smob.db)
            single { LocalDB.createSmobItemDao(get()) }

            // DataSources ------------------------------------------------------------

            // declare a (singleton) repository service with interface "SmobItemDataSource"
            single<SmobItemDataSource> { SmobItemRepository(get()) }

        }

        // declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }

        // get our real repository (type: SmobItemDataSource - exposed in Koin module, see above)
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllSmobItems()
        }

        // initialize test smob item (used to populate smob item list)
        testSmobItemATO = SmobItemATO(
            "e2e.title",
            "e2e.description",
            "e2e.location",
            46.0,
            24.0,
            UUID.randomUUID().toString()
        )

    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    // E2E testing... SmobItemListFragment
    @Test
    fun smobActivityTest_fragmentSmobItemList() = runBlocking {

        // add a smob item to the repository
        repository.saveSmobItem(testSmobItemATO)

        // startup with the smob item list screen
        //
        // ... done manually here (as opposed to @get:Rule
        //     so we get a chance to initialize the repo first (see above)
        //
        // ... need to launch the *activity* rather than the *fragment* to allow for navigation
        //     to take place (as the activity holds the fragment container)
        val activityScenario = ActivityScenario.launch(SmobPlanningActivity::class.java)

        // monitor activityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // verify that the SmobItemList fragment is in view
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // verify that the smob item from the DB is displayed correctly
        onView(withId(R.id.title)).check(matches(withText(testSmobItemATO.title)))
        onView(withId(R.id.description)).check(matches(withText(testSmobItemATO.description)))
        onView(withId(R.id.location)).check(matches(withText(testSmobItemATO.location)))

        // click on the "add smob item" and travel to SaveSmobItem fragment
        onView(withId(R.id.add_smob_item_fab)).perform(click())

        // verify that we have navigated to the saveSmobItem fragment
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // go back to the SmobItemList fragment
        Espresso.pressBack()

        // verify that we have navigated back to the SmobItemList fragment
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // make sure the activityScenario is closed before resetting the db
        activityScenario.close()

    }


    // E2E testing... SmobItemList --> SmobItemDetailsActivity
    @Test
    fun SmobActivityTest_displaySmobItemDetails() = runBlocking {

        // add a smob item to the repository
        repository.saveSmobItem(testSmobItemATO)

        // startup with the SmobItem screen
        //
        // ... done manually here (as opposed to @get:Rule
        //     so we get a chance to initialize the repo first (see above)
        //
        // ... need to launch the *activity* rather than the *fragment* to allow for navigation
        //     to take place (as the activity holds the fragment container)
        val activityScenario = ActivityScenario.launch(SmobPlanningActivity::class.java)

        // monitor activityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // verify that the SmobItemList fragment is in view
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // verify that the location smob item from the DB is displayed correctly
        onView(withId(R.id.title)).check(matches(withText(testSmobItemATO.title)))

        // click on the smob item to display details
        onView(withId(R.id.title)).perform(click())

        // verify that we have navigated to the SmobItemDescription activity
        onView(withId(R.id.clSmobItemDetails)).check((matches(isDisplayed())))

        // verify that the smob item details are displayed correctly
        onView(withId(R.id.tvTitleText)).check(matches(withText(testSmobItemATO.title)))
        onView(withId(R.id.tvDescText)).check(matches(withText(testSmobItemATO.description)))
        onView(withId(R.id.tvLocText)).check(matches(withText(testSmobItemATO.location)))

        // click 'DISMISS' to back to the SmobItemList fragment
        onView(withId(R.id.btDismiss)).perform(click())

        // verify that we have navigated back to the SmobItemList fragment
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // make sure the activityScenario is closed before resetting the db
        activityScenario.close()

    }


    // E2E testing... SaveSmobItemFragment
    @Test
    fun SmobActivityTest_fragmentSaveSmobItem() = runBlocking {

        // add a smob item to the repository
        repository.saveSmobItem(testSmobItemATO)

        // fetch SaveSmobItemViewModel to set some test data
        _viewModel = inject<PlanningShopEditViewModel>().value
        _viewModel.smobItemSelectedLocationStr.postValue("Espresso test location")

        // startup with the SmobActivity screen (fragment container)
        //
        // ... done manually here (as opposed to @get:Rule
        //     so we get a chance to initialize the repo first (see above)
        //
        // ... need to launch the *activity* rather than the *fragment* to allow for navigation
        //     to take place (as the activity holds the fragment container)
        val activityScenario = ActivityScenario.launch(SmobPlanningActivity::class.java)

        // monitor activityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // verify that the SmobItemList is in view
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // click on the "save smob item" and navigate to SaveSmobItem fragment
        onView(withId(R.id.add_smob_item_fab)).perform(click())

        // verify that the SaveSmobItemFragment is in view
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // fill out the form...
        onView(withId(R.id.smobItemTitle)).perform(clearText(), typeText("Have an espresso..."))
        onView(withId(R.id.smobItemTitle)).check(matches(withText("Have an espresso...")))
        onView(withId(R.id.smobItemDescription)).perform(clearText(), typeText("While we are testing..."))
        onView(withId(R.id.smobItemDescription)).check(matches(withText("While we are testing...")))

        // check location information
        onView(withId(R.id.selectedLocation)).check(matches(withText("Espresso test location")))


        // click on Smob (item) Location - should navigate to select location screen
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.clSelectLocationFragment)).check((matches(isDisplayed())))

        // there should be a user prompt (snackBar)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.map_user_prompt)))

//        // allow foreground access to location data (map)
//        val permissionSettings = device.wait(Until.findObject(
//            By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")
//            //  .text("While using the app")
//        ), 2000)
//
//        // click primary creds label
//        permissionSettings.click()
//
//        // wait a little... to allow user to give permission
//        onView(isRoot()).perform(waitFor(3000))

        // go back
        Espresso.pressBack()
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // click on the "save smob item" and travel to SaveSmobItem fragment
        onView(withId(R.id.saveSmobItem)).perform(click())

        // verify that we have navigated to the saveSmobItem fragment
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // go back to the SmobItemList fragment
        Espresso.pressBack()

        // verify that we have navigated back to the SmobItemList fragment
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // make sure the activityScenario is closed before resetting the db
        activityScenario.close()

    }


    // E2E testing... SelectLocationFragment
    @Test
    fun SmobActivityTest_fragmentSelectLocation() = runBlocking {


        // startup with the SmobActivity screen (fragment container)
        //
        // ... done manually here (as opposed to @get:Rule
        //     so we get a chance to initialize the repo first (see above)
        //
        // ... need to launch the *activity* rather than the *fragment* to allow for navigation
        //     to take place (as the activity holds the fragment container)
        val activityScenario = ActivityScenario.launch(SmobPlanningActivity::class.java)

        // monitor activityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // verify that the SmobItemListFragment is in view
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // click on the "save smob item" and navigate to SaveSmobItem fragment
        onView(withId(R.id.add_smob_item_fab)).perform(click())

        // verify that the SaveSmobItemFragment is in view
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // click on Smob (item) Location - should navigate to select location screen
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.clSelectLocationFragment)).check((matches(isDisplayed())))

        // select location (click wherever I am...)
       onView(withId(R.id.map)).perform(longClick())

        // add location description
        val locationText = "I was here..."
        onView(withId(R.id.etLocationName)).perform(
            clearText(),
            typeText(locationText),
            closeSoftKeyboard(),
        )

        onView(withId(R.id.etLocationName)).check(matches(withText(locationText)))

        // wait a little... to avoid flakiness
        // ... seems justified in some espresso situations, see: https://knowledge.udacity.com/questions/469208
        onView(isRoot()).perform(waitFor(1000))

        // click on OK
        onView(withId(R.id.btnOk)).perform(click())

        // wait a little... to avoid flakiness
        // ... seems justified in some espresso situations, see: https://knowledge.udacity.com/questions/469208
        onView(isRoot()).perform(waitFor(500))

        // check: are we back?
        onView(withId(R.id.clPlanningProductEditFragment)).check((matches(isDisplayed())))

        // has the chosen location been saved?
        onView(withId(R.id.selectedLocation)).check(matches(withText(locationText)))

        // wait a little... to avoid flakiness
        // ... seems justified in some espresso situations, see: https://knowledge.udacity.com/questions/469208
        //onView(isRoot()).perform(waitFor(500))

        // make sure the activityScenario is closed before resetting the db
        activityScenario.close()

    }


    // E2E testing... AuthenticationActivity
    @Test
    fun SmobActivityTest_loginActivity() = runBlocking {

        // add a smob item to the repository
        repository.saveSmobItem(testSmobItemATO)

        // open new activity container w/h class "AuthenticationActivity"
        val authenticationActivityScenario = ActivityScenario.launch(SmobAuthActivity::class.java)

        // monitor authenticationActivityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(authenticationActivityScenario)

        // verify that the login screen is in view
        onView(withId(R.id.main_layout)).check((matches(isDisplayed())))

        // check a few things on the display
        onView(withId(R.id.titleText)).check(matches(withText(R.string.welcome_message)))
        onView(withId(R.id.status)).check(matches(withText(R.string.signed_out)))
        val loginBtn = onView(withId(R.id.auth_button)).check(matches(withText(R.string.login)))

        // click on the login button
        loginBtn.perform(click())

        // click on the user provided email account
        val emailAccount = device.wait(Until.findObject(
            By.res("com.google.android.gms:id/credential_primary_label")
            //  .text("Frank Douvre")
        ), 3000)

        // click primary creds label
        emailAccount.click()

        // wait a little... to remove flakiness
        // ... seems justified in some espresso situations, see: https://knowledge.udacity.com/questions/469208
        onView(isRoot()).perform(waitFor(1000))

        // verify that we have navigated to the SmobItemList fragment
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // make sure the activityScenario is closed before resetting the db
        authenticationActivityScenario.close()

    }


    // LiveData: showErrorMessage (Toast)
    @Test
    fun setError_AddSmobItemFirstMessageIsDisplayed() {

        // startup with the SmobItemsActivity screen (fragment container)
        //
        // ... done manually here (as opposed to @get:Rule
        //     so we get a chance to initialize the repo first (see above)
        //
        // ... need to launch the *activity* rather than the *fragment* to allow for navigation
        //     to take place (as the activity holds the fragment container)
        val activityScenario = ActivityScenario.launch(SmobPlanningActivity::class.java)

        // monitor activityScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // verify that the SmobItemList fragment is in view
        onView(withId(R.id.smobItemsRecyclerView)).check((matches(isDisplayed())))

        // WHEN...
        // ... swiping down on an empty DB - this should display an error message/note (Toast)
        onView(withId(R.id.refreshLayout)).perform(swipeDown())

        // THEN the Toast should be displayed
        // ... ref: answer to https://knowledge.udacity.com/questions/663647
        val daErrorText = getActivity(activityScenario)?.getString(R.string.error_add_smob_items)
        onView(withText(daErrorText)).inRoot(
            RootMatchers.withDecorView(
                CoreMatchers.not(
                    CoreMatchers.`is`(getActivity(activityScenario)?.window?.decorView)
                )
            )
        )
            .check(matches(isDisplayed()))

        // make sure the activityScenario is closed before resetting the db
        activityScenario.close()

    }

}
