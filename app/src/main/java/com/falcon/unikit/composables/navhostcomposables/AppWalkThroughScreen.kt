package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.falcon.unikit.MainActivity
import com.falcon.unikit.Utils

@Composable
fun MainActivity.AppWalkThroughScreen(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    BackHandler(
        onBack = {
            finish()
        }
    )
    LaunchedEffect(key1 = Unit) {
        val userDataJson = sharedPreferences.getString(Utils.USER_DATA, "NO_USER_FOUND")
        if (userDataJson != "NO_USER_FOUND") {
            navController.navigate("select_college_screen")
        }
    }
    com.falcon.unikit.ui.walkthrough.WalkThroughScreen {
        navController.navigate("sign_in")
    }
}