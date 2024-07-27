package com.falcon.unikit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.cat
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository
) : ViewModel() {
    val subjects: StateFlow<List<SubjectItem>> = unikitRepository.subject
    init {
        viewModelScope.launch {
            while (subjects.value.isEmpty() && cat != "") {
                unikitRepository.getSubject(cat)
            }
        }
    }
//    private fun getSubjects(branchId: String) {
//        viewModelScope.launch {
//            Log.i("SubjectViewModel", "getSubjects called with branchId: $branchId")
//            try {
//                unikitRepository.getSubject(branchId)
//                Log.i("SubjectViewModel", "Subjects fetched successfully")
//            } catch (e: Exception) {
//                Log.e("SubjectViewModel", "Error fetching subjects: ${e.message}")
//            }
//        }
//    }
}