package com.tanfra.shopmob.smob.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.repo.SmobUserRepository
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.collect
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
    private lateinit var shopMobUserATOList: MutableList<SmobUserATO>
    private lateinit var newSmobUserATO: SmobUserATO

    // fake data source (repo)
    private lateinit var shopMobRepo: SmobUserDataSource

    // fake DB (room, in-memory)
    private lateinit var fakeDB: SmobDatabase
    private lateinit var itemDao: SmobUserDao

    // populate the fake DB / repo
    private suspend fun populateFakeDB() {
        shopMobUserATOList.map {
            shopMobRepo.saveSmobUser(it)
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
        shopMobUserATOList = mutableListOf<SmobUserATO>()

        // generate some test data
        for (idx in 0..19) {
            shopMobUserATOList.add(
                SmobUserATO(
                    UUID.randomUUID().toString(),
                    SmobItemStatus.NEW,
                    -1L,
                    "test federated ID $idx",
                    "test contacts ID $idx",
                    "test username $idx",
                    "test name $idx",
                    "test email $idx",
                    "test imageURL $idx",
                    listOf(),
                )
            )
        }

        // initialize a new smob item for 'saveSmobUser' test
        newSmobUserATO = SmobUserATO(
            UUID.randomUUID().toString(),
            "a new test username",
            "a new test name",
            "a new test email",
            "a new test imageURL",
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
        itemDao = fakeDB.smobUserDao()

        // create repository with DAO of fake DB
        shopMobRepo = SmobUserRepository(
            itemDao,
            Unit as SmobUserApi,  // strictly dummy - do not use
            // ensure Room (& the DAO & the repo) and the test use the same coroutine dispatcher
            testDispatcher,
        )

    }  // setUp()


    /*
     * to be tested: repo class
     *
     *  interface SmobUserDataSource {
     *      suspend fun getSmobUsers(): Resource<List<SmobUserDTO>>
     *      suspend fun saveSmobUser(smobUser: SmobUserDTO)
     *      suspend fun getSmobUser(id: String): Resource<SmobUserDTO>
     *      suspend fun deleteAllSmobUsers()
     *  }
     *
     */

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // test repo interface method 'getSmobUsers'
    // ... use our own TestCoroutineScope (testScope, see above) to ensure that both the test and
    //     Room's DAO functions run in the same scope
    @Test
    fun repository_getSmobUsers_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // successfully fetch smob items from (fake) repo
        shopMobRepo.getAllSmobUsers().collect {

            // should return a success and with some data
            assertThat(it.status, `is`(Status.SUCCESS))
            assertThat(it.data, CoreMatchers.notNullValue())

            // check all data records
            it.data?.mapIndexed { idx, smobUser ->
                // check for equality
                assertThat(smobUser, CoreMatchers.notNullValue())
                assertThat(smobUser.id, `is`(shopMobUserATOList[idx].id))
                assertThat(smobUser.username, `is`(shopMobUserATOList[idx].username))
                assertThat(smobUser.name, `is`(shopMobUserATOList[idx].name))
                assertThat(smobUser.email, `is`(shopMobUserATOList[idx].email))
                assertThat(smobUser.imageUrl, `is`(shopMobUserATOList[idx].imageUrl))
            }
        }


    }


    // test repo interface method 'getSmobUser' - existing smob item
    @Test
    fun repository_getSmobUser_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // fetch specific smob item from (fake) repo
        val idx = 4

        // successfully fetch smob item from (fake) repo
        shopMobRepo.getSmobUser(shopMobUserATOList[idx].id).collect {

            // should return a success and with some data
            assertThat(it.status, `is`(Status.SUCCESS))
            assertThat(it.data, CoreMatchers.notNullValue())

            // check for equality
            val smobUser = it.data
            assertThat(smobUser, CoreMatchers.notNullValue())
            assertThat(smobUser?.id, `is`(shopMobUserATOList[idx].id))
            assertThat(smobUser?.username, `is`(shopMobUserATOList[idx].username))
            assertThat(smobUser?.name, `is`(shopMobUserATOList[idx].name))
            assertThat(smobUser?.email, `is`(shopMobUserATOList[idx].email))
            assertThat(smobUser?.imageUrl, `is`(shopMobUserATOList[idx].imageUrl))

        }

    }

    // test repo interface method 'getSmobUser' - non-existing smob item
    @Test
    fun repository_getSmobUser_failure() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // attempt to fetch non-existing smob item from (fake) repo
        val nonId = "this-index-does-not-exist-in-DB"
        shopMobRepo.getSmobUser(nonId).collect {

            assertThat(it.status, `is`(Status.ERROR))
            assertThat(it.message, `is`("SmobUser not found!"))

        }

    }


    // test repo interface method 'deleteAllSmobUsers'
    @Test
    fun repository_deleteAllSmobUsers_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // purge all items from (fake) repo, the read all smob items
        shopMobRepo.deleteAllSmobUsers()

        // read back smob items
        shopMobRepo.getAllSmobUsers().collect {

            // should be valid and empty
            assertThat(it.status, `is`(Status.SUCCESS))
            assertThat(it.data?.size, `is`(0))

        }

    }


    // test repo interface method 'saveSmobUser' - successful
    @Test
    fun repository_saveSmobUser_success() = testScope.runBlockingTest {

        // store test data in fake DB
        populateFakeDB()

        // save new smob item to (fake) repo, then read it back
        shopMobRepo.saveSmobUser(newSmobUserATO)

        shopMobRepo.getSmobUser(newSmobUserATO.id).collect {

            // check that result is valid
            assertThat(it.status, `is`(Status.SUCCESS))

            // check the read back data record
            it.data.let {
                // check for equality
                assertThat(it, CoreMatchers.notNullValue())
                assertThat(it?.id, `is`(newSmobUserATO.id))
                assertThat(it?.username, `is`(newSmobUserATO.username))
                assertThat(it?.name, `is`(newSmobUserATO.name))
                assertThat(it?.email, `is`(newSmobUserATO.email))
                assertThat(it?.imageUrl, `is`(newSmobUserATO.imageUrl))
            }

        }

    }

}