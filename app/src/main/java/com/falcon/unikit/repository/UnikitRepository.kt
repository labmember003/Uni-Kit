package com.falcon.unikit.repository

import android.util.Log
import com.falcon.unikit.MyNoteItem
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.DownloadableURL
import com.falcon.unikit.api.GetDownloadableUrlBody
import com.falcon.unikit.api.Item
import com.falcon.unikit.models.body.JWTbody
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.api.UserData
import com.falcon.unikit.models.body.GetMyNotesBody
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

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>>
        get() = _items

    private val _jwtToken = MutableStateFlow(UserData())
    val jwtToken: StateFlow<UserData>
        get() = _jwtToken

    private val _myNotes = MutableStateFlow<List<MyNoteItem>>(emptyList())
    val myNotes: StateFlow<List<MyNoteItem>>
        get() = _myNotes

    private val _downloadableLink = MutableStateFlow(DownloadableURL())
    val downloadableLink:  StateFlow<DownloadableURL>
        get() = _downloadableLink


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
        val response = unikitAPI.getContent(subjectID)
        if (response.isSuccessful && response.body() != null) {
            _content.emit(response.body()!!)
        }
    }

    suspend fun getItem(contentId: String) {
        val response = unikitAPI.getItem(contentId)
        if (response.isSuccessful && response.body() != null) {
            _items.emit(response.body()!!)
        }
    }

    suspend fun getJwtToken(idToken: String) {
        val response = unikitAPI.getJwtToken(JWTbody(idToken))
        if (response.isSuccessful && response.body() != null) {
            _jwtToken.emit(response.body()!!)
        } else {
            // Handle unsuccessful response (e.g., log error message)
            Log.e("NetworkError", "Unsuccessful response: ${response.code()}")
        }
    }

    suspend fun getMyNotes(jwtToken: String) {
        val response = unikitAPI.getMyNotes(GetMyNotesBody(jwtToken))
        if (response.isSuccessful && response.body() != null) {
            _myNotes.emit(response.body()!!)
        } else {
            // Handle unsuccessful response (e.g., log error message)
            Log.e("NetworkError", "Unsuccessful response: ${response.code()}")
        }
    }

    suspend fun likeButtonPressed(itemID: String, userId: String) {
        unikitAPI.likeButtonPressed(itemID, userId)
    }

    suspend fun dislikeButtonPressed(itemID: String, userId: String) {
        unikitAPI.dislikeButtonPressed(itemID, userId)
    }

    suspend fun reportContent(token: String, contentId: String, parameter: String) {
        unikitAPI.reportContent(token, contentId, parameter)
    }

    suspend fun getDownloadableURL(contentId: String) {
        val response = unikitAPI.getDownloadableURL(GetDownloadableUrlBody(contentId))
        if (response.isSuccessful && response.body() != null) {
            _downloadableLink.emit(response.body()!!)
        } else {
            // Handle unsuccessful response (e.g., log error message)
            Log.e("NetworkError", "Unsuccessful response: ${response.code()}")
        }
    }
}