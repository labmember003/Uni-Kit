package com.falcon.unikit.uploadfile

import android.content.ContentResolver
import android.net.Uri
import com.falcon.unikit.api.UnikitAPI
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class FileUploadRepository @Inject constructor (private val api: UnikitAPI) {
    suspend fun uploadFile(contentResolver: ContentResolver, uri: Uri, uploadFileBody: UploadFileBody): UploadResponse {
        val inputStream = contentResolver.openInputStream(uri)
        val file = inputStream?.let {
            val tempFile = File.createTempFile("upload", null)
            tempFile.deleteOnExit()
            tempFile.outputStream().use { output ->
                it.copyTo(output)
            }
            tempFile
        } ?: throw IllegalArgumentException("Invalid URI")

        val requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return api.uploadFile(body, uploadFileBody.jwtToken, uploadFileBody.subjectid, uploadFileBody.contentType)
    }
}
