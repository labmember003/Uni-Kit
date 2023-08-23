package com.falcon.unikit.repository

import com.falcon.unikit.api.Content
import com.falcon.unikit.api.ContentBody
import com.falcon.unikit.models.body.CourseBody
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.models.body.BranchBody
import com.falcon.unikit.models.body.SubjectBody
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.models.body.YearListBody
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
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
    suspend fun getCollege() {
        val response = unikitAPI.getCollegeList()
        if (response.isSuccessful && response.body() != null) {
            _colleges.emit(response.body()!!)
        }
    }

    suspend fun getCourse(collegeID: String) {
        val response = unikitAPI.getCourseList(CourseBody(collegeID))
        if (response.isSuccessful && response.body() != null) {
            _course.emit(response.body()!!)
        }
    }

    suspend fun getYear(courseID: String) {
        val response = unikitAPI.getYearList(YearListBody(courseID))
        if (response.isSuccessful && response.body() != null) {
            _year.emit(response.body()!!)
        }
    }

    suspend fun getBranch(yearID: String) {
        val response = unikitAPI.getBranchList(BranchBody(yearID))
        if (response.isSuccessful && response.body() != null) {
            _branch.emit(response.body()!!)
        }
    }

    suspend fun getSubject(branchID: String) {
        val response = unikitAPI.getSubjectList(SubjectBody(branchID))
        if (response.isSuccessful && response.body() != null) {
            _subject.emit(response.body()!!)
        }
    }

    suspend fun getContent(subjectID: String) {
        val response = unikitAPI.getContentOfSubject(ContentBody(subjectID))
        if (response.isSuccessful && response.body() != null) {
            _content.emit(response.body()!!)
        }
    }
}