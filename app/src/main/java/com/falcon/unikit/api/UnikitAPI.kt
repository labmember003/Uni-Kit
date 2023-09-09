package com.falcon.unikit.api

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
    @GET("/college")
    suspend fun getCollegeList(): Response<List<CollegeItem>>

//    @GET("/getCourseList")
    @GET("college/data")
    suspend fun getCourseList(@Query("collegeId") courseQueryParam: String): Response<List<CourseItem>>

//    @GET("/getYearList")
    @GET("college/data")
    suspend fun getYearList(@Query("courseId") yearListQueryParam: String): Response<List<YearItem>>

//    @GET("/getBranchList")
    @GET("college/data")
    suspend fun getBranchList(@Query("year") branchQueryParam: String): Response<List<BranchItem>>

//    @GET("/getSubjectList")
    @GET("college/data")
    suspend fun getSubjectList(@Query("branchId") subjectQueryParam: String): Response<List<SubjectItem>>

//    @GET("/getContent")
    @GET("college/data")
    suspend fun getContentOfSubject(@Query("subject_id") contentQueryParam: String): Response<List<Content>>

    @POST("/users")
    suspend fun getJwtToken(@Body token: String): Response<String>

    @POST("/getMyNotes")
    suspend fun getMyNotes(@Body token: String): Response<String>

    @POST("/getContent")
    suspend fun likeButtonPressed(@Body itemID: String)
}

///college/
