package com.falcon.unikit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.CollegeItem
import com.falcon.unikit.models.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(private val unikitRepository: UnikitRepository) : ViewModel() {
    val courses : StateFlow<List<CourseItem>>
        get() = unikitRepository.course

    init {
        viewModelScope.launch {
//            TODO(ABHI STATIC RKHA HAI DATA, ISSE DYNAMICALLY PASS KRNA HAI)
            unikitRepository.getCourse(CollegeItem("abc", "USAR"))
        }
    }
}