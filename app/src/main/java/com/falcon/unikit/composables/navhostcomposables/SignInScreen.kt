package com.falcon.unikit.composables.navhostcomposables

import android.content.Context
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.LottieAnimation
import com.falcon.unikit.MainActivity
import com.falcon.unikit.R

@Composable
fun MainActivity.SignInScreen(
    navController: NavHostController,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    context: Context
) {
    BackHandler(
        onBack = {
            navController.navigate("walk_through_screen")
        }
    )
    LaunchedEffect(isSigninSuccess) {
        if (isSigninSuccess) {
            navController.navigate("sign_in_otp")
        }
    }
    GoogleSignInMainScreen {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    intentSenderLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d("TAG", e.localizedMessage.toString())
            }
    }
}

@Composable
fun GoogleSignInMainScreen(
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LottieAnimation(R.raw.login_animation)
        Spacer(modifier = Modifier.padding(32.dp))
        GoogleSignInCard(onClick = onClick)
    }
}

@Composable
fun GoogleSignInCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(id = R.drawable.ic_goole),
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Continue With Google",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}