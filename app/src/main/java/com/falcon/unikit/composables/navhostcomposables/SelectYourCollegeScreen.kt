package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import com.falcon.unikit.SelectCollegeScreen
import com.falcon.unikit.Utils
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.viewmodels.CollegeViewModel
import kotlinx.coroutines.delay

@Composable
fun MainActivity.SelectYourCollegeScreen(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    //                        TODO("CHANGE INITIAL_LAUCH TO IS COLLEGE SELECTED OR IS COURSE SELECTED")
    LaunchedEffect(Unit) {
        val collegeID = sharedPreferences.getString(Utils.COLLEGE_ID, "NO_COLLEGE_SELECTED")
        Log.i("collegeID", collegeID.toString())
        if (collegeID != "NO_COLLEGE_SELECTED") {
            navController.navigate("select_course_screen/${collegeID}")
        }
    }
    BackHandler(
        onBack = {
            navController.navigate("walk_through_screen")
        }
    )

//                        TODO - PERFORM AT CHANGE COLLEGE OPTION IN SETTINGS
//                        val editor = sharedPreferences.edit()
//                        editor.clear()
//                        editor.apply()
    val errorState = remember { mutableStateOf(false) }
    val collegeViewModel: CollegeViewModel = hiltViewModel()
    val colleges: State<List<CollegeItem>> = collegeViewModel.colleges.collectAsState()

    LaunchedEffect(key1 = errorState.value) {
        val timeoutDurationMillis = 15000L // 15 seconds (adjust as needed)
        delay(timeoutDurationMillis) // Wait for the timeout duration
        // Check if the data is still not available (i.e., an error occurred)
        if (colleges.value.isEmpty()) {
            errorState.value = true
        }
    }

    if (errorState.value) {
        // Show error message and retry button
        ErrorPage {
            errorState.value = false
        }
    } else {
        if (colleges.value != emptyList<CollegeItem>()) {
            SelectCollegeScreen(
                itemList = colleges.value,
                title = "Select Your College",
                sharedPrefTitle = "COLLEGE",
                sharedPreferences = sharedPreferences
            ) { collegeID ->
                if (collegeID == null) {
                    Toast.makeText(this, "Please Select College", Toast.LENGTH_SHORT).show()
                } else {
                    navController.navigate("select_course_screen/${collegeID}")
                }

            }
        } else {
            LoadingScreen()
        }
    }
//                        TODO
//                          use ErrorPage() composable also
}