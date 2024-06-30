package com.falcon.unikit.composables.navhostcomposables

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullWebView(url: String) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    // Configure the WebView
    webView.settings.javaScriptEnabled = true
    webView.webViewClient = WebViewClient()
    webView.webChromeClient = WebChromeClient()

    // Load the URL
    webView.loadUrl(url)

    // Create AndroidView to display WebView
    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    ) { webView ->
        // WebView is configured and loaded with the provided URL
    }
}