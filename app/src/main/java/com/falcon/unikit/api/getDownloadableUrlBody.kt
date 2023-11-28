package com.falcon.unikit.api

import com.google.gson.annotations.SerializedName

data class GetDownloadableUrlBody (
    @SerializedName("contentid")
    val contentId: String
)
