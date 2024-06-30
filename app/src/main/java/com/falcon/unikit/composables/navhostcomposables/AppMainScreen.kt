package com.falcon.unikit.composables.navhostcomposables

import android.util.Log
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
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.viewmodels.YearViewModel
import kotlinx.coroutines.delay

@Composable
fun MainActivity.AppMainScreen(navController: NavHostController) {
    BackHandler(
        onBack = {
            finish()
        }
    )
    val errorState = remember { mutableStateOf(false) }
    val yearViewModel: YearViewModel = hiltViewModel()
    val years: State<List<YearItem>> = yearViewModel.years.collectAsState()
    Log.i("catcatcatdog", years.value.size.toString())
    LaunchedEffect(key1 = errorState.value) {
        val timeoutDurationMillis = 15000L // 15 seconds (adjust as needed)
        delay(timeoutDurationMillis) // Wait for the timeout duration
        // Check if the data is still not available (i.e., an error occurred)
        if (years.value.isEmpty()) {
            errorState.value = true
        }
    }
    if (errorState.value) {
        ErrorPage {
            errorState.value = false
        }
    } else {
        if (years.value != emptyList<CollegeItem>()) {
            com.falcon.unikit.screens.MainScreen(
                yearList = years,
                navController = navController
            ) { yearID ->
                Log.i("yearyearyear", yearID)
                navController.navigate("branches_screen/${yearID}")
            }
        } else {
            LoadingScreen()
        }
    }
}