package com.falcon.unikit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YearViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val years : StateFlow<List<YearItem>>
        get() = unikitRepository.year

    init {
        viewModelScope.launch {
            val courseID = savedStateHandle.get<String>("courseID") ?: "USAR001"
            Log.i("catcatcat", courseID)
            unikitRepository.getYear(courseID)
        }
    }
}