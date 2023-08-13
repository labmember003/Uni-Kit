package com.falcon.unikit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.billingclient.api.BillingClient
import com.falcon.unikit.Utils.INITIAL_LAUCH
import com.falcon.unikit.profile.ProfileScreen
import com.falcon.unikit.ui.sign_in.GoogleAuthUiClient
import com.falcon.unikit.ui.walkthrough.WalkThroughScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val  googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var billingClient: BillingClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(navController = navController, startDestination = "walk_through_screen") {
                    composable("walk_through_screen") {
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
                        LaunchedEffect(key1 = Unit) {
                            if(googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("main_screen")
                            }
                        }
                        WalkThroughScreen {
                            navController.navigate("select_college_screen")
                        }
                    }
                    val colleges = listOf("University School of Chemical Technology",
                    "University School of Information Communication and Technology",
                    "University School of Biotechnology",
                    "University School of Automation ",
                    "Maharaja Agrasen Institute of Technology",
                    "Maharaja Surajmal Institute of Technology",
                    "Bharti Vidyapeeth's College of Engineering",
                    "Bhagwan Parshuram Institute of Technology",
                    "Vivekananda Institute of Professional Studies",
                    "Dr. Akhilesh Das Gupta Institute of Technology and Management",
                    "Guru Tegh Bahadur Institute of Technology",
                    "HMR Institute of Technology and Management",
                    "Delhi Technical Campus, Greater Noida",
                    "JIMS Engineering Management Technical Campus",
                    "Trinity Institute of Technology and Research",
                    "SBIT")
                    composable("select_college_screen") {
                        val sharedPreferences = remember {
                            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        }
                        if (sharedPreferences.getBoolean(INITIAL_LAUCH, true)) {
                            SelectCollegeScreen(colleges = colleges, navController = navController)
                        } else {
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("main_screen")
                                }
                            }
                        }
                    }
                    composable("sign_in") {
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
                        LoginScreen()
                    }
                    composable("main_screen") {

                    }
                    composable("profile") {
                        val sharedPreferences = remember {
                            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                        }
                        ProfileScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                    navController.navigate("walk_through_screen")
                                }
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.apply()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LottieAnimation(animationID: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animationID))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(400.dp)
    )
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

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

enum class PAGES {
    LOGIN, WALKTHROUGH, MAINPAGE, BUYTOKEN
}

enum class SCREEN {
    LOGIN, SIGNUP
}

@Composable
fun EditText(text: String, visualTransformation: VisualTransformation = VisualTransformation.None) {
    val content = remember { mutableStateOf("") }
    OutlinedTextField(
        value = content.value,
        onValueChange = {
            content.value = it
        },
        label = { androidx.compose.material.Text(text) },
        modifier = Modifier.padding(16.dp),
        visualTransformation = visualTransformation
    )
}