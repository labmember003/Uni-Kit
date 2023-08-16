package com.falcon.unikit.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.NavDrawerContent
import com.falcon.unikit.R

@Composable
fun MainScreen(numberOfYears: Int, navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        ModalDrawerSample(numberOfYears, navController)
    }

}

@Composable
fun ModalDrawerSample(numberOfYears: Int, navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController)
        },
        content = {
            MainScreenContent(numberOfYears)
        }
    )
}

@Composable
fun MainScreenContent(numberOfYears: Int) {
    for (i in 1..numberOfYears) {
        Image(painter = painterResource(id = R.drawable.graduation), contentDescription = "")
        Text(text = i.toString())
    }
}

@Composable
fun DrawerContent(navController: NavHostController) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.productivity))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxWidth()
            .size(300.dp)
    )
    NavDrawerContent("Profile", R.drawable.baseline_person_24) {
        navController.navigate("profile")
    }
    Spacer(modifier = Modifier.height(2.dp))
    NavDrawerContent("Settings", R.drawable.baseline_settings_24) {
        navController.navigate("settings")
    }
}
