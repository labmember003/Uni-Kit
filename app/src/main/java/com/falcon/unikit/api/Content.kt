package com.falcon.unikit.api

data class Content (
    val _id: String? = null,
    val contentName: String? = null,
    val contentType: String? = null,      // Example notes, papers etc
    val author: String? = null,
    val contentId: String? = null,
    val like: List<String>? = null,
    val dislike: List<String>? = null,
    val report: List<String>? = null,
    val subjectID: String? = null,

    val pdfFile: String? = null
)