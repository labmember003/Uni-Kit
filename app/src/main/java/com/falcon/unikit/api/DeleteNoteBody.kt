package com.falcon.unikit.api

import com.google.gson.annotations.SerializedName

data class DeleteNoteBody (
    @SerializedName("token")
    val jwtToken: String,
    @SerializedName("contentID")
    val contentID: String
)
