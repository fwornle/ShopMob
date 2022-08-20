package com.tanfra.shopmob.smob.ui.planning.shops

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.planning.shops.addNewItem.PlanningShopsAddNewItemFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

import com.tanfra.shopmob.smob.data.local.FakeItemDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.shops.addNewItem.PlanningShopsAddNewItemViewModel
import com.tanfra.shopmob.util.DataBindingIdlingResource
import com.tanfra.shopmob.util.monitorFragment
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.*

import java.util.*


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
// UI Testing
@MediumTest
class SmobItemATOListFragmentTest: AutoCloseKoinTest() {

    // test data for (fake) DB
    private lateinit var shopMobItemATOList: MutableList<SmobItemATO>

    // fake data source (repo)
    private lateinit var shopMobRepo: SmobItemDataSource

    // need to launch a fragment scenario to test it...
    // ... and configure it with the mock(ito)ed NavController
    private lateinit var shopMobPlanningShopListFragementScenario: FragmentScenario<PlanningShopsTableFragment>
    private lateinit var testNavController: NavController

    // an idling resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // (alternatively... but then the repo is not swapped-out to the fake data source)
    //
    // recreate SmobItemsActivity environment (before each rule is run)
    //   @get: Rule
    //    val activityScenarioRule: ActivityScenarioRule<SmobItemsActivity> =
    //        ActivityScenarioRule(SmobItemsActivity::class.java)

