package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobProductWithListDataATO")
data class SmobProductWithListDataATO(
    override val id: String,
    override var status: ItemStatus,
    override var position: Long,
    var productName: String,
    var productDescription: String?,
    var productImageUrl: String?,
    var productCategory: ProductCategory,
    var productActivity: ActivityStatus,
    var productInShop: InShop,
    val listId: String,
    val listStatus: ItemStatus,
    val listPosition: Long,
    var listName: String,
    var listDescription: String?,
    var listItems: List<SmobListItem>,
    var listGroups: List<SmobGroupItem>,
    var listLifecycle: SmobListLifecycle,
) : Ato {

    // extract product
    fun product() = SmobProductATO(
        this.id,
        this.status,
        this.position,
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