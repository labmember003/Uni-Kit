package com.falcon.unikit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.falcon.unikit.Utils.USER_DATA
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.composables.navhostcomposables.AppContentScreen
import com.falcon.unikit.composables.navhostcomposables.AppMainScreen
import com.falcon.unikit.composables.navhostcomposables.AppOtpScreen
import com.falcon.unikit.composables.navhostcomposables.AppWalkThroughScreen
import com.falcon.unikit.composables.navhostcomposables.DisplayFileViaDeepLinkScreen
import com.falcon.unikit.composables.navhostcomposables.FullWebView
import com.falcon.unikit.composables.navhostcomposables.MyNotesScreen
import com.falcon.unikit.composables.navhostcomposables.Redeem
import com.falcon.unikit.composables.navhostcomposables.ReferAndEarnScreen
import com.falcon.unikit.composables.navhostcomposables.RewardHistory
import com.falcon.unikit.composables.navhostcomposables.SelectYourBranchScreen
import com.falcon.unikit.composables.navhostcomposables.SelectYourCollegeScreen
import com.falcon.unikit.composables.navhostcomposables.SelectYourCourseScreen
import com.falcon.unikit.composables.navhostcomposables.SignInScreen
import com.falcon.unikit.composables.navhostcomposables.SignInViaOtpScreen
import com.falcon.unikit.composables.navhostcomposables.UserProfileScreen
import com.falcon.unikit.composables.navhostcomposables.UserSettingsScreen
import com.falcon.unikit.composables.navhostcomposables.WithdrawalHistory
import com.falcon.unikit.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var oneTapClient: SignInClient
    lateinit var signUpRequest: BeginSignInRequest
    var isSigninSuccess by mutableStateOf(false)
    var isSigninSuccess2 by mutableStateOf(false)
    @Inject
    lateinit var unikitAPI: UnikitAPI

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
        val intentSenderLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate with your backend.
                            Log.d("TAG", "Got ID token.")
                            Log.i("googleOneTap", idToken)
                            val email = credential.id
                            Log.i("emailemail", email) // To Log Email
                            Log.i("emailemail2", credential.googleIdToken.toString()) // To Log Email and googleIDToken
                            val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
                            lifecycleScope.launch {
                                authViewModel.getToken(idToken)
                                authViewModel.jwtToken.collect { userData ->
//                                    val clipboardManager = ContextCompat.getSystemService(
//                                        this@MainActivity,
//                                        ClipboardManager::class.java
//                                    ) as ClipboardManager?
//                                    clipboardManager?.let {
//                                        val clipData = ClipData.newPlainText("label", userData.token)
//                                        it.setPrimaryClip(clipData)
//
//                                        Toast.makeText(
//                                            this@MainActivity,
//                                            "token copied to clipboard",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
                                    val sharedPreferences = this@MainActivity.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                                    if (userData.user != "") {
                                        val editor = sharedPreferences.edit()
                                        editor.putString(Utils.JWT_TOKEN, userData.token)
                                        editor.apply()
//                                        Toast.makeText(this@MainActivity, userData.token, Toast.LENGTH_SHORT).show()
                                    }
                                    val editor = sharedPreferences.edit()
                                    val gson = Gson()
                                    editor.putString(USER_DATA, gson.toJson(userData))
                                    editor.apply()
                                }
                            }
                            isSigninSuccess = true
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d("TAG", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                val context = LocalContext.current
                val sharedPreferences = remember {
                    context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                }
                NavHost(navController = navController, startDestination = "walk_through_screen") {
                    composable("walk_through_screen") {
                        AppWalkThroughScreen(sharedPreferences, navController)
                    }
                    composable("sign_in") {
                        SignInScreen(navController, intentSenderLauncher, context)
                    }
                    composable("sign_in_otp") {
                        SignInViaOtpScreen(navController)
                    }
                    composable(
                        route= "get_otp/{otp}",
                        arguments = listOf(
                            navArgument("otp") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->
                        AppOtpScreen(entry, navController)
                    }
                    composable("comicify") {
                        FullWebView("https://mediafiles.botpress.cloud/865dac6b-a4c7-49f9-91e9-4e45c76ee3cc/webchat/bot.html")
                    }
                    composable("select_college_screen") {
                        SelectYourCollegeScreen(sharedPreferences, navController)
                    }
                    composable(
                        "select_course_screen/{collegeID}",
                        arguments = listOf(
                            navArgument("collegeID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        SelectYourCourseScreen(sharedPreferences, navController)
                    }
                    composable(
                        "main_screen/{courseID}",
                        arguments = listOf(
                            navArgument("courseID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        AppMainScreen(navController)
                    }
                    composable(
                        "branches_screen/{yearID}",
                        arguments = listOf(
                            navArgument("yearID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        SelectYourBranchScreen(navController)
                    }
                    composable(
                        "content_screen" + "/{subjectID}" + "/{subjectName}" + "/{pageNumber}",
                        arguments = listOf(
                            navArgument("subjectID") {
                                type = NavType.StringType
                                nullable = false
                            },
                            navArgument("subjectName") {
                                type = NavType.StringType
                                nullable = false
                            },
                            navArgument("pageNumber") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) { entry ->
                        AppContentScreen(entry, navController)
                    }
                    composable(
                        route = "open_file" + "/{uri}",
                        arguments = listOf(
                            navArgument("uri") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->
                        val uriString = entry.arguments?.getString("uri")
                        val decoded = uriString?.let { decode(it) }
                        LaunchedEffect(key1 = Unit) {
                            val intent = Intent(this@MainActivity, PDFviewActivity::class.java)
                            intent.putExtra("uri", decoded)
                            startActivity(intent)
                        }
                    }
                    composable(
                        route = "display_file_deeplink",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "https://uni-kit-api.vercel.app/{contentId}"
                                action = Intent.ACTION_VIEW
                            }
                        ),
                        arguments = listOf(
                            navArgument("contentId") {
                                type = NavType.StringType
                                defaultValue = "null"
                            }
                        )
                    ) { entry ->
                        DisplayFileViaDeepLinkScreen(entry, navController)
                    }
                    composable("community") {
                        Community()
                    }
                    composable("profile") {
                        UserProfileScreen(sharedPreferences, navController)
                    }
                    composable("settings") {
                        UserSettingsScreen(navController, sharedPreferences)
                    }
                    composable("my_notes") {
                        MyNotesScreen(navController, sharedPreferences)
                    }
                    composable("redeem") {
                        Redeem(navController)
                    }
                    composable("reward_history") {
                        RewardHistory(navController)
                    }
                    composable("withdrawal_history") {
                        WithdrawalHistory(navController)
                    }
                    composable("refer_and_earn") {
                        ReferAndEarnScreen()
                    }
                }
            }
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean { // Don't Remove This Function
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun Community() {
    HeadingSummarizedPage()
}

fun encode(url: String): String = URLEncoder.encode(url, "UTF-8")
fun decode(url: String) = URLDecoder.decode(url, "UTF-8")