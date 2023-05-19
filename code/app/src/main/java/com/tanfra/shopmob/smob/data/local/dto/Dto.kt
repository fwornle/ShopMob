package com.tanfra.shopmob.smob.data.local.dto

import com.tanfra.shopmob.smob.data.types.ItemStatus

/**
 * supertype, common to all DTO types - generic part of any DTO class
 * (properties declared abstract --> implementation delegated to inheriting concrete class)
 */
sealed class Dto {
    abstract var itemId: String
    abstract var itemStatus: ItemStatus
    abstract var itemPosition: Long
}