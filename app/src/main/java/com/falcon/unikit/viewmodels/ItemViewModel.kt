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

    suspend fun likeButtonPressed(itemID: String, userId: String) {
        unikitRepository.likeButtonPressed(itemID, userId)
    }
    suspend fun dislikeButtonPressed(itemID: String, userId: String) {
        unikitRepository.dislikeButtonPressed(itemID, userId)
    }

    suspend fun reportContent(token: String, contentID: String, parameter: String) {
        unikitRepository.reportContent(token, contentID, parameter)
    }
}