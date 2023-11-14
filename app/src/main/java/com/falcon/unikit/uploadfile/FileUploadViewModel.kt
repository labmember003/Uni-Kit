package com.falcon.unikit.uploadfile

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

sealed class UploadResult {
    data class Success(val message: String) : UploadResult()
    data class Failure(val error: String) : UploadResult()
}
@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val repository: FileUploadRepository
) : ViewModel() {



    private val _uploadResult = MutableStateFlow<UploadResult?>(null)
    val uploadResult: StateFlow<UploadResult?> = _uploadResult

    fun uploadFile(contentResolver: ContentResolver, uri: Uri, uploadFileBody: UploadFileBody) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.uploadFile(contentResolver, uri, uploadFileBody)
                _uploadResult.value = UploadResult.Success(response.message)
//                Toast.makeText(application, "Success", Toast.LENGTH_SHORT).show()
                Log.i("fileupload", "Success")
            } catch (e: Exception) {
                _uploadResult.value = UploadResult.Failure("File upload failed: ${e.message}")
//                Toast.makeText(application, "Failure", Toast.LENGTH_SHORT).show()
                Log.i("fileupload", "Failure")
                Log.i("fileupload", e.message.toString())
            }
        }
    }
}
