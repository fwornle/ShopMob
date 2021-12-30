package com.tanfra.shopmob.smob.data.local

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO

// use FakeDataSource that acts as a test double to the LocalDataSource
// inject the smob items stored in this source via the constructor of the class
class FakeItemDataSource(var smobItemATOES: MutableList<SmobItemATO>? = mutableListOf()) :
    SmobItemDataSource {

    override suspend fun getSmobItems(): Resource<List<SmobItemATO>> {
        // return the entire list of smob items from fake local data source... if any
        smobItemATOES?.let {
            return Resource.success(ArrayList(it))
        }
        return Resource.error(
            "Could not fetch smob items from (fake) local storage.", null
        )
    }

    override suspend fun saveSmobItem(smobItemATO: SmobItemATO) {
        // store provided smob item in fake local data source (list)
        smobItemATOES?.add(smobItemATO)
    }

    override suspend fun getSmobItem(id: String): Resource<SmobItemATO> {
        // fetch smob item associated with provided id
        smobItemATOES?.firstOrNull {it.id == id} ?.let {
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
        smobItemATOES?.clear()
    }

}