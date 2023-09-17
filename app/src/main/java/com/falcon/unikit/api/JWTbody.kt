package com.falcon.unikit.api

import com.google.gson.annotations.SerializedName

data class JWTbody (
    @SerializedName("googleToken")
    val googleToken: String
)
