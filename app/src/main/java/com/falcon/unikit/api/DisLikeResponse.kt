package com.falcon.unikit.api

data class DisLikeResponse (
    val contentid: String? = null,
    val count: String? = null,
    val dislikeList: List<String>? = null,
    val message: String? = null,
)