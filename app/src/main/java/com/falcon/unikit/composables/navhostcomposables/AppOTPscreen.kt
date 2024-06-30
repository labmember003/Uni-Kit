package com.falcon.unikit.composables.navhostcomposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.LottieAnimation
import com.falcon.unikit.R

@Composable
fun AppOtpScreen(
    entry: NavBackStackEntry,
    navController: NavHostController
) {
    val otp = entry.arguments?.getString("otp")
    OTPScreen(otp) {
        navController.navigate("select_college_screen")
    }
}

@Composable
fun OTPScreen(OTP: String?, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            text = "ENTER OTP",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        LottieAnimation(R.raw.otp)
        Spacer(modifier = Modifier.padding(32.dp))
//        OtpComp()
        val otp = remember {
            mutableStateOf("")
        }
        OutlinedTextField(
            value = otp.value,
            onValueChange = {
                otp.value = it
            },
            label = { androidx.compose.material.Text("OTP") },
            modifier = Modifier
                .padding(16.dp),
            visualTransformation = VisualTransformation.None,
        )
        Spacer(modifier = Modifier
            .size(30.dp))
        FloatingActionButton(
            onClick = {
                if (OTP == otp.value) {
                    onClick()
                }
                onClick() // TODO REMOVE THIS LINE AND CHECK IF OTP IS OK OR NOT
                //TODO HANDLE INCORRECT OTP
                //TODO HANDLE INCORRECT OTP
                //TODO HANDLE INCORRECT OTP
                //TODO HANDLE INCORRECT OTP
            },
            modifier = Modifier
                .padding(4.dp)
                .size(56.dp),
            shape = RoundedCornerShape(percent = 30),
        ) {
            Icon(
                imageVector = Icons.Filled.NavigateNext,
                contentDescription = "Go",
                tint = MaterialTheme.colors.primary,
            )
        }
    }
}