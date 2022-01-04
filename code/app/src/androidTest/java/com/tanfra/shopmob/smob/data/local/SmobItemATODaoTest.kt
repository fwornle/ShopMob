package com.tanfra.shopmob.smob.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO

import org.junit.Before
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
// unit test the DAO
@SmallTest
class SmobUserATODaoTest {

    // fake DB (room, in-memory)
    private lateinit var fakeDB: SmobDatabase
    private lateinit var itemDao: SmobUserDao

    // test data for (fake) DB
    private lateinit var shopMobUserDto: SmobUserDTO


    // testing "architecture components" --> execute everything synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        // create fake datasource
        fakeDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SmobDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

        // fetch DAO
        itemDao = fakeDB.smobUserDao()

        // test database item
        shopMobUserDto = SmobUserDTO(
            UUID.randomUUID().toString(),
            "test username 1",
            "test name 1",
            "test email 1",
            "test imageURL 1",
            )

    }

    @After
    fun tearDown() {
        fakeDB.close()
    }

    /**
     * check insertion of smob item into the DB
     */
    @Test
    fun saveSmobUser_storesDataInDB() = runBlockingTest {

        // store one smob item in (fake) DB
        itemDao.saveSmobUser(shopMobUserDto)

        // read it back
        val readBackSmobUser = itemDao.getSmobUserById(shopMobUserDto.id)

        // check for equality
        readBackSmobUser.collect {
            assertThat(it, notNullValue())
            assertThat(it?.id, `is`(shopMobUserDto.id))
            assertThat(it?.username, `is`(shopMobUserDto.username))
            assertThat(it?.name, `is`(shopMobUserDto.name))
            assertThat(it?.email, `is`(shopMobUserDto.email))
            assertThat(it?.imageUrl, `is`(shopMobUserDto.imageUrl))
        }

    }

}

