package com.tanfra.shopmob.smob.data

import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// use FakeDataSource that acts as a test double to the LocalDataSource
// inject the smob items stored in this source via the constructor of the class
class FakeUserDataSource(private var smobUserList: MutableList<SmobUserATO>? = mutableListOf()) :
    SmobUserDataSource {

    // test for errors
    private var shouldReturnError = false

    // setter function for error (test) flag
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun getAllSmobUsers(): Flow<Resource<List<SmobUserATO>>> {

        // testing for errors...
        if (shouldReturnError) {
            return flowOf(Resource.error("Test exception", null))
        }

        // return the entire list of smob items from fake local data source... if any
        smobUserList?.let {
            return flowOf(Resource.success(ArrayList(it)))
        }
        return flowOf(Resource.error(
            "Could not fetch smob items from (fake) local storage.", null
        ))
    }

    override suspend fun saveSmobUser(smobUserATO: SmobUserATO) {
        // store provided smob item in fake local data source (list)
        smobUserList?.add(smobUserATO)
    }

    override suspend fun saveSmobUsers(smobUsersATO: List<SmobUserATO>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSmobUser(smobUserATO: SmobUserATO) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSmobUsers(smobUsersATO: List<SmobUserATO>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSmobUser(id: String) {
        TODO("Not yet implemented")
    }

    override fun getSmobUser(id: String): Flow<Resource<SmobUserATO>> {

        // testing for errors...
        if (shouldReturnError) {
            return flowOf(Resource.error("Test exception", null))
        }

        // fetch smob item associated with provided id
        smobUserList?.firstOrNull {it.id == id} ?.let {
            // found it
            return flowOf(Resource.success(it))
        }

        // smob item with ID not found
        return flowOf(Resource.error(
            "SmobUser with ID $id not found in (fake) local storage.", null
        ))
    }

    override suspend fun deleteAllSmobUsers() {
        // empty list to fake deleting all records from local data source (DB)
        smobUserList?.clear()
    }

    override suspend fun refreshDataInLocalDB() {
        TODO("Not yet implemented")
    }

}