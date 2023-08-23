package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.repository.UnikitRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubjectViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val subjects : StateFlow<List<SubjectItem>>
        get() = unikitRepository.subject

    init {
        viewModelScope.launch {
//            TODO(ABHI STATIC RKHA HAI DATA, ISSE DYNAMICALLY PASS KRNA HAI)
            unikitRepository.getSubject(BranchItem("abc", "USAR"))
        }
    }
}