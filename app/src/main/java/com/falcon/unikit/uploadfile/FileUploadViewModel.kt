package com.falcon.unikit.uploadfile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class FileUploadViewModel(private val repository: FileUploadRepository) : ViewModel() {

    sealed class UploadResult {
        data class Success(val message: String) : UploadResult()
        data class Failure(val error: String) : UploadResult()
    }

    private val _uploadResult = MutableStateFlow<UploadResult?>(null)
    val uploadResult: StateFlow<UploadResult?> = _uploadResult

    fun uploadFile(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.uploadFile(contentResolver, uri)
                _uploadResult.value = UploadResult.Success(response.message)
            } catch (e: Exception) {
                _uploadResult.value = UploadResult.Failure("File upload failed: ${e.message}")
            }
        }
    }
}
