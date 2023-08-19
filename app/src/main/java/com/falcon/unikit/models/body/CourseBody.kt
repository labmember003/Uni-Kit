package com.falcon.unikit.models.body

import com.google.gson.annotations.SerializedName

data class CourseBody(
    @SerializedName("college_id") val collegeId: String
)