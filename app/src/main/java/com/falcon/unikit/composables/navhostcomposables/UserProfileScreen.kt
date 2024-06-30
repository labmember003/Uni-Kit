package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.falcon.unikit.MainActivity
import com.falcon.unikit.Utils
import com.falcon.unikit.api.UserData
import com.falcon.unikit.profile.ProfileScreen
import com.google.gson.Gson

@Composable
fun MainActivity.UserProfileScreen(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    val gson = Gson()
    val userData = gson.fromJson(sharedPreferences.getString(Utils.USER_DATA, null), UserData::class.java)
    ProfileScreen(
        userData = userData,
        onSignOut = {
            isSigninSuccess = false
            navController.navigate("walk_through_screen")
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        },
        navController
    )
}