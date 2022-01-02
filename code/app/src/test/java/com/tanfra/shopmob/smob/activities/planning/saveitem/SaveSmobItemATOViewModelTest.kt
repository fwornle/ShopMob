package com.tanfra.shopmob.smob.activities.planning.saveitem

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.FakeItemDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.testutils.MainCoroutineRule
import com.tanfra.shopmob.smob.testutils.getOrAwaitValue
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import java.lang.reflect.Method
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveSmobItemATOViewModelTest: AutoCloseKoinTest() {

    // declare globally used variables
    private lateinit var smobDataATO: SmobItemATO
    private lateinit var privateTestFun: Method

    // viewModel
    private lateinit var _viewModelSmob: SaveSmobItemViewModel

    // smob item repository and fake data
    private lateinit var smobItemRepo: SmobItemDataSource
    private lateinit var smobItemATOList: MutableList<SmobItemATO>
    private lateinit var smobItemATONew: SmobItemATO

    
    // test liveData
    //    @get:Rule
    //    var instantExecutorRule = InstantTaskExecutorRule()

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

        // re-initialize smobItemData with a valid data record
        smobDataATO = SmobItemATO(
            "test title",
            "test description",
            "test location",
            1.0,
            2.0,
            UUID.randomUUID().toString(),
        )

        // generate some test database items (smob items)
        smobItemATOList = mutableListOf<SmobItemATO>()
        smobItemATOList.add(
            SmobItemATO(
                "test title 1",
                "test description 1",
                "test location 1",
                1.0,
                1.0,
                UUID.randomUUID().toString(),
            )
        )
        smobItemATOList.add(
            SmobItemATO(
                "test title 2",
                "test description 2",
                "test location 2",
                2.0,
                2.0,
                UUID.randomUUID().toString(),
            )
        )
        smobItemATOList.add(
            SmobItemATO(
                "test title 3",
                "test description 3",
                "test location 3",
                3.0,
                3.0,
                UUID.randomUUID().toString(),
            )
        )

        // new entry to be added to the repo
        smobItemATONew = SmobItemATO(
            "test title 4",
            "test description 4",
            "test location 4",
            4.0,
            4.0,
            UUID.randomUUID().toString(),
        )

        // get a fresh fake data source (repository)
        smobItemRepo = FakeItemDataSource(smobItemATOList)

        // get a fresh viewModel
        _viewModelSmob = SaveSmobItemViewModel(
            ApplicationProvider.getApplicationContext(),
            smobItemRepo,
        )

    }  // setupTest()


    /* ******************************************************************
     * test suite for: validateEnteredData (private method)
     * ******************************************************************/
    @Test
    fun `validateEnteredData returns false if title is missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN validating the data item with the following 'impairment'
        smobDataATO.title = null

        // THEN false should be returned
        Assert.assertEquals(false, privateTestFun(_viewModelSmob, smobDataATO))

    }

    @Test
    fun `validateEnteredData returns false if location is missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN validating the data item with the following 'impairment'
        smobDataATO.location = ""

        // THEN false should be returned
        Assert.assertEquals(false, privateTestFun(_viewModelSmob, smobDataATO))

    }

    @Test
    fun `validateEnteredData returns false if both title and location are missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN validating the data item with the following 'impairment'
        smobDataATO.title = null
        smobDataATO.location = ""

        // THEN false should be returned
        Assert.assertEquals(false, privateTestFun(_viewModelSmob, smobDataATO))

    }

    @Test
    fun `validateEnteredData returns true if neither title nor location are missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN validating the data item with the following 'impairment'
        // -- none --

        // THEN true should be returned
        Assert.assertEquals(true, privateTestFun(_viewModelSmob, smobDataATO))

    }

    /* ******************************************************
     * combined test - validate and save smob items
     * ******************************************************/
    @Test
    fun `validateAndSaveSmobItem stores valid smob item in repository`()  =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... some VALID smob item

        // WHEN calling function validateAndSaveSmobItem
        _viewModelSmob.validateAndSaveSmobItem(smobDataATO)
        val smobItemReadBack = smobItemRepo.getSmobItem(smobDataATO.id)

        // check that result is valid
        assertThat(smobItemReadBack.status, CoreMatchers.`is`(Status.SUCCESS))

        // THEN the smob item is verified and stored in the repository
        assertThat(smobItemReadBack.data?.title, IsEqual(smobDataATO.title))
        assertThat(smobItemReadBack.data?.description, IsEqual(smobDataATO.description))
        assertThat(smobItemReadBack.data?.location, IsEqual(smobDataATO.location))
        assertThat(smobItemReadBack.data?.latitude, IsEqual(smobDataATO.latitude))
        assertThat(smobItemReadBack.data?.longitude, IsEqual(smobDataATO.longitude))
        assertThat(smobItemReadBack.data?.id, IsEqual(smobDataATO.id))

    }

    @Test
    fun `validateAndSaveSmobItem refused to store invalid smob item in repository`()  =
        mainCoroutineRule.runBlockingTest {

        // GIVEN...
        // ... some INVALID smob item
        smobDataATO.title = null

        // WHEN calling function validateAndSaveSmobItem
        _viewModelSmob.validateAndSaveSmobItem(smobDataATO)
        val smobItemReadBack = smobItemRepo.getSmobItem(smobDataATO.id)

        // check that result is invalid
        assertThat(smobItemReadBack.status, CoreMatchers.`is`(Status.ERROR))

        // THEN the smob item is verified and stored in the repository
        assertThat(smobItemReadBack.message, IsEqual("SmobItem with ID ${smobDataATO.id} not found in (fake) local storage."))

    }

    /* ******************************************************
     * liveData testing
     * ******************************************************/

    // LiveData: snackBarInt
    @Test
    fun `validateEnteredData triggers single event showSnackBarInt when title is missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN...
        // ... validating the data item with the following 'impairment' and after triggering the
        //     method to be tested (private --> indirection via reflected method for access)
        smobDataATO.title = ""
        privateTestFun(_viewModelSmob, smobDataATO)

        // THEN the snackbar event (showSnackBarInt) should be triggered
        //
        // --> use LiveData extension function 'getOrAwaitValue' to fetch LiveData value of
        //     SingleEvent 'showSnackBarInt' or return with an error after 2 seconds (timeout)
        // --> using assertThat from hamcrest library directly (as org.junit.* 'indirection' has
        //     been deprecated
        assertThat(_viewModelSmob.showSnackBarInt.getOrAwaitValue(), equalTo(R.string.err_enter_title))

    }

    // LiveData: snackBarInt
    @Test
    fun `validateEnteredData triggers single event showSnackBarInt when location is missing`() {

        // GIVEN...
        // ... access to the PRIVATE method to be tested via REFLECTION (see:
        //     https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd)
        privateTestFun = _viewModelSmob.javaClass
            .getDeclaredMethod("validateEnteredData", smobDataATO.javaClass)
            .apply { isAccessible = true }

        // WHEN...
        // ... validating the data item with the following 'impairment' and after triggering the
        //     method to be tested (private --> indirection via reflected method for access)
        smobDataATO.location = ""
        privateTestFun(_viewModelSmob, smobDataATO)

        // THEN the snackbar event (showSnackBarInt) should be triggered
        //
        // --> use LiveData extension function 'getOrAwaitValue' to fetch LiveData value of
        //     SingleEvent 'showSnackBarInt' or return with an error after 2 seconds (timeout)
        // --> using assertThat from hamcrest library directly (as org.junit.* 'indirection' has
        //     been deprecated
        assertThat(_viewModelSmob.showSnackBarInt.getOrAwaitValue(), equalTo(R.string.err_select_location))

    }

    // LiveData: smobItemTitle
    @Test
    fun `setting value in MutableLiveData smobItemTitle triggers LiveData observer`() {

        // WHEN...
        // ... setting the value of the LiveData element to be tested
        _viewModelSmob.smobItemTitle.value = "test"

        // THEN the associated LiveData observer should be triggered
        assertThat(_viewModelSmob.smobItemTitle.getOrAwaitValue(), equalTo("test"))

    }

    // LiveData: smobItemDescription
    @Test
    fun `setting value in MutableLiveData smobItemDescription triggers LiveData observer`() {

        // WHEN...
        // ... setting the value of the LiveData element to be tested
        _viewModelSmob.smobItemDescription.value = "test"

        // THEN the associated LiveData observer should be triggered
        assertThat(_viewModelSmob.smobItemDescription.getOrAwaitValue(), equalTo("test"))

    }

    // LiveData: smobItemSelectedLocationStr
    @Test
    fun `setting value in MutableLiveData smobItemSelectedLocationStr triggers LiveData observer`() {

        // WHEN...
        // ... setting the value of the LiveData element to be tested
        _viewModelSmob.smobItemSelectedLocationStr.value = "test"

        // THEN the associated LiveData observer should be triggered
        assertThat(_viewModelSmob.smobItemSelectedLocationStr.getOrAwaitValue(), equalTo("test"))

    }

    // LiveData: latitude
    @Test
    fun `setting value in MutableLiveData smobItemLatitude triggers LiveData observer`() {

        // WHEN...
        // ... setting the value of the LiveData element to be tested
        _viewModelSmob.smobItemlatitude.value = 1.0

        // THEN the associated LiveData observer should be triggered
        assertThat(_viewModelSmob.smobItemlatitude.getOrAwaitValue(), equalTo(1.0))

    }

    // LiveData: longitude
    @Test
    fun `setting value in MutableLiveData smobItemLongitude triggers LiveData observer`() {

        // WHEN...
        // ... setting the value of the LiveData element to be tested
        _viewModelSmob.smobItemlongitude.value = 1.0

        // THEN the associated LiveData observer should be triggered
        assertThat(_viewModelSmob.smobItemlongitude.getOrAwaitValue(), equalTo(1.0))

    }

    // LiveData: onClear
    @Test
    fun `calling onClear triggers all LiveData observers and sets values to null`() {

        // WHEN...
        // ... all LiveData element are set to non-null values and
        _viewModelSmob.smobItemTitle.value = "test"
        _viewModelSmob.smobItemDescription.value = "test"
        _viewModelSmob.smobItemSelectedLocationStr.value = "test"
        _viewModelSmob.smobItemlatitude.value = 1.0
        _viewModelSmob.smobItemlongitude.value = 1.0

        // ... onClear is called
        _viewModelSmob.onClear()

        // THEN all associated LiveData observers should be triggered and the values should be null
        assertThat(_viewModelSmob.smobItemTitle.getOrAwaitValue(), equalTo(null))
        assertThat(_viewModelSmob.smobItemDescription.getOrAwaitValue(), equalTo(null))
        assertThat(_viewModelSmob.smobItemSelectedLocationStr.getOrAwaitValue(), equalTo(null))
        assertThat(_viewModelSmob.smobItemlatitude.getOrAwaitValue(), equalTo(null))
        assertThat(_viewModelSmob.smobItemlongitude.getOrAwaitValue(), equalTo(null))

    }


    // test repository ------------------------------------------------------------

    // getSmobItems
    @Test
    fun `getSmobItems requests all smob items from local data source`() =
        mainCoroutineRule.runBlockingTest {

        // WHEN smob items are requested from the repository / location smob item repository
        val smobItem = smobItemRepo.getSmobItems()


        // THEN smob items are loaded from the local data source
        assertThat(smobItem.data, IsEqual(smobItemATOList))

    }

    // getSmobItem --> Resource.success
    @Test
    fun `getSmobItem requests existing smob item from repository`() =
        mainCoroutineRule.runBlockingTest {

        // WHEN an existent smob item is requested from the location smob item repository
        val smobItem = smobItemRepo.getSmobItem(smobItemATOList.first().id)

        // check that result is valid
        assertThat(smobItem.status, CoreMatchers.`is`(Status.SUCCESS))

        // THEN this smob item is loaded from the repository / location smob item repository
        assertThat(smobItem.data, IsEqual(smobItemATOList.first()))

    }

    // getSmobItem --> Resource.error
    @Test
    fun `getSmobItem requests non-existing smob item from repository`() =
        mainCoroutineRule.runBlockingTest {

        // WHEN a non-existent smob item is requested from the location smob item repository
        val fakeId = "this is a fake ID"
        val noSmobItem = smobItemRepo.getSmobItem(fakeId)

        // check that result is valid
        assertThat(noSmobItem.status, CoreMatchers.`is`(Status.ERROR))

        // THEN the return value is an error message
        assertThat(noSmobItem.message, IsEqual("SmobItem with ID $fakeId not found in (fake) local storage."))

    }

    // saveSmobItem
    @Test
    fun `saveSmobItem writes new smob item to repository`() =
        mainCoroutineRule.runBlockingTest {

        // WHEN a new smob item is added to the location smob item repository
        smobItemRepo.saveSmobItem(smobItemATONew)
        val smobItemReadBack = smobItemRepo.getSmobItem(smobItemATONew.id)

        // THEN this smob item is stored in the repository

        // check that result is valid
        assertThat(smobItemReadBack.status, CoreMatchers.`is`(Status.SUCCESS))
        assertThat(smobItemReadBack.data, IsEqual(smobItemATONew))

    }

    // deleteAllSmobItems
    @Test
    fun `deleteAllSmobItems deletes all smobs from repository`() =
        mainCoroutineRule.runBlockingTest {

        // WHEN all smob items are deleted from the location smob items repository
        smobItemRepo.deleteAllSmobItems()

        // THEN the repository is empty
        val smobItemReadBack = smobItemRepo.getSmobItems()

        // check that result is valid
        assertThat(smobItemReadBack.status, CoreMatchers.`is`(Status.SUCCESS))
        assertThat(smobItemReadBack.data, Is(empty()))

    }

}