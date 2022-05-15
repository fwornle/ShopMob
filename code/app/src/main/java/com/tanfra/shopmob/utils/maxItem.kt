package com.tanfra.shopmob.utils

import com.tanfra.shopmob.smob.data.repo.ato.Ato

fun maxItem(items: List<Ato>): Long {
    return items.maxOf { it -> it.itemPosition }
}