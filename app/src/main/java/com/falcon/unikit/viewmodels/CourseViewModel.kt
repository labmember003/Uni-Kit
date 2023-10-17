package com.falcon.unikit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val courses : StateFlow<List<CourseItem>>
        get() = unikitRepository.course

    init {
        viewModelScope.launch {
            val collegeID = savedStateHandle.get<String>("collegeID") ?: "123"
            Log.i("meriyafiruskibilli", collegeID)
            unikitRepository.getCourse(collegeID)
        }
    }
}