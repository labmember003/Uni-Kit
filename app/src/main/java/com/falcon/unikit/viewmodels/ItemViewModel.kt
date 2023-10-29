package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.Item
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val items : StateFlow<List<Item>>
        get() = unikitRepository.items

    suspend fun getItem(contentID: String) {
//        val contentId = savedStateHandle.get<String>(contentID) ?: "123"
        unikitRepository.getItem(contentID)
    }

    init {
//        viewModelScope.launch {
//            val contentId = savedStateHandle.get<String>("subjectID") ?: "123"
//            unikitRepository.getItem(contentId)
//        }
    }

    suspend fun likeButtonPressed(itemID: String) {
        unikitRepository.likeButtonPressed(itemID)
    }

}