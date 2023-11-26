package com.falcon.unikit.uploadfile

import com.google.gson.annotations.SerializedName

data class UploadFileBody (
    @SerializedName("token")
    val jwtToken: String,
    @SerializedName("subjectid")
    val subjectid: String,
    @SerializedName("type")
    val contentType: String,
    @SerializedName("name")
    val name: String
)