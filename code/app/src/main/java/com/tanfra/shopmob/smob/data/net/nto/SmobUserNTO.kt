package com.tanfra.shopmob.smob.data.net.nto


data class SmobUserNTO(
    var username: String,
    var imageUrl: String?,
    var shops: List<String?>,
    var groups: List<String?>,
    var lists: List<String?>,
    val userId: String,
)

