package com.falcon.unikit

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.falcon.unikit.Utils.PDF_PASSWORD
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

class PDFviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)
        val pdfLayout = findViewById<PDFView>(R.id.pdfViewLayout)
        val intent = intent
        val receivedData = intent.getStringExtra("uri")
        val fileUri = Uri.parse(receivedData)
        try {
            pdfLayout.fromUri(fileUri)
                .password(PDF_PASSWORD)
                .enableDoubletap(true)
                .load()
        } catch (e: Exception) {
            Log.i("erty",e.message.toString())
        }
    }
}