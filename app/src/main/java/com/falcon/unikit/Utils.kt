package com.falcon.unikit

import android.content.Context
import android.content.Intent
import android.net.Uri

object Utils {
    const val INITIAL_LAUCH = "INITIAL_LAUCH"
    const val COLLEGE_NAME = "COLLEGE_NAME"
    const val COLLEGE_ID = "COLLEGE_ID"
    const val COURSE_ID = "COURSE_ID"
    const val COURSE_NAME = "COURSE_NAME"
    const val JWT_TOKEN = "JWT_TOKEN"
    const val USER_DATA = "USER_DATA"
    const val PDF_PASSWORD = "ABC"
    const val GENDER = "GENDER"
}

fun openUrlInBrowser(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}