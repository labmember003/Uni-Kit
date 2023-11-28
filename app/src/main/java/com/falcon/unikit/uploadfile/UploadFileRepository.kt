package com.falcon.unikit.uploadfile

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.falcon.unikit.api.UnikitAPI
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class FileUploadRepository @Inject constructor (private val api: UnikitAPI) {
suspend fun uploadFile(contentResolver: ContentResolver, uri: Uri, uploadFileBody: UploadFileBody): UploadResponse {
    val inputStream = contentResolver.openInputStream(uri)
    val fileExtension = getFileExtension(contentResolver, uri)
    val mimeType = getMimeType(fileExtension)

    val file = inputStream?.let {
        val tempFile = File.createTempFile("upload", ".$fileExtension")
        tempFile.deleteOnExit()
        tempFile.outputStream().use { output ->
            it.copyTo(output)
        }
        tempFile
    } ?: throw IllegalArgumentException("Invalid URI")

    val requestFile = RequestBody.create(mimeType?.toMediaTypeOrNull(), file)
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
    Log.i("sexxxxxxxx", uploadFileBody.name)
    return api.uploadFile(body, uploadFileBody.jwtToken, uploadFileBody.subjectid, uploadFileBody.contentType, uploadFileBody.name)
}

    private fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "pdf"
    }

    private fun getMimeType(fileExtension: String): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
    }


}
