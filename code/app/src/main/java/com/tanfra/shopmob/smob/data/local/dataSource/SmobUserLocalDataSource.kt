package com.tanfra.shopmob.smob.data.local.dataSource

import androidx.room.*
import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local Data Access Object for the smobUsers table.
 */
@Dao
interface SmobUserLocalDataSource: SmobItemLocalDataSource<SmobUserDTO> {

    /**
     * @param smobItemId the ID of the smob user
     * @return the smob user object with the smobItemId - or null if the table is empty
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobUsers WHERE userId = :smobItemId")
    override fun getSmobItemById(smobItemId: String): Flow<SmobUserDTO?>

    /**
     * @return all smobUsers - returns an empty list, if the table is empty
     */
    //
    // note: Flow types must not be declared as "suspend"able functions, see the third answer in:
    //       https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    @Query("SELECT * FROM smobUsers ORDER BY userId ASC")
    override fun getSmobItems(): Flow<List<SmobUserDTO>>

    /**
     * @return all smobUsers on a given (SmobGroup) list (= group members).
     */
    //
    // Notes:
    //
    // - Flow types must not be declared as "suspend"able functions, see the third answer in:
    //   https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    //
    // - using INNER JOIN to resolve the data dependency between groups and members at DB level
    //   ... this is necessary to be able to work with Flow<List<SmobUserDTO>> within the app, as
    //   there is no way to transform a Flow<SmobUserDTO> into a Flow<List<SmobUserDTO>>
    //   without first collecting the individual user flows and re-assembling a (then pointless)
    //   flow of List<SmobUserDTO>
    // - using LIKE to perform a regex search on field 'smobGroups.members', as this is a JSON
    //   encoded serialization of the underlying List<SmobUser>
    //
    @Query("SELECT * FROM smobUsers INNER JOIN smobGroups ON smobGroups.groupId=:groupId AND smobGroups.groupMembers LIKE '%' || smobUsers.userId  || '%' ORDER BY smobUsers.userUsername ASC, smobUsers.userItemStatus")
    fun getSmobMembersByGroupId(groupId: String): Flow<List<SmobUserDTO>>


    /**
     * @return all smobUsers on a given (SmobList) list (= list members).
     */
    //
    // Notes:
    //
    // - Flow types must not be declared as "suspend"able functions, see the third answer in:
    //   https://stackoverflow.com/questions/46445964/room-not-sure-how-to-convert-a-cursor-to-this-methods-return-type-which-meth
    //
    // - using INNER JOIN to resolve the data dependency between groups and members at DB level
    //   ... this is necessary to be able to work with Flow<List<SmobUserDTO>> within the app, as
    //   there is no way to transform a Flow<SmobUserDTO> into a Flow<List<SmobUserDTO>>
    //   without first collecting the individual user flows and re-assembling a (then pointless)
    //   flow of List<SmobUserDTO>
    // - using LIKE to perform a regex search on field 'smobGroups.members', as this is a JSON
    //   encoded serialization of the underlying List<SmobUser>
    //
    @Query("SELECT * FROM smobUsers INNER JOIN smobLists ON smobLists.listId=:listId INNER JOIN smobGroups ON smobLists.listGroups LIKE '%' || smobGroups.groupId || '%' ORDER BY smobUsers.userName, smobUsers.userItemStatus")
    fun getSmobMembersByListId(listId: String): Flow<List<SmobUserDTO>>

    /**
     * Insert a smob user in the database. If the smob user already exists, replace it.
     *
     * @param smobItem the smob user to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveSmobItem(smobItem: SmobUserDTO)

    /**
     * Update an existing smob user in the database. If the smob user already exists, replace it.
     * If not, do nothing.
     *
     * @param smobItem the smob user to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateSmobItem(smobItem: SmobUserDTO)

    /**
     * Delete a smob user in the database.
     *
     * @param smobItemId the ID of the smob user
     */
    @Query("DELETE FROM smobUsers WHERE userId = :smobItemId")
    override suspend fun deleteSmobItemById(smobItemId: String)

    // Delete all smobUsers.
    @Query("DELETE FROM smobUsers")
    override suspend fun deleteSmobItems()

}