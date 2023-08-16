package com.falcon.unikit.repository

import com.falcon.unikit.api.CourseBody
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.models.CollegeItem
import com.falcon.unikit.models.CourseItem
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
}