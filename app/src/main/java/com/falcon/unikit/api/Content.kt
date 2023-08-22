package com.falcon.unikit.api

data class Content (
    val contentId: String,
    val contentName: String, // Example notes, papers etc
    val items: List<Item>
)