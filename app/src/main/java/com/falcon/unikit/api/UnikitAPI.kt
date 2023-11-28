package com.falcon.unikit.api

import com.falcon.unikit.MyNoteItem
import com.falcon.unikit.uploadfile.UploadResponse
import com.falcon.unikit.models.body.GetMyNotesBody
import com.falcon.unikit.models.body.JWTbody
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.uploadfile.UploadFileBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @POST("content/data")
    suspend fun getContent(@Query("subjectid") contentQueryParam: String): Response<List<Content>>

    @POST("college/data")
    suspend fun getItem(@Query("content_id") itemQueryParam: String): Response<List<Item>>

    @POST("users/googleOneTap")
    suspend fun getJwtToken(@Body googleToken: JWTbody): Response<UserData>

    @POST("users/mycontent")
    suspend fun getMyNotes(@Body token: GetMyNotesBody): Response<List<MyNoteItem>>

    @POST("/content/likeCount")
    suspend fun likeButtonPressed(@Query("contentid") contentid: String, @Query("userid") userId: String): Response<String>

    @POST("/content/dislikeCount")
    suspend fun dislikeButtonPressed(@Query("contentid") contentid: String, @Query("userid") userId: String): Response<String>

    @Multipart
    @POST("/content/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Query("token") token: String,
        @Query("subjectid") subjectid: String,
        @Query("type") type: String,
        @Query("name") name: String
    ): UploadResponse

    @POST("/content/download")
    suspend fun getDownloadableURL(@Body contentid: GetDownloadableUrlBody): Response<DownloadableURL>

    @POST("content/report")
    suspend fun reportContent(@Query("userid") token: String, @Query("contentid") contentid: String, @Query("parameter") parameter: String) : Response<List<String>>
}