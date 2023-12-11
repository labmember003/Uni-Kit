package com.falcon.unikit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.unikit.MyNoteItem
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.DownloadableURL
import com.falcon.unikit.api.OTP
import com.falcon.unikit.api.UserData
import com.falcon.unikit.repository.UnikitRepository
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

    val myNotes: StateFlow<List<MyNoteItem>>
        get() = unikitRepository.myNotes

    val downloadableURL: StateFlow<DownloadableURL>
        get() = unikitRepository.downloadableLink

    val contentFromID: StateFlow<List<Content>>
        get() = unikitRepository.contentFromID
    val OTP: StateFlow<OTP>
        get() = unikitRepository.OTP

    suspend fun getToken(idToken: String) {
        unikitRepository.getJwtToken(idToken)
    }

    fun getMyNotes(jwtToken: String) {
        viewModelScope.launch {
            unikitRepository.getMyNotes(jwtToken)
        }
    }

    suspend fun getDownloadableURL(contentID: String) {
        unikitRepository.getDownloadableURL(contentID)
    }

    suspend fun getContentFromContentID(contentid: String) {
        unikitRepository.getContentFromContentID(contentid)
    }

    suspend fun getOTP(phoneNumber: String) {
        unikitRepository.getOTP(phoneNumber)
    }
}