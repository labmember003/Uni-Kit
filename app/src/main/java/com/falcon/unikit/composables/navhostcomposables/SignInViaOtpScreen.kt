package com.falcon.unikit.composables.navhostcomposables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.LottieAnimation
import com.falcon.unikit.MainActivity
import com.falcon.unikit.R
import com.falcon.unikit.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MainActivity.SignInViaOtpScreen(navController: NavHostController) {
    BackHandler(
        onBack = {
            navController.navigate("walk_through_screen")
        }
    )
    LaunchedEffect(isSigninSuccess2) {
        if (isSigninSuccess2) {
            navController.navigate("select_college_screen")
        }
    }
    OtpSignIn() { otp ->
        navController.navigate("get_otp/${otp}")
    }
}

@Composable
fun OtpSignIn(
    onClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LottieAnimation(R.raw.login_animation)
        Spacer(modifier = Modifier.padding(32.dp))
        OTPSignInCard { OTP ->
            onClick(OTP.toString())
        }
    }
}

@Composable
fun OTPSignInCard(
    onClick: (String?) -> Unit
) {
    val authViewModel : AuthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val content = remember {
            mutableStateOf("")
        }
        OutlinedTextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            label = { Text("Mobile Number") },
            modifier = Modifier
                .padding(16.dp)
                .weight(0.75f),
            visualTransformation = VisualTransformation.None,
        )
        FloatingActionButton(
            onClick = {
                scope.launch {
                    authViewModel.getOTP(content.value)
                    authViewModel.OTP.collect { OTP ->
                        onClick(OTP.toString())
                    }
                }
            },
            modifier = Modifier
                .weight(0.25f)
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