package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.falcon.unikit.MainActivity
import com.falcon.unikit.settings.SettingsScreen

@Composable
fun MainActivity.UserSettingsScreen(
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    SettingsScreen({
        navController.popBackStack()
    }, {
        navController.navigate("select_college_screen")
    }, {
        isSigninSuccess = false
        navController.navigate("walk_through_screen")
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    })
}