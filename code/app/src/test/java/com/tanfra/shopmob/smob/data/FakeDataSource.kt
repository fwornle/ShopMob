package com.tanfra.shopmob.smob.data

import com.tanfra.shopmob.smob.data.dto.SmobItemDTO
import com.tanfra.shopmob.smob.data.dto.Result

// use FakeDataSource that acts as a test double to the LocalDataSource
// inject the smob items stored in this source via the constructor of the class
class FakeDataSource(var smobItems: MutableList<SmobItemDTO>? = mutableListOf()) : SmobItemDataSource {

    // test for errors
    private var shouldReturnError = false

    // setter function for error (test) flag
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getSmobItems(): Result<List<SmobItemDTO>> {

        // testing for errors...
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }

        // return the entire list of smob items from fake local data source... if any
        smobItems?.let {
            return Result.Success(ArrayList(it))
        }
        return Result.Error(
            "Could not fetch smob items from (fake) local storage."
        )
    }

    override suspend fun saveSmobItem(smobItem: SmobItemDTO) {
        // store provided smob item in fake local data source (list)
        smobItems?.add(smobItem)
    }

    override suspend fun getSmobItem(id: String): Result<SmobItemDTO> {

        // testing for errors...
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }

        // fetch smob item associated with provided id
        smobItems?.firstOrNull {it.id == id} ?.let {
            // found it
            return Result.Success(it)
        }

        // smob item with ID not found
        return Result.Error(
            "SmobItem with ID $id not found in (fake) local storage."
        )
    }

    override suspend fun deleteAllSmobItems() {
        // empty list to fake deleting all records from local data source (DB)
        smobItems?.clear()
    }

}