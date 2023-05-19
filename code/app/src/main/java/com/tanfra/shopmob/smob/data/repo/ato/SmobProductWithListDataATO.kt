package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.local.utils.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
data class SmobProductWithListDataATO(
    override val itemId: String,
    override var itemStatus: @Contextual SmobItemStatus,
    override var itemPosition: Long,
    var productName: String,
    var productDescription: String?,
    var productImageUrl: String?,
    var productCategory: @Contextual ProductCategory,
    var productActivity: @Contextual ActivityStatus,
    var productInShop: @Contextual InShop,
    val listId: String,
    val listStatus: @Contextual SmobItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<@Contextual SmobListItem>,
    var listGroups: List<SmobGroupItem>,
    var listLifecycle: @Contextual SmobListLifecycle,
) : Ato, java.io.Serializable {

    // extract product
    fun product() = SmobProductATO(
        this.itemId,
        this.itemStatus,
        this.itemPosition,
        this.productName,
        this.productDescription,
        this.productImageUrl,
        this.productCategory,
        this.productActivity,
        this.productInShop,
    )

    // extract list
    fun list() = SmobListATO(
        this.listId,
        this.listStatus,
        this.listPosition,
        this.listName,
        this.listDescription,
        this.listItems,
        this.listGroups,
        this.listLifecycle,
    )

}