package com.tanfra.shopmob.smob.data.local.dto

import com.tanfra.shopmob.smob.data.types.ItemStatus

/**
 * supertype, common to all DTO types - generic part of any DTO class
 * (properties declared abstract --> implementation delegated to inheriting concrete class)
 */
sealed class Dto {
    abstract var id: String
    abstract var status: ItemStatus
    abstract var position: Long
}