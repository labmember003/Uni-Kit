package com.falcon.unikit.models.body

import com.google.gson.annotations.SerializedName

data class JWTbody (
    @SerializedName("googleToken")
    val googleToken: String
)
