package com.tanfra.shopmob.smob.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.local.dao.SmobItemDao
import com.tanfra.shopmob.smob.data.repo.SmobItemRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.types.SmobItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class SmobRepositoryTest {

    // test data for (fake) DB
    private lateinit var shopMobItemList: MutableList<SmobItem>
    private lateinit var newSmobItem: SmobItem

    // fake data source (repo)
    private lateinit var shopMobRepo: SmobItemDataSource

    // fake DB (room, in-memory)
    private lateinit var fakeDB: SmobDatabase
    private lateinit var itemDao: SmobItemDao

    // populate the fake DB / repo
    private suspend fun populateFakeDB() {
        shopMobItemList.map {
            shopMobRepo.saveSmobItem(it)
        }
    }

    // specify query and transaction executors for Room
    // ... necessary to avoid "java.lang.IllegalStateException: This job has not completed yet"
    //     error, see: https://medium.com/@eyalg/testing-androidx-room-kotlin-coroutines-2d1faa3e674f
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)


    @Before
    fun setUp() {

        // generate some test database items (smob items)
        shopMobItemList = mutableListOf<SmobItem>()

        // generate some test data
        for (idx in 0..19) {
            shopMobItemList.add(
                SmobItem(
                    "test title $idx",
                    "test description $idx",
                    "test location $idx",
                    idx.toDouble(),
                    idx.toDouble(),
                    UUID.randomUUID().toString(),
                )
            )
        }

        // initialize a new smob item for 'saveSmobItem' test
        newSmobItem = SmobItem(
            "a new title",
            "a new test description",
            "a new test location",
            42.0,
            242.0,
            UUID.randomUUID().toString(),
        )

        // create fake datasource ... also using the DAO
        fakeDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SmobDatabase::class.java,
        )
            // use TestCoroutineDispatcher for Room 'transaction executions' - this way, all
            // suspended functions of Room's DAO run inside the same Coroutine scope which is also
            // used for the tests (testScope.runblocking { ... } - see below)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()

        // fetch DAO
        itemDao = fakeDB.smobItemDao()

        // create repository with DAO of fake DB
        shopMobRepo = SmobItemRepository(
            itemDao,
            // ensure Room (& the DAO & the repo) and the test use the same coroutine dispatcher
            testDispatcher,
        )

    }  // setUp()


    /*
     * to be tested: repo class
     *
     *  interface SmobItemDataSource {
     *      suspend fun getSmobItems(): Resource<List<SmobItemDTO>>
     *      suspend fun saveSmobItem(smobItem: SmobItemDTO)
     *      suspend fun getSmobItem(id: String): Resource<SmobItemDTO>
     *      suspend fun deleteAllSmobItems()
     *  }
     *
     */

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // test repo interface method 'getSmobItems'
    // ... use our own TestCoroutineScope (testScope, see above) to ensure that both the test and
    //     Room's DAO functions run in the same scope
    @Test
    fun repository_getSmobItems_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // successfully fetch smob items from (fake) repo
        val result = shopMobRepo.getSmobItems()

        // should return a success and with some data
        assertThat(result.status, `is`(Status.SUCCESS))
        assertThat(result.data, CoreMatchers.notNullValue())

        // check all data records
        result.data?.mapIndexed { idx, smobItem ->
            // check for equality
            assertThat(smobItem, CoreMatchers.notNullValue())
            assertThat(smobItem.id, `is`(shopMobItemList[idx].id))
            assertThat(smobItem.title, `is`(shopMobItemList[idx].title))
            assertThat(smobItem.description, `is`(shopMobItemList[idx].description))
            assertThat(smobItem.location, `is`(shopMobItemList[idx].location))
            assertThat(smobItem.latitude, `is`(shopMobItemList[idx].latitude))
            assertThat(smobItem.longitude, `is`(shopMobItemList[idx].longitude))

        }

    }


    // test repo interface method 'getSmobItem' - existing smob item
    @Test
    fun repository_getSmobItem_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // fetch specific smob item from (fake) repo
        val idx = 4

        // successfully fetch smob item from (fake) repo
        val result = shopMobRepo.getSmobItem(shopMobItemList[idx].id)

        // should return a success and with some data
        assertThat(result.status, `is`(Status.SUCCESS))
        assertThat(result.data, CoreMatchers.notNullValue())

        // check for equality
        val smobItem = result.data
        assertThat(smobItem, CoreMatchers.notNullValue())
        assertThat(smobItem?.id, `is`(shopMobItemList[idx].id))
        assertThat(smobItem?.title, `is`(shopMobItemList[idx].title))
        assertThat(smobItem?.description, `is`(shopMobItemList[idx].description))
        assertThat(smobItem?.location, `is`(shopMobItemList[idx].location))
        assertThat(smobItem?.latitude, `is`(shopMobItemList[idx].latitude))
        assertThat(smobItem?.longitude, `is`(shopMobItemList[idx].longitude))

    }

    // test repo interface method 'getSmobItem' - non-existing smob item
    @Test
    fun repository_getSmobItem_failure() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // attempt to fetch non-existing smob item from (fake) repo
        val nonId = "this-index-does-not-exist-in-DB"
        val result = shopMobRepo.getSmobItem(nonId)

        assertThat(result.status, `is`(Status.ERROR))
        assertThat(result.message, `is`("SmobItem not found!"))

    }


    // test repo interface method 'deleteAllSmobItems'
    @Test
    fun repository_deleteAllSmobItems_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // purge all items from (fake) repo, the read all smob items
        shopMobRepo.deleteAllSmobItems()

        // read back smob items
        val result = shopMobRepo.getSmobItems()

        // should be valid and empty
        assertThat(result.status, `is`(Status.SUCCESS))
        assertThat(result.data?.size, `is`(0))

    }


    // test repo interface method 'saveSmobItem' - successful
    @Test
    fun repository_saveSmobItem_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // save new smob item to (fake) repo, then read it back
        shopMobRepo.saveSmobItem(newSmobItem)

        val result = shopMobRepo.getSmobItem(newSmobItem.id)

        // check that result is valid
        assertThat(result.status, `is`(Status.SUCCESS))

        // check the read back data record
        result.data.let {
            // check for equality
            assertThat(it, CoreMatchers.notNullValue())
            assertThat(it?.id, `is`(newSmobItem.id))
            assertThat(it?.title, `is`(newSmobItem.title))
            assertThat(it?.description, `is`(newSmobItem.description))
            assertThat(it?.location, `is`(newSmobItem.location))
            assertThat(it?.latitude, `is`(newSmobItem.latitude))
            assertThat(it?.longitude, `is`(newSmobItem.longitude))
        }

    }

}