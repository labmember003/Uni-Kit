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
import retrofit2.http.Query

interface UnikitAPI {
    @GET("/getCollegeList")
    suspend fun getCollegeList(): Response<List<CollegeItem>>

    @GET("/getCourseList")
    suspend fun getCourseList(@Query("college_id") courseQueryParam: String): Response<List<CourseItem>>

    @GET("/getYearList")
    suspend fun getYearList(@Query("course_id") yearListQueryParam: String): Response<List<YearItem>>

    @GET("/getBranchList")
    suspend fun getBranchList(@Query("year_id") branchQueryParam: String): Response<List<BranchItem>>

    @GET("/getSubjectList")
    suspend fun getSubjectList(@Query("branch_id") subjectQueryParam: String): Response<List<SubjectItem>>

    @GET("/getContent")
    suspend fun getContentOfSubject(@Query("subject_id") contentQueryParam: String): Response<List<Content>>
}