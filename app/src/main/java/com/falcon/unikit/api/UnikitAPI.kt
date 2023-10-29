package com.falcon.unikit.api

import com.falcon.unikit.MyNoteItem
import com.falcon.unikit.models.body.GetMyNotesBody
import com.falcon.unikit.models.body.JWTbody
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

    @POST("college/data")
    suspend fun getCourseList(@Query("collegeId") courseQueryParam: String): Response<List<CourseItem>>

    @POST("college/data")
    suspend fun getYearList(@Query("courseId") yearListQueryParam: String): Response<List<YearItem>>

    @POST("college/data")
    suspend fun getBranchList(@Query("year") branchQueryParam: String): Response<List<BranchItem>>

    @POST("college/data")
    suspend fun getSubjectList(@Query("branchId") subjectQueryParam: String): Response<List<SubjectItem>>

    @POST("college/data")
    suspend fun getContent(@Query("subject_id") contentQueryParam: String): Response<List<Content>>

    @POST("college/data")
    suspend fun getItem(@Query("content_id") itemQueryParam: String): Response<List<Item>>

    @POST("users/googleOneTap")
    suspend fun getJwtToken(@Body googleToken: JWTbody): Response<UserData>

    @POST("users/myNotes")
    suspend fun getMyNotes(@Body token: GetMyNotesBody): Response<List<MyNoteItem>>

    @POST("/getContent")
    suspend fun likeButtonPressed(@Body itemID: String)
}

///college/
