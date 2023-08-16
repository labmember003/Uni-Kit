package com.falcon.unikit.api

import com.falcon.unikit.models.CollegeItem
import com.falcon.unikit.models.CourseItem
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
    suspend fun getNumberOfYear(@Body body: YearListBody): Response<List<Int>>

    @POST("/getBranchList")
    suspend fun getBranchList(@Body body: BranchBody): Response<List<BranchItem>>

    @POST("/getSubjectList")
    suspend fun getSubjectList(@Body body: SubjectBody): Response<List<SubjectItem>>
}