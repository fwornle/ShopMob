package com.tanfra.shopmob.smob.smoblist

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tanfra.shopmob.smob.data.FakeItemDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.testutils.MainCoroutineRule
import com.tanfra.shopmob.smob.testutils.getOrAwaitValue

import com.tanfra.shopmob.smob.types.SmobItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.Shadows.shadowOf
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SmobItemListViewModelTest: AutoCloseKoinTest() {

    // viewModel
    private lateinit var _viewModel: SmobItemListViewModel

    // smob item repository and fake data
    private lateinit var smobItemRepo: SmobItemDataSource
    private lateinit var smobItemList: MutableList<SmobItem>

    // use our own dispatcher for coroutine testing (swaps out Dispatcher.Main to a version which
    // can be used for testing, where asynchronous tasks should run synchronously)
    //
    // ... see: tanfra Android Kotlin course, lesson 5.4: MainCoroutineRule and Injecting Dispatchers
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // run before each individual test
    @Before
    fun setupTest() {

        // run BEFORE EACH individual test ----------------------------------------

        // generate some test database items (location smob items)
        smobItemList = mutableListOf<SmobItem>()
        smobItemList.add(
            SmobItem(
                "test title 1",
                "test description 1",
                "test location 1",
                1.0,
                1.0,
                UUID.randomUUID().toString(),
            )
        )
        smobItemList.add(
            SmobItem(
                "test title 2",
                "test description 2",
                "test location 2",
                2.0,
                2.0,
                UUID.randomUUID().toString(),
            )
        )
        smobItemList.add(
            SmobItem(
                "test title 3",
                "test description 3",
                "test location 3",
                3.0,
                3.0,
                UUID.randomUUID().toString(),
            )
        )

    }  // setupTest()


    /* ******************************************************************
     * test suite for: loadSmobItems
     * ******************************************************************/

    @Test
    fun `loadSmobItems fetches all smob items from repository and triggers observer of smobItemList`() =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... some data in the (fake) data source
        smobItemRepo = FakeItemDataSource(smobItemList)

        // ... and a fresh viewModel with this data source injected (via constructor)
        _viewModel = SmobItemListViewModel(
            ApplicationProvider.getApplicationContext(),
            smobItemRepo,
        )

        // WHEN calling function loadSmobItems
        _viewModel.loadSmobItems()

        // THEN the smob item is verified and stored in the repository
        assertThat(_viewModel.smobItemList.getOrAwaitValue(), equalTo(smobItemList))

    }

    @Test
    fun `loadSmobItems displays an error message (snackBar) when fetching from the local data source fails`()  =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... a broken (fake) data source (to simulate a read error)
        smobItemRepo = FakeItemDataSource(null)

        // ... and a fresh viewModel with this data source injected (via constructor)
        _viewModel = SmobItemListViewModel(
            ApplicationProvider.getApplicationContext(),
            smobItemRepo,
        )

        // WHEN calling function loadSmobItems
        _viewModel.loadSmobItems()

        // THEN...
        // ... the observer of liveData showSnackBar is triggered (and value set to error message)
        // ... the observer of liveData showNoData is triggered (and value set to true)
        assertThat(_viewModel.showSnackBar.getOrAwaitValue(),
            equalTo("Could not fetch smob items from (fake) local storage."))
        assertThat(_viewModel.showNoData.getOrAwaitValue(),
            equalTo(true))

    }

    // LiveData: force error by setting shouldReturnError to true (fake data source)
    @Test
    fun `shouldReturnError - setting shouldReturnError to true should cause error when reading from DB`() {

        mainCoroutineRule.runBlockingTest {

            // GIVEN...
            // ... a broken (fake) data source (to simulate a read error)
            smobItemRepo = FakeItemDataSource(null)

            // ... and 'simulating an error when reading the smob items from the DB'
            (smobItemRepo as FakeItemDataSource).setReturnError(true)

            // ... and a fresh viewModel with this data source injected (via constructor)
            _viewModel = SmobItemListViewModel(
                ApplicationProvider.getApplicationContext(),
                smobItemRepo,
            )

            // WHEN calling function loadSmobItems
            _viewModel.loadSmobItems()

            // THEN...
            // ... the observer of liveData showSnackBar is triggered (and value set to error message)
            // ... the observer of liveData showNoData is triggered (and value set to true)
            assertThat(
                _viewModel.showSnackBar.getOrAwaitValue(),
                equalTo("Test exception")
            )
            assertThat(
                _viewModel.showNoData.getOrAwaitValue(),
                equalTo(true)
            )

        }

    }

    // test loading spinner
    @Test
    fun `check_loading - loadingSpinner appears and disappears`() {

            // GIVEN...
            // ... some data in the (fake) data source
            smobItemRepo = FakeItemDataSource(smobItemList)

            // ... and a fresh viewModel with this data source injected (via constructor)
            _viewModel = SmobItemListViewModel(
                ApplicationProvider.getApplicationContext(),
                smobItemRepo,
            )

            // WHEN calling function loadSmobItems
            mainCoroutineRule.pauseDispatcher()
            _viewModel.loadSmobItems()

            // check that loading spinner has been started
            assertThat(
                _viewModel.showLoading.getOrAwaitValue(),
                equalTo(true)
            )
            mainCoroutineRule.resumeDispatcher()

            // drain the (roboelectric) main looper
            //
            // http://robolectric.org/blog/2019/06/04/paused-looper/
            // if you see test failures like Main looper has queued unexecuted runnables, you may
            // need to insert shadowOf(getMainLooper()).idle() calls to your test to drain the main
            // Looper. Its recommended to step through your test code with a watch set on
            // Looper.getMainLooper().getQueue() to see the status of the looper queue, to determine
            // the appropriate point to add a shadowOf(getMainLooper()).idle() call.
            shadowOf(Looper.getMainLooper()).idle()

            // check that loading spinner has been stopped
            assertThat(
                _viewModel.showLoading.getOrAwaitValue(),
                equalTo(false)
            )

        }

    // LiveData: force error by setting shouldReturnError to true (fake data source)
    @Test
    fun `shouldReturnError - setting shouldReturnError to true should fail a call to getSmobItem`() {

        mainCoroutineRule.runBlockingTest {

            // GIVEN...
            // ... some data in the (fake) data source
            smobItemRepo = FakeItemDataSource(smobItemList)

            // ... and 'simulating an error when reading the smob item from the DB'
            (smobItemRepo as FakeItemDataSource).setReturnError(true)

            // WHEN trying to read
            val result = smobItemRepo.getSmobItem(smobItemList[0].id) as Resource.error

            // THEN...
            // ... a Resource.error should be returned with message "Test exception"
            assertThat(result.message, equalTo("Test exception"))

        }

    }

}