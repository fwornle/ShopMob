package com.tanfra.shopmob.smob.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tanfra.shopmob.smob.data.SmobItemDataSource
import com.tanfra.shopmob.smob.data.dto.SmobItemDTO
import com.tanfra.shopmob.smob.data.dto.Result
import com.tanfra.shopmop.smob.data.local.SmobItemsLocalRepository
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
class SmobItemsLocalRepositoryTest {

    // test data for (fake) DB
    private lateinit var shopMobItemDtoList: MutableList<SmobItemDTO>
    private lateinit var newSmobItemDTO: SmobItemDTO

    // fake data source (repo)
    private lateinit var shopMobItemRepo: SmobItemDataSource

    // fake DB (room, in-memory)
    private lateinit var fakeDB: SmobItemDatabase
    private lateinit var dao: SmobItemDao

    // populate the fake DB / repo
    private suspend fun populateFakeDB() {
        shopMobItemDtoList.map {
            shopMobItemRepo.saveSmobItem(it)
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
        shopMobItemDtoList = mutableListOf<SmobItemDTO>()

        // generate some test data
        for (idx in 0..19) {
            shopMobItemDtoList.add(
                SmobItemDTO(
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
        newSmobItemDTO = SmobItemDTO(
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
            SmobItemDatabase::class.java,
        )
            // use TestCoroutineDispatcher for Room 'transaction executions' - this way, all
            // suspended functions of Room's DAO run inside the same Coroutine scope which is also
            // used for the tests (testScope.runblocking { ... } - see below)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()

        // fetch DAO
        dao = fakeDB.smobItemDao()

        // create repository with DAO of fake DB
        shopMobItemRepo = SmobItemsLocalRepository(
            dao,
            // ensure Room (& the DAO & the repo) and the test use the same coroutine dispatcher
            testDispatcher,
        )

    }  // setUp()


    /*
     * to be tested: repo class
     *
     *  interface SmobItemDataSource {
     *      suspend fun getSmobItems(): Result<List<SmobItemDTO>>
     *      suspend fun saveSmobItem(smobItem: SmobItemDTO)
     *      suspend fun getSmobItem(id: String): Result<SmobItemDTO>
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
        val result = shopMobItemRepo.getSmobItems() as Result.Success<List<SmobItemDTO>>

        // check all data records
        result.data.mapIndexed { idx, smobItemDTO ->
            // check for equality
            assertThat(smobItemDTO, CoreMatchers.notNullValue())
            assertThat(smobItemDTO.id, `is`(shopMobItemDtoList[idx].id))
            assertThat(smobItemDTO.title, `is`(shopMobItemDtoList[idx].title))
            assertThat(smobItemDTO.description, `is`(shopMobItemDtoList[idx].description))
            assertThat(smobItemDTO.location, `is`(shopMobItemDtoList[idx].location))
            assertThat(smobItemDTO.latitude, `is`(shopMobItemDtoList[idx].latitude))
            assertThat(smobItemDTO.longitude, `is`(shopMobItemDtoList[idx].longitude))

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
        val result = shopMobItemRepo
            .getSmobItem(shopMobItemDtoList[idx].id) as Result.Success<SmobItemDTO>
        val smobItemDTO = result.data

        // check for equality
        assertThat(smobItemDTO, CoreMatchers.notNullValue())
        assertThat(smobItemDTO.id, `is`(shopMobItemDtoList[idx].id))
        assertThat(smobItemDTO.title, `is`(shopMobItemDtoList[idx].title))
        assertThat(smobItemDTO.description, `is`(shopMobItemDtoList[idx].description))
        assertThat(smobItemDTO.location, `is`(shopMobItemDtoList[idx].location))
        assertThat(smobItemDTO.latitude, `is`(shopMobItemDtoList[idx].latitude))
        assertThat(smobItemDTO.longitude, `is`(shopMobItemDtoList[idx].longitude))

    }

    // test repo interface method 'getSmobItem' - non-existing smob item
    @Test
    fun repository_getSmobItem_failure() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // attempt to fetch non-existing smob item from (fake) repo
        val nonId = "this-index-does-not-exist-in-DB"
        val result = shopMobItemRepo.getSmobItem(nonId) as Result.Error

        assertThat(result.message, `is`("SmobItem not found!"))

    }


    // test repo interface method 'deleteAllSmobItems'
    @Test
    fun repository_deleteAllSmobItems_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // purge all items from (fake) repo, the read all smob items
        shopMobItemRepo.deleteAllSmobItems()

        // read back smob items
        val result = shopMobItemRepo.getSmobItems() as Result.Success<List<SmobItemDTO>>

        // should be empty
        assertThat(result.data.size, `is`(0))

    }


    // test repo interface method 'saveSmobItem' - successful
    @Test
    fun repository_saveSmobItem_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // save new smob item to (fake) repo, then read it back
        shopMobItemRepo.saveSmobItem(newSmobItemDTO)

        val result = shopMobItemRepo.getSmobItem(newSmobItemDTO.id) as Result.Success<SmobItemDTO>

        // check the read back data record
        result.data.let {
            // check for equality
            assertThat(it, CoreMatchers.notNullValue())
            assertThat(it.id, `is`(newSmobItemDTO.id))
            assertThat(it.title, `is`(newSmobItemDTO.title))
            assertThat(it.description, `is`(newSmobItemDTO.description))
            assertThat(it.location, `is`(newSmobItemDTO.location))
            assertThat(it.latitude, `is`(newSmobItemDTO.latitude))
            assertThat(it.longitude, `is`(newSmobItemDTO.longitude))
        }

    }

}