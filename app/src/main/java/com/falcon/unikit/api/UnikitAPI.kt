package com.falcon.unikit.api

import com.falcon.unikit.models.body.BranchBody
import com.falcon.unikit.models.body.CourseBody
import com.falcon.unikit.models.body.SubjectBody
import com.falcon.unikit.models.body.YearListBody
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.models.item.YearItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UnikitAPI {
    @GET("/getCollegeList")
    suspend fun getCollegeList(): Response<List<CollegeItem>>

    @POST("/getCourseList")
    suspend fun getCourseList(@Body body: CourseBody): Response<List<CourseItem>>

    @POST("/getYearList")
    suspend fun getYearList(@Body body: YearListBody): Response<List<YearItem>>

    @POST("/getBranchList")
    suspend fun getBranchList(@Body body: BranchBody): Response<List<BranchItem>>

    @POST("/getSubjectList")
    suspend fun getSubjectList(@Body body: SubjectBody): Response<List<SubjectItem>>
}