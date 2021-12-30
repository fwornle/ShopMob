package com.tanfra.shopmob.smob.data

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.types.SmobItem

// use FakeDataSource that acts as a test double to the LocalDataSource
// inject the smob items stored in this source via the constructor of the class
class FakeItemDataSource(private var smobItems: MutableList<SmobItem>? = mutableListOf()) :
    SmobItemDataSource {

    // test for errors
    private var shouldReturnError = false

    // setter function for error (test) flag
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getSmobItems(): Resource<List<SmobItem>> {

        // testing for errors...
        if (shouldReturnError) {
            return Resource.error("Test exception", null)
        }

        // return the entire list of smob items from fake local data source... if any
        smobItems?.let {
            return Resource.success(ArrayList(it))
        }
        return Resource.error(
            "Could not fetch smob items from (fake) local storage.", null
        )
    }

    override suspend fun saveSmobItem(smobItem: SmobItem) {
        // store provided smob item in fake local data source (list)
        smobItems?.add(smobItem)
    }

    override suspend fun getSmobItem(id: String): Resource<SmobItem> {

        // testing for errors...
        if (shouldReturnError) {
            return Resource.error("Test exception", null)
        }

        // fetch smob item associated with provided id
        smobItems?.firstOrNull {it.id == id} ?.let {
            // found it
            return Resource.success(it)
        }

        // smob item with ID not found
        return Resource.error(
            "SmobItem with ID $id not found in (fake) local storage.", null
        )
    }

    override suspend fun deleteAllSmobItems() {
        // empty list to fake deleting all records from local data source (DB)
        smobItems?.clear()
    }

}