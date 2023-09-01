package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ProductSubCategory
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobGroupItem
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobProductWithListDataATO")
data class SmobProductWithListDataATO(
    override val id: String = "invalid smob product with list data id",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1L,
    var productName: String = "invalidName",
    var productDescription: String? = "invalidDescription",
    var productImageUrl: String? = "invalidURL",
    var productCategory: ProductCategory = ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER),
    var productActivity: ActivityStatus = ActivityStatus("invalidDate", 0),
    var productInShop: InShop = InShop(ShopCategory.OTHER, "invalidShopName", ShopLocation(0.0, 0.0)),
    val listId: String = "invalidListID",
    val listStatus: ItemStatus = ItemStatus.INVALID,
    val listPosition: Long = -1L,
    var listName: String = "invalidListName",
    var listDescription: String? = "invalidListDescription",
    var listItems: List<SmobListItem> = listOf(),
    var listGroups: List<SmobGroupItem> = listOf(),
    var listLifecycle: SmobListLifecycle = SmobListLifecycle(ItemStatus.INVALID, 0.0),
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