package com.falcon.unikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val subjects : StateFlow<List<SubjectItem>>
        get() = unikitRepository.subject


    private val _branchID = MutableLiveData<String>()
    private val branchID: LiveData<String> = _branchID

    fun setBranchID(newValue: String) {
        _branchID.value = newValue
    }
    fun getSubjects(branchId: String) {
        viewModelScope.launch {
            unikitRepository.getSubject(branchId)
        }
    }
//    init {
//        viewModelScope.launch {
//            unikitRepository.getSubject(branchID.value.toString())
//        }
//    }
}