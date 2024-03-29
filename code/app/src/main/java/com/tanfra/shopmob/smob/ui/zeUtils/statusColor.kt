package com.tanfra.shopmob.smob.ui.zeUtils

import androidx.compose.ui.graphics.Color
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.features.common.theme.swipePrimaryColor
import com.tanfra.shopmob.features.common.theme.swipeSecondaryColor
import com.tanfra.shopmob.features.common.theme.swipeSecondaryLightColor

fun statusColor(status: ItemStatus = ItemStatus.INVALID) = when(status) {
    ItemStatus.NEW -> swipeSecondaryLightColor
    ItemStatus.OPEN -> swipeSecondaryLightColor
    ItemStatus.IN_PROGRESS -> swipePrimaryColor
    ItemStatus.DONE -> swipeSecondaryColor
    ItemStatus.INVALID -> Color(R.color.blue)
    else -> Color(R.color.white)
}