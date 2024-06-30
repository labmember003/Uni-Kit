package com.falcon.unikit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.falcon.unikit.Utils.USER_DATA
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.api.UserData
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
import com.falcon.unikit.screens.AddNotesFAB
import com.falcon.unikit.screens.ComingSoonScreen
import com.falcon.unikit.screens.downloadPdfNotifination
import com.falcon.unikit.viewmodels.AuthViewModel
import com.falcon.unikit.viewmodels.ItemViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Random
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

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}


@Composable
fun NavDrawerContent(contentName: String, imageID: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = "" ,
            modifier = Modifier
                .size(30.dp)
        )
        Spacer(
            modifier = Modifier
                .size(5.dp)
        )
        Text(
            text = contentName,
            style = androidx.compose.material.MaterialTheme.typography.body1,
            color = Color.Unspecified
        )
    }
}

@Composable
fun Community() {
    HeadingSummarizedPage()
}

@Composable
fun NotesCard(
    contentItem: Content, icon: Int, navController: NavHostController
) {
    val context = LocalContext.current
    val expanded = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val itemViewModel : ItemViewModel = hiltViewModel()
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val token = sharedPreferences.getString(Utils.JWT_TOKEN, "").toString()
    val liked = remember {
        mutableStateOf(false)
    }
    val disliked = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        if (contentItem.like?.contains(token) == true) {
            liked.value = true
        }
        if (contentItem.dislike?.contains(token) == true) {
            disliked.value = true
        }
    }
    val likeIcon = if (liked.value) R.drawable.like_green else R.drawable.like_grey
    val dislikeIcon = if (disliked.value) R.drawable.dislike_red else R.drawable.dislike_grey

    val numLikes = remember {
        mutableStateOf(contentItem.like?.size ?: 0)
    }
    val numDisLikes = remember {
        mutableStateOf(contentItem.dislike?.size ?: 0)
    }
    val likeFunction = {
        if (liked.value && disliked.value) {
            disliked.value = false
        }
        if (disliked.value) {
            numDisLikes.value = numDisLikes.value - 1
        }
        disliked.value = false
        numLikes.value = numLikes.value.plus(if (liked.value) -1 else 1)
        liked.value = !liked.value
    }
    val dislikeFunction = {
        if (liked.value && disliked.value) {
            liked.value = false
        }
        if (liked.value) {
            numLikes.value = numLikes.value - 1
        }
        liked.value = false
        numDisLikes.value = numDisLikes.value.plus(if (disliked.value) -1 else 1)
        disliked.value = !disliked.value
    }
    val activity = LocalContext.current as? androidx.activity.ComponentActivity
    val authViewModel : AuthViewModel = hiltViewModel()
    val reportDialogueVisibility = remember {
        mutableStateOf(false)
    }
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        initiateDownloadOrLaunch(
                            contentItem,
                            context,
                            scope,
                            authViewModel,
                            navController,
                            activity
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(28.dp)
                    )
                    Spacer(modifier = Modifier.size(11.dp))
                    Text(
                        text = contentItem.contentName.toString(),
                        fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .width(119.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .height(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = likeIcon),
                            contentDescription = "likeIcon",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    scope.launch {
                                        itemViewModel.likeButtonPressed(
                                            contentItem.contentID.toString(),
                                            token
                                        )
                                    }
                                    likeFunction()
                                }
                        )
                        Text(
                            text = numLikes.value.toString(),
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colorResource(R.color.light_grey_text_image)
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Column(
                        modifier = Modifier
                            .height(50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = dislikeIcon),
                            contentDescription = "",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    scope.launch {
                                        itemViewModel.dislikeButtonPressed(
                                            contentItem.contentID.toString(),
                                            token.toString()
                                        )
                                    }
                                    dislikeFunction()
                                }
                        )
                        Text(
                            text = numDisLikes.value.toString(),
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colorResource(R.color.light_grey_text_image)
                        )
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.share_grey),
                            contentDescription = "",
                            modifier = Modifier
                                .size(23.dp)
                                .clickable {
                                    val text =
                                        "https://uni-kit-api.vercel.app/" + contentItem.contentID
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.expand),
                            contentDescription = "expand icon",
                            modifier = Modifier
                                .size(23.dp)
                                .rotate(180f)
                                .clickable {
                                    expanded.value = !expanded.value
                                    Toast
                                        .makeText(
                                            context,
                                            expanded.value.toString(),
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                }
                        )
                    }
                }

            }
            if (expanded.value) {
                Row {
                    ExpandedNotesView(reportDialogueVisibility)
                }
            }
            if (reportDialogueVisibility.value) {
                ReportDialog(
                    onDismissRequest = { reportDialogueVisibility.value = false },
                    onConfirmation = { parameter ->
                        reportDialogueVisibility.value = false
                        Log.i("meri billi ausppicious", parameter)
                        scope.launch {
                            itemViewModel.reportContent(token.toString(), contentItem.contentID.toString(), parameter)
                        }
                    },
                    dialogTitle = "Report Content",
                    icon = Icons.Default.Info
                )
            }
        }
    }
}

