package com.tanfra.shopmob.smob.data.types

@JvmInline value class SmobItemPosition(val value: Long) { init { require(value >= -1) } }