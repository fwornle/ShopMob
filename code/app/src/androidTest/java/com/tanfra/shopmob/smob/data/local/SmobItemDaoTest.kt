package com.tanfra.shopmob.smob.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.tanfra.shopmob.smob.data.local.dao.SmobItemDao
import com.tanfra.shopmob.smob.data.local.dto.SmobItemDTO

import org.junit.Before
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SmobItemDaoTest {

    // fake DB (room, in-memory)
    private lateinit var fakeDB: SmobDatabase
    private lateinit var itemDao: SmobItemDao

    // test data for (fake) DB
    private lateinit var shopMobItemDto: SmobItemDTO


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
        itemDao = fakeDB.smobItemDao()

        // test database item
        shopMobItemDto = SmobItemDTO(
                "test title 1",
                "test description 1",
                "test location 1",
                1.0,
                1.0,
                UUID.randomUUID().toString(),
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
    fun saveSmobItem_storesDataInDB() = runBlockingTest {

        // store one smob item in (fake) DB
        itemDao.saveSmobItem(shopMobItemDto)

        // read it back
        val readBackSmobItem = itemDao.getSmobItemById(shopMobItemDto.id)

        // check for equality
        assertThat(readBackSmobItem, notNullValue())
        assertThat(readBackSmobItem?.id, `is`(shopMobItemDto.id))
        assertThat(readBackSmobItem?.title, `is`(shopMobItemDto.title))
        assertThat(readBackSmobItem?.description, `is`(shopMobItemDto.description))
        assertThat(readBackSmobItem?.location, `is`(shopMobItemDto.location))
        assertThat(readBackSmobItem?.latitude, `is`(shopMobItemDto.latitude))
        assertThat(readBackSmobItem?.longitude, `is`(shopMobItemDto.longitude))

    }


}