@Composable
fun ExpandedNotesView(reportDialogueVisibility: MutableState<Boolean>) {
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {
        UploadDateAndReport(
            date = "Uploaded on: 5 September 2024"
        ) {
            reportDialogueVisibility.value = !reportDialogueVisibility.value
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AuthorName(icon = R.drawable.user, name = "Choco Byte")
            Downloads(downloads = "5.4K")
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Comments (102)",
            fontSize = 11.sp,
            fontFamily = FontFamily(Font(R.font.nunito_regular_1)),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.6f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "",
                    modifier = Modifier
                        .size(23.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(
                        text = "Add a comment",
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_extralight)),
                    )
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Image(
                painter = painterResource(id = R.drawable.expand),
                contentDescription = "",
                modifier = Modifier
                    .size(23.dp)
                    .rotate(90F)
            )
        }
        Card(
            modifier = Modifier
                .padding(16.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp))
                .fillMaxWidth(),
            backgroundColor = colorResource(id = R.color.card_grey)
        ) {
            Column(
                Modifier
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "",
                        modifier = Modifier
                            .size(23.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "maxblagun",
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_regular_1)),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "2 weeks ago",
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_extralight)),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Woah, your project looks awesome! How long have you been coding for? I’m still new, but think I want to dive into React as well soon.",
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_regular_1)),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PlusMinusOnComment()
                    ReplyButton()
                }
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "view/hide reply (10)",
                    color = colorResource(id = R.color.custom_text_primary),
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_light_1)),
                )
            }
        }
    }
}

@Composable
private fun Downloads(downloads: String) {
    Text(
        text = "Downloads: $downloads",
        fontSize = 11.sp,
        fontFamily = FontFamily(Font(R.font.nunito_light_1)),
    )
}

@Composable
private fun AuthorName(icon: Int, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .size(23.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontFamily = FontFamily(Font(R.font.nunito_light_1)),
        )
    }
}

@Composable
private fun UploadDateAndReport(date: String, reportOnClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = date,
            fontSize = 11.sp,
            fontFamily = FontFamily(Font(R.font.nunito_light_1)),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    reportOnClick()
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.warning_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.size(7.dp))
            Text(
                text = "Report",
                fontSize = 11.sp,
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.nunito_light_1)),
            )
        }
    }
}

@Composable
private fun ReplyButton() {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.reply),
            contentDescription = "",
            modifier = Modifier
                .size(16.dp)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = "Reply",
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
            color = colorResource(id = R.color.text_purple)
        )
    }
}

@Composable
private fun PlusMinusOnComment() {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(4.dp),
        backgroundColor = colorResource(id = R.color.off_white)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(14.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "5",
                color = colorResource(id = R.color.custom_text_primary),
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Image(
                painter = painterResource(id = R.drawable.minus),
                contentDescription = "",
                modifier = Modifier
                    .size(14.dp)
            )
        }
    }
}

private fun initiateDownloadOrLaunch(
    contentItem: Content,
    context: Context,
    scope: CoroutineScope,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    activity: ComponentActivity?
) {
    val fileName = contentItem.contentID + ".pdf"
    Log.i("asdfvfdfefe", fileName)
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
        fileName
    )
    scope.launch {
        authViewModel.getDownloadableURL(contentItem.contentID.toString())
        authViewModel.downloadableURL.collect { downloadableURL ->
            if (file.exists()) {
                val uri: String? = Uri
                    .fromFile(file)
                    .toString()
                val encoded = uri?.let { encode(it) }
                navController.navigate("open_file/${encoded}")
                Toast
                    .makeText(context, "exosts", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast
                    .makeText(
                        context,
                        "Please Wait Downloading is being starting",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                val notificationId = Random().nextInt()
                downloadPdfNotifination(
                    context,
                    downloadableURL.githuburl.toString(),
                    notificationId,
                    scope,
                    activity,
                    contentItem
                )
                // UPAR WAALE CODE SE FILE SAVE HOGI WITH NAME : contentItem.contentID + "ENC" + ".pdf"
                // NICHE WAALE CODE SE NEW FILE BNEGI WITH NAME : contentItem.contentID + ".pdf"
                // AUR FIR ENC WAALI FILE KO DELETE KR DENGE

                Toast
                    .makeText(context, "Downloading started", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _WithdrawalHistory() {
    WithdrawalHistory(NavHostController(LocalContext.current))
}

@Composable
fun WithdrawalHistory(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = "WithDrawal History",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(
            modifier = Modifier
                .size(24.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "$ 100",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Current Balance",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(24.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        }
        Spacer(
            modifier = Modifier
                .size(24.dp)
        )
        Text(
            text = "History",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        LazyColumn(content = {
            val redeemData = listOf(
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12")
            )
            items(redeemData) { content ->
                WithdrawalListItem(content.icon ?: 0, content.title.toString(), content.dateText.toString(), content.cost.toString())
            }
        })
    }
}

@Composable
private fun WithdrawalListItem(icon: Int, title: String, dateText: String, cost: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Redeem Brand Icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                )
                Text(
                    text = dateText,
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
            }
        }
        Text(
            text = "-$cost",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1))
        )
    }
}

fun encode(url: String): String = URLEncoder.encode(url, "UTF-8")
fun decode(url: String) = URLDecoder.decode(url, "UTF-8")