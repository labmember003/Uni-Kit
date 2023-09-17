package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.falcon.unikit.api.UserData
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.repository.UnikitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val unikitRepository: UnikitRepository,
    private val savedStateHandle: SavedStateHandle): ViewModel() {
    val jwtToken : StateFlow<UserData>
        get() = unikitRepository.jwtToken

    suspend fun getToken(idToken: String) {
        unikitRepository.getJwtToken(idToken)
    }
}