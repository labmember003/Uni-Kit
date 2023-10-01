package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.api.UserData
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.repository.UnikitRepository
import com.falcon.unikit.repository.UserNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle): ViewModel() {
    val jwtToken : StateFlow<UserData>
        get() = unikitRepository.jwtToken

    val myNotes: StateFlow<List<UserNote>>
        get() = unikitRepository.myNotes

    suspend fun getToken(idToken: String) {
        unikitRepository.getJwtToken(idToken)
    }

    fun getMyNotes(jwtToken: String) {
        viewModelScope.launch {
            unikitRepository.getMyNotes(jwtToken)
        }
    }
}