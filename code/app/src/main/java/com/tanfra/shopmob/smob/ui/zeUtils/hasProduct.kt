package com.tanfra.shopmob.smob.ui.zeUtils

import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

// extension function of class SmobShopATO to establish, if a shop sells a particular item
fun SmobShopATO.hasProduct(productMainCategory: ProductMainCategory): Boolean {

    // the logic implemented here could eben be shop specific
    return when(this.category) {
        ShopCategory.BAKERY -> {
            when(productMainCategory) {
                ProductMainCategory.FOODS,
                -> true
                else -> false
            }
        }
        ShopCategory.ACCESSORIES -> {
            when(productMainCategory) {
                ProductMainCategory.SUPPLIES,
                ProductMainCategory.OTHER,
                -> true
                else -> false
            }
        }
        ShopCategory.CLOTHING -> {
            when(productMainCategory) {
                ProductMainCategory.CLOTHING,
                -> true
                else -> false
            }
        }
        ShopCategory.DRUGSTORE -> {
            when(productMainCategory) {
                ProductMainCategory.SUPPLIES,
                ProductMainCategory.OTHER,
                -> true
                else -> false
            }
        }
        ShopCategory.FURNITURE -> {
            when(productMainCategory) {
                ProductMainCategory.OTHER,
                -> true
                else -> false
            }
        }
        ShopCategory.HARDWARE -> {
            when(productMainCategory) {
                ProductMainCategory.HARDWARE,
                -> true
                else -> false
            }
        }
        ShopCategory.SUPERMARKET -> {
            when(productMainCategory) {
                ProductMainCategory.FOODS,
                ProductMainCategory.HARDWARE,
                ProductMainCategory.SUPPLIES,
                ProductMainCategory.CLOTHING,
                -> true
                else -> false
            }
        }
        ShopCategory.SUPPLIES -> {
            when(productMainCategory) {
                ProductMainCategory.SUPPLIES,
                ProductMainCategory.OTHER,
                -> true
                else -> false
            }
        }

        // default catch branch... in case we have forgotten any shop type
        // ...be optimistic, it may just have it
        else -> true

    }

}