    // all permissions granted...
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
    )

    @Before
    fun setUp() {

        // generate some test database items (smob items)
        shopMobItemATOList = mutableListOf<SmobItemATO>()

        // generate some test data
        for (idx in 0..19) {
            shopMobItemATOList.add(
                SmobItemATO(
                    "test title $idx",
                    "test description $idx",
                    "test location $idx",
                    idx.toDouble(),
                    idx.toDouble(),
                    UUID.randomUUID().toString(),
                )
            )
        }

        // get a fresh fake data source (repository)
        shopMobRepo = FakeItemDataSource(shopMobItemATOList)


        /**
         * use Koin Library as a service locator
         */

        // stop the original app koin, which is launched when the application starts (in "MyApp")
        stopKoin()

        // define Koin service locator module with fake data source as repo (linked into the VM)
        val myModule = module {

            // declare a ViewModel - to be injected into Fragment with dedicated injector using
            // "by viewModel()"
            viewModel {
                PlanningViewModel(
                    get(),  // app (context)
                    get() as SmobListDataSource,  // repo as data source
                    get() as SmobProductDataSource,  // repo as data source
                    get() as SmobShopDataSource,  // repo as data source
                )
            }

            // declare a ViewModel - to be injected into Fragment with standard injector using
            // "by inject()"
            // --> this view model is declared singleton to be used across multiple fragments
            //
            // class SaveSmobItemViewModel(
            //    val app: Application,
            //    val dataSource: SmobItemDataSource
            // ) : BaseViewModel(app) { ... }
            single {
                PlanningShopsAddNewItemViewModel(
                    get(),
                    get() as SmobShopDataSource
                )
            }

            // SmobItemDataSource
            //
            // declare a (singleton) repository service with interface "SmobItemDataSource"
            single<SmobItemDataSource> { shopMobRepo }

        }  // myModule

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(myModule))
        }

        // launch smob item list fragment
        // ... using "launchINCONTAINER" to attach the fragment to an (empty) activity
        //     see: https://developer.android.com/guide/fragments/test#declare-dependencies
        shopMobPlanningShopListFragementScenario = launchFragmentInContainer(Bundle(), R.style.AppTheme)

        // monitor fragmentScenario for "idling" (used to flow control the espresso tests)
        dataBindingIdlingResource.monitorFragment(
            shopMobPlanningShopListFragementScenario as FragmentScenario<Fragment>)

        // attach the navController (for navigation tests)
        // ... configure mock to return 'true' on popBackStack
        testNavController = mock(NavController::class.java)
        `when`(testNavController.popBackStack()).thenReturn(true)
        shopMobPlanningShopListFragementScenario.onFragment {
            Navigation.setViewNavController(it.view!!, testNavController)
        }

    }  // setUp()


    // check that RecyclerView is visible
    @Test
    fun isListFragmentVisible_onLaunch() {

        // check if recyclerView is visible (on launch)
        onView(withId(R.id.smobItemsRecyclerView)).check(matches(isDisplayed()))

    }

    // check for specific item from DB
    @Test
    fun itemWithText_doesExist() {

        // index of item in the list to be tested (off screen)
        val testItemIdx = 17
        val testSmobItem = shopMobItemATOList[testItemIdx]

        // attempt to scroll to selected list item
        onView(withId(R.id.smobItemsRecyclerView)) // scrollTo will fail the test if no item matches.
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(testSmobItem.title))
                )
            )
    }

    // check for specific item from DB at corresponding position in RV
    @Test
    fun specificItemWithText_doesExistAtCorrectPosition() {

        // index of item in the list to be tested
        val testItemIdx = 1
        val testSmobItem = shopMobItemATOList[testItemIdx]

        // select a specific item in the list and check title
        onView(withId(R.id.smobItemsRecyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(testItemIdx, scrollTo()))
            .check(matches(hasDescendant(withText(testSmobItem.title))))

    }

    // check for a non-existent smob item item
    @Test(expected = PerformException::class)
    fun itemWithText_doesNotExist() {

        // attempt to scroll to an item that contains the special text.
        onView(withId(R.id.smobItemsRecyclerView)) // scrollTo will fail the test if no item matches.
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("not in the list"))
                )
            )
    }


    /*
    // Dec 2021:  test still fails with API 30+, as there is a currently unresolved problem with
    //            the espresso library, see: https://knowledge.udacity.com/questions/608423
    //
    // LiveData: showErrorMessage
    @Test
    fun setError_ErrorMessageIsDisplayed() = runBlockingTest {

        // GIVEN...
        // ... access to the viewModel (from Koin service locator module)
        val _viewModel = inject<SmobItemsListViewModel>().value

        // WHEN...
        // ... setting liveData value to error message
        //     --> use postValue, as this is on a background thread (within the test environment)
        val testToastText = "some error occurred"
        _viewModel.showErrorMessage.postValue(testToastText)

        // THEN the Toast should be displayed
        // ... onToast method (from ToastMatcher - author: see ToastMatcher.kt)
        onToast(testToastText).check(matches(isDisplayed()))

    }
    */


    // navigation test: SmobItemsList --> SaveSmobItem
    @Test
    fun clickAddSmobItemButton_navigateToSaveSmobItemFragment() {

        // GIVEN - on the SmobItemList fragment
        // ... done centrally in @Before

        // WHEN - click on the "+" button (FAB)
        onView(withId(R.id.add_smob_item_fab)).perform(click())

        // THEN - verify that we navigate to the save smob item screen
        verify(testNavController).navigate(
            SmobItemListFragmentDirections.actionShopMobItemListFragmentToSaveSmobItemFragment()
        )
    }

    // navigation test: saveSmobItemFragment --> SmobItemListFragment
    @Test
    fun clickSaveButton_navigateToSmobItemListFragment() {

        // GIVEN - on the SaveSmobItem fragment
        val saveSmobItemFragmentScenario = launchFragmentInContainer<PlanningShopsAddNewItemFragment>(Bundle(), R.style.AppTheme)
        // ... (with the NavController hooked up to this container)
        saveSmobItemFragmentScenario.onFragment {
            Navigation.setViewNavController(it.view!!, testNavController)
        }

        // WHEN clicking the "SAVE" button (FAB)
        onView(withId(R.id.saveSmobItem)).perform(click())

        // THEN - verify that we navigate back to the SmobItem List screen (via popBackStack)
        //
        // Notes:
        // - this is done by setting the observable liveData '_viewModel.navigationCommand' to
        //   'NavigationCommand.Back' (in private method SaveSmobItemViewModel.saveSmobItem)
        //   ... which triggers 'findNavController().popBackStack()' (see: the implementation
        //   of the 'navigationCommand' liveData observer in BaseFragment.kt)
        // - permissions need to be granted (see @get:Rule, above) to ensure SaveSmobItemFragment
        //   actually calls upon the lambda function which, in turn, calls 'saveSmobItem'
        verify(testNavController).popBackStack()

    }

    // navigation test: saveSmobItemFragment --> selectLocationFragment
    @Test
    fun clickLocation_navigateToSelectLocationFragment() {

        // GIVEN we are on the SaveSmobItem screen
        val saveSmobItemScenario = launchFragmentInContainer<PlanningShopsAddNewItemFragment>(Bundle(), R.style.AppTheme)
        // ... (with the NavController hooked up to this container)
        saveSmobItemScenario.onFragment {
            Navigation.setViewNavController(it.view!!, testNavController)
        }

        // WHEN clicking on the location button (FAB)
        onView(withId(R.id.selectLocation)).perform(click())

        // THEN - verify that we navigate to the select location screen
        verify(testNavController).navigate(
            SmobItemListFragmentDirections.actionShopMobItemListFragmentToSaveSmobItemFragment()
        )

    }

}