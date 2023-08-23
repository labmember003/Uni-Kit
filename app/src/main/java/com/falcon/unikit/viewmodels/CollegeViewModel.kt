package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollegeViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository
) : ViewModel() {
    val colleges : StateFlow<List<CollegeItem>>
        get() = unikitRepository.college

    init {
        viewModelScope.launch {
            unikitRepository.getCollege()
        }
    }
}