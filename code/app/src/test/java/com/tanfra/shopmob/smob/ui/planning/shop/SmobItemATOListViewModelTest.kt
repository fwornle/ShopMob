package com.tanfra.shopmob.smob.ui.planning.shop

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tanfra.shopmob.smob.data.FakeUserDataSource
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.testutils.MainCoroutineRule
import com.tanfra.shopmob.smob.testutils.getOrAwaitValue

import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.planning.PlanningProductListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.robolectric.Shadows.shadowOf
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SmobUserATOListViewModelTest: AutoCloseKoinTest() {

    // viewModel
    private var _viewModel: PlanningProductListViewModel by inject()


    // smob item repository and fake data
    private lateinit var smobUserRepo: SmobUserDataSource
    private lateinit var smobUserATOList: MutableList<SmobUserATO>

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
        smobUserATOList = mutableListOf<SmobUserATO>()
        smobUserATOList.add(
            SmobUserATO(
                UUID.randomUUID().toString(),
                "test username 1",
                "test name 1",
                "test email 1",
                "test imageUrl 1",
            )
        )
        smobUserATOList.add(
            SmobUserATO(
                UUID.randomUUID().toString(),
                "test username 2",
                "test name 2",
                "test email 2",
                "test imageUrl 2",
            )
        )
        smobUserATOList.add(
            SmobUserATO(
                UUID.randomUUID().toString(),
                "test username 3",
                "test name 3",
                "test email 3",
                "test imageUrl 3",

            )
        )

    }  // setupTest()


    /* ******************************************************************
     * test suite for: loadSmobUsers
     * ******************************************************************/

    @Test
    fun `loadSmobUsers fetches all smob items from repository and triggers observer of smobUserList`() =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... some data in the (fake) data source
        smobUserRepo = FakeUserDataSource(smobUserATOList)

        // ... and a fresh viewModel with this data source injected (via constructor)
        _viewModel = PlanningShopListViewModel(
            ApplicationProvider.getApplicationContext(),
            smobUserRepo,
        )

        // WHEN calling function loadSmobUsers
        _viewModel.loadSmobUsers()

        // THEN the smob item is verified and stored in the repository
        assertThat(_viewModel._smobList.getOrAwaitValue(), equalTo(smobUserATOList))

    }

    @Test
    fun `loadSmobUsers displays an error message (snackBar) when fetching from the local data source fails`()  =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... a broken (fake) data source (to simulate a read error)
        smobUserRepo = FakeUserDataSource(null)

        // ... and a fresh viewModel with this data source injected (via constructor)
        _viewModel = PlanningShopListViewModel(
            ApplicationProvider.getApplicationContext(),
            smobUserRepo,
        )

        // WHEN calling function loadSmobUsers
        _viewModel.loadSmobUsers()

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
            smobUserRepo = FakeUserDataSource(null)

            // ... and 'simulating an error when reading the smob items from the DB'
            (smobUserRepo as FakeUserDataSource).setReturnError(true)

            // ... and a fresh viewModel with this data source injected (via constructor)
            _viewModel = PlanningShopListViewModel(
                ApplicationProvider.getApplicationContext(),
                smobUserRepo,
            )

            // WHEN calling function loadSmobUsers
            _viewModel.loadSmobUsers()

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
            smobUserRepo = FakeUserDataSource(smobUserATOList)

            // ... and a fresh viewModel with this data source injected (via constructor)
            _viewModel = PlanningShopListViewModel(
                ApplicationProvider.getApplicationContext(),
                smobUserRepo,
            )

            // WHEN calling function loadSmobUsers
            mainCoroutineRule.pauseDispatcher()
            _viewModel.loadSmobUsers()

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
    fun `shouldReturnError - setting shouldReturnError to true should fail a call to getSmobUser`() {

        mainCoroutineRule.runBlockingTest {

            // GIVEN...
            // ... some data in the (fake) data source
            smobUserRepo = FakeUserDataSource(smobUserATOList)

            // ... and 'simulating an error when reading the smob item from the DB'
            (smobUserRepo as FakeUserDataSource).setReturnError(true)

            // WHEN trying to read
            val result = smobUserRepo.getSmobUser(smobUserATOList[0].id)

            // check that result is failed
            assertThat(result.status, CoreMatchers.`is`(Status.ERROR))

            // THEN...
            // ... a Resource.error should be returned with message "Test exception"
            assertThat(result.message, equalTo("Test exception"))

        }

    }

}