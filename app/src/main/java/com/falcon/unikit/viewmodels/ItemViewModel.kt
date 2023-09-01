package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
) : ViewModel() {

    suspend fun likeButtonPressed(itemID: String) {
        unikitRepository.likeButtonPressed(itemID)
    }

}