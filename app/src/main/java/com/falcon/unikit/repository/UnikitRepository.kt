package com.falcon.unikit.repository

import android.util.Log
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.JWTbody
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.api.UserData
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.models.item.YearItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UnikitRepository @Inject constructor(private val unikitAPI: UnikitAPI) {

    private val _colleges = MutableStateFlow<List<CollegeItem>>(emptyList())
    val college: StateFlow<List<CollegeItem>>
        get() = _colleges

    private val _course = MutableStateFlow<List<CourseItem>>(emptyList())
    val course: StateFlow<List<CourseItem>>
        get() = _course

    private val _year = MutableStateFlow<List<YearItem>>(emptyList())
    val year: StateFlow<List<YearItem>>
        get() = _year

    private val _branch = MutableStateFlow<List<BranchItem>>(emptyList())
    val branch: StateFlow<List<BranchItem>>
        get() = _branch

    private val _subject = MutableStateFlow<List<SubjectItem>>(emptyList())
    val subject: StateFlow<List<SubjectItem>>
        get() = _subject

    private val _content = MutableStateFlow<List<Content>>(emptyList())
    val content: StateFlow<List<Content>>
        get() = _content

    private val _jwtToken = MutableStateFlow<UserData>(UserData("", "", ""))
    val jwtToken: StateFlow<UserData>
        get() = _jwtToken
    suspend fun getCollege() {
        val response = unikitAPI.getCollegeList()
        if (response.isSuccessful && response.body() != null) {
            _colleges.emit(response.body()!!)
        }
    }

    suspend fun getCourse(collegeID: String) {
        val response = unikitAPI.getCourseList(collegeID)
        if (response.isSuccessful && response.body() != null) {
            _course.emit(response.body()!!)
        }
    }

    suspend fun getYear(courseID: String) {
        val response = unikitAPI.getYearList(courseID)
        if (response.isSuccessful && response.body() != null) {
            _year.emit(response.body()!!)
        }
    }

    suspend fun getBranch(yearID: String) {
        val response = unikitAPI.getBranchList(yearID)
        if (response.isSuccessful && response.body() != null) {
            _branch.emit(response.body()!!)
        }
    }

    suspend fun getSubject(branchID: String) {
        val response = unikitAPI.getSubjectList(branchID)
        if (response.isSuccessful && response.body() != null) {
            _subject.emit(response.body()!!)
        }
    }

    suspend fun getContent(subjectID: String) {
        val response = unikitAPI.getContentOfSubject(subjectID)
        if (response.isSuccessful && response.body() != null) {
            _content.emit(response.body()!!)
        }
    }

    suspend fun getJwtToken(idToken: String) {
        val response = unikitAPI.getJwtToken(JWTbody(idToken))
        Log.i("catCatCatcatcat", response.body().toString())
        Log.i("catCatCatcatcat2", idToken.toString())
        if (response.isSuccessful && response.body() != null) {
            _jwtToken.emit(response.body()!!)
        } else {
            // Handle unsuccessful response (e.g., log error message)
            Log.e("NetworkError", "Unsuccessful response: ${response.code()}")
        }
    }

    suspend fun likeButtonPressed(itemID: String) {
        unikitAPI.likeButtonPressed(itemID)
    }
}