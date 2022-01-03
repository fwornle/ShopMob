package com.tanfra.shopmob.smob.data.repo.ato

import kotlinx.serialization.Serializable

// base class for all ATO data types - first step towards generics
@Serializable
abstract class Ato {
    abstract val id: String
}
