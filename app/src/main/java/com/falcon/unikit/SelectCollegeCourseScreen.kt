package com.falcon.unikit

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.Utils.INITIAL_LAUCH


@Composable
fun SelectCollegeCourseScreen(
    itemList: List<String>,
    title: String,
    sharedPrefTitle: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier
            .size(100.dp))
        Text(
            text = title,
            fontSize = 18.sp
        )
        Text(
            text = "Can Be Changed Anytime From Settings",
            color = Color.Gray,
            fontSize = 12.sp
        )
        LottieAnimation(animationID = R.raw.university)
        Picker(colleges = itemList, sharedPrefTitle = sharedPrefTitle)
        Spacer(modifier = Modifier
            .size(20.dp))
        Button(onClick = {
            onClick()
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            ) {
            Text(
                text = "NEXT",
            )
        }
    }
}
