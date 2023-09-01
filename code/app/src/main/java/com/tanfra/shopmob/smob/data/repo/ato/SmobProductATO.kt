package com.tanfra.shopmob.smob.data.repo.ato

import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ProductSubCategory
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// domain independent data type (Application Transfer Object)
@Serializable
@SerialName("smobProductATO")
data class SmobProductATO(
    override val id: String = "invalid smob product",
    override var status: ItemStatus = ItemStatus.INVALID,
    override var position: Long = -1L,
    var name: String = "invalidName",
    var description: String? = "invalidDescription",
    var imageUrl: String? = "invalidURL",
    var category: ProductCategory = ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER),
    var activity: ActivityStatus = ActivityStatus("invalidDate", 0),
    var inShop: InShop = InShop(ShopCategory.OTHER, "invalidShopName", ShopLocation(0.0, 0.0)),
) : Ato