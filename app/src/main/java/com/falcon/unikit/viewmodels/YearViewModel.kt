package com.falcon.unikit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class YearViewModel@Inject constructor(private val unikitRepository: UnikitRepository): ViewModel() {
    val years : StateFlow<List<YearItem>>
        get() = unikitRepository.year

    init {
        viewModelScope.launch {
//            TODO(ABHI STATIC RKHA HAI DATA, ISSE DYNAMICALLY PASS KRNA HAI)
            unikitRepository.getYear(CourseItem("abc", "USAR"))
        }
    }
}