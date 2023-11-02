package com.falcon.unikit.api

data class Item (
    val itemName: String,
    val itemId: String? = null,
    val downloadURL: String,
    val likeCount: Int = 0,
    val dislikeCount: Int = 0
)
