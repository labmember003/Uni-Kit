package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.ErrorPage
import com.falcon.unikit.composables.general.LoadingScreen
import com.falcon.unikit.MainActivity
import com.falcon.unikit.SelectCourseScreen
import com.falcon.unikit.Utils
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.viewmodels.CourseViewModel
import kotlinx.coroutines.delay

@Composable
fun MainActivity.SelectYourCourseScreen(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        val courseID = sharedPreferences.getString(Utils.COURSE_ID, "NO_COURSE_SELECTED")
        if (courseID != "NO_COURSE_SELECTED") {
            navController.navigate("main_screen/${courseID}")
        }
    }
    val errorState = remember { mutableStateOf(false) }
    val courseViewModel: CourseViewModel = hiltViewModel()
    val courses: State<List<CourseItem>> = courseViewModel.courses.collectAsState()
    Log.i("catcatcatcat", courses.value.size.toString())
    LaunchedEffect(key1 = errorState.value) {
        val timeoutDurationMillis = 15000L // 15 seconds (adjust as needed)
        delay(timeoutDurationMillis) // Wait for the timeout duration
        // Check if the data is still not available (i.e., an error occurred)
        if (courses.value.isEmpty()) {
            errorState.value = true
        }
    }
    if (errorState.value) {
        // Show error message and retry button
        ErrorPage {
            errorState.value = false
        }
    } else {
        if (courses.value != emptyList<CourseItem>()) {
            SelectCourseScreen(
                itemList = courses.value,
                title = "Select Your Course",
                sharedPrefTitle = "COURSE",
                sharedPreferences = sharedPreferences
            ) { courseID ->
                if (courseID == null) {
                    Toast.makeText(this, "Please Select Course", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("main_screen/${courseID}")
                }
            }
        } else {
            LoadingScreen()
        }
    }
}