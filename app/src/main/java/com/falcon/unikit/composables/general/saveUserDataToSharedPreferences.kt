package com.falcon.unikit.composables.general

import android.content.Context
import android.content.SharedPreferences
import com.falcon.unikit.api.UserData
import com.google.gson.Gson

fun saveUserDataToSharedPreferences(context: Context, userData: UserData, sharedPreferences: SharedPreferences) {
    val editor = sharedPreferences.edit()
    val gson = Gson()
    editor.putString("user_data_key", gson.toJson(userData))
    editor.apply()
}