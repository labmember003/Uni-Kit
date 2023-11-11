package com.falcon.unikit.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.api.Content
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val contents : StateFlow<List<Content>>
        get() = unikitRepository.content

    init {
        viewModelScope.launch {
            val subjectID = savedStateHandle.get<String>("subjectID") ?: "123"
            Log.i("diwaliiii", subjectID.toString())
            unikitRepository.getContent(subjectID)
        }
    }
}