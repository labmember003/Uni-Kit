package com.falcon.unikit.repository

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
    suspend fun getCollege() {
        val response = unikitAPI.getCollegeList()
        if (response.isSuccessful && response.body() != null) {
            _colleges.emit(response.body()!!)
        }
    }

    suspend fun getCourse(collegeItem: CollegeItem) {
        val response = unikitAPI.getCourseList(CourseBody(collegeItem.collegeID))
        if (response.isSuccessful && response.body() != null) {
            _course.emit(response.body()!!)
        }
    }

    suspend fun getYear(courseItem: CourseItem) {
        val response = unikitAPI.getYearList(YearListBody(courseItem.courseID))
        if (response.isSuccessful && response.body() != null) {
            _year.emit(response.body()!!)
        }
    }

    suspend fun getBranch(yearItem: YearItem) {
        val response = unikitAPI.getBranchList(BranchBody(yearItem.yearID))
        if (response.isSuccessful && response.body() != null) {
            _branch.emit(response.body()!!)
        }
    }

    suspend fun getSubject(branchItem: BranchItem) {
        val response = unikitAPI.getSubjectList(SubjectBody(branchItem.branchID))
        if (response.isSuccessful && response.body() != null) {
            _subject.emit(response.body()!!)
        }
    }
}