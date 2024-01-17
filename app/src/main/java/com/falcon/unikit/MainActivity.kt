package com.falcon.unikit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.Utils.COLLEGE_ID
import com.falcon.unikit.Utils.COURSE_ID
import com.falcon.unikit.Utils.USER_DATA
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.api.UserData
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.profile.ProfileScreen
import com.falcon.unikit.screens.AddNotesFAB
import com.falcon.unikit.screens.BottomSheetContent
import com.falcon.unikit.screens.ComingSoonScreen
import com.falcon.unikit.screens.ContentItemRow
import com.falcon.unikit.screens.ContentScreen
import com.falcon.unikit.screens.MainScreen
import com.falcon.unikit.screens.downloadPdfNotifination
import com.falcon.unikit.screens.getIcon
import com.falcon.unikit.settings.SettingsScreen
import com.falcon.unikit.ui.walkthrough.WalkThroughScreen
import com.falcon.unikit.viewmodels.AuthViewModel
import com.falcon.unikit.viewmodels.BranchViewModel
import com.falcon.unikit.viewmodels.CollegeViewModel
import com.falcon.unikit.viewmodels.ContentViewModel
import com.falcon.unikit.viewmodels.CourseViewModel
import com.falcon.unikit.viewmodels.ItemViewModel
import com.falcon.unikit.viewmodels.YearViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Random
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private var isSigninSuccess by mutableStateOf(false)
    private var isSigninSuccess2 by mutableStateOf(false)
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
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("TAG", "Got ID token.")
                            Log.i("googleOneTap", idToken)
                            val email = credential.id
                            Log.i("emailemail", email)
                            Log.i("emailemail2", credential.googleIdToken.toString())
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
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
                        LaunchedEffect(key1 = Unit) {
                            val userDataJson = sharedPreferences.getString(USER_DATA, "NO_USER_FOUND")
                            if (userDataJson != "NO_USER_FOUND") {
                                navController.navigate("select_college_screen")
                            }
                        }
                        WalkThroughScreen {
                            navController.navigate("sign_in")
                        }
                    }
                    composable("sign_in") {
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
                                .addOnSuccessListener(this@MainActivity) { result ->
                                    try {
                                        val intentSenderRequest = IntentSenderRequest
                                            .Builder(result.pendingIntent.intentSender).build()
                                        intentSenderLauncher.launch(intentSenderRequest)
                                    } catch (e: IntentSender.SendIntentException) {
                                        Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener(this@MainActivity) { e ->
                                    // No Google Accounts found. Just continue presenting the signed-out UI.
                                    Log.d("TAG", e.localizedMessage)
                                }
                        }
                    }
                    composable("sign_in_otp") {
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
                    composable(
                        route= "get_otp/{otp}",
                        arguments = listOf(
                            navArgument("otp") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->
                        val otp = entry.arguments?.getString("otp")
                        OTPScreen(otp) {
                            navController.navigate("select_college_screen")
                        }
                    }
                    composable("comicify") {
                        FullWebView("https://mediafiles.botpress.cloud/865dac6b-a4c7-49f9-91e9-4e45c76ee3cc/webchat/bot.html")
                    }
                    composable("select_college_screen") {
//                        TODO("CHANGE INITIAL_LAUCH TO IS COLLEGE SELECTED OR IS COURSE SELECTED")
                        LaunchedEffect(Unit) {
                            val collegeID = sharedPreferences.getString(COLLEGE_ID, "NO_COLLEGE_SELECTED")
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
                        val collegeViewModel : CollegeViewModel = hiltViewModel()
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
                                        Toast.makeText(this@MainActivity, "Please Select College", Toast.LENGTH_SHORT).show()
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
                    composable(
                        "select_course_screen/{collegeID}",
                        arguments = listOf(
                            navArgument("collegeID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        LaunchedEffect(Unit) {
                            val courseID = sharedPreferences.getString(COURSE_ID, "NO_COURSE_SELECTED")
                            if (courseID != "NO_COURSE_SELECTED") {
                                navController.navigate("main_screen/${courseID}")
                            }
                        }
                        val errorState = remember { mutableStateOf(false) }
                        val courseViewModel : CourseViewModel = hiltViewModel()
                        val courses: State<List<CourseItem>> = courseViewModel.courses.collectAsState()
                        Log.i("catcatcatcat", courses.value.size.toString())
                        LaunchedEffect(key1 = errorState.value) {
                            val timeoutDurationMillis = 15000L // 15 seconds (adjust as needed)
                            delay(timeoutDurationMillis) // Wait for the timeout duration
                            // Check if the data is still not available (i.e., an error occurred)
                            if (courses.value.isEmpty()) {
                                errorState.value = true
                            }
                        }
                        if (errorState.value) {
                            // Show error message and retry button
                            ErrorPage {
                                errorState.value = false
                            }
                        } else {
                            if (courses.value != emptyList<CourseItem>()) {
                                SelectCourseScreen(
                                    itemList = courses.value,
                                    title = "Select Your Course",
                                    sharedPrefTitle = "COURSE",
                                    sharedPreferences = sharedPreferences
                                ) { courseID ->
                                    if (courseID == null) {
                                        Toast.makeText(this@MainActivity, "Please Select Course", Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.navigate("main_screen/${courseID}")
                                    }
                                }
                            } else {
                                LoadingScreen()
                            }
                        }
                    }
                    composable(
                        "main_screen/{courseID}",
                        arguments = listOf(
                            navArgument("courseID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
                        val errorState = remember { mutableStateOf(false) }
                        val yearViewModel : YearViewModel = hiltViewModel()
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
                                MainScreen(
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
                    composable(
                        "branches_screen/{yearID}",
                        arguments = listOf(
                            navArgument("yearID") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val branchViewModel : BranchViewModel = hiltViewModel()
                        val branches: State<List<BranchItem>> = branchViewModel.branches.collectAsState()
                        if (branches.value != emptyList<BranchItem>()) {
                            BranchesScreen(
                                branches.value
                            ) { subjectID, subjectName ->
                                navController.navigate("content_screen/${subjectID}/${subjectName}/${"0"}")
                            }
                        } else {
                            LoadingScreen()
                        }
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
                        val subjectID = entry.arguments?.getString("subjectID")
                        val subjectName = entry.arguments?.getString("subjectName")
                        val recompose = { pageNumber: String ->
                            navController.popBackStack()
                            navController.navigate("content_screen/${subjectID}/${subjectName}/${pageNumber}")
                        }



                        val contentViewModel : ContentViewModel = hiltViewModel()
                        val content: State<List<Content>> = contentViewModel.contents.collectAsState()
                        if (content.value != emptyList<Content>()) {
                            ContentScreenFigma(
                                content.value,
                                navController,
                                entry.arguments?.getString("subjectName"),
                                entry.arguments?.getString("pageNumber"),
                                recompose
                            )
                        } else {
                            LoadingScreen()
                        }
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
                        val scope = rememberCoroutineScope()
                        val contentId = entry.arguments?.getString("contentId")
                        val authViewModel : AuthViewModel = hiltViewModel()
                        scope.launch {
                            authViewModel.getContentFromContentID(contentId!!)
                        }
                        val content by rememberUpdatedState(authViewModel.contentFromID.collectAsState())
                        if (content.value.isNotEmpty()) {
                            DisplayFileDeepLink(content.value[0], navController)
                        } else {
                            LoadingScreen()
                        }
                    }
                    composable("community") {
                        Community()
                    }
                    composable("profile") {
                        val gson = Gson()
                        val userData = gson.fromJson(sharedPreferences.getString(USER_DATA, null), UserData::class.java)
                        ProfileScreen(
                            userData = userData,
                            onSignOut = {
                                isSigninSuccess = false
                                navController.navigate("walk_through_screen")
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.apply()
                            },
                            navController
                        )
                    }
                    composable("settings") {
                        SettingsScreen ({
                            navController.popBackStack()
                        }, {
                            navController.navigate("select_college_screen")
                        },{
                            isSigninSuccess = false
                            navController.navigate("walk_through_screen")
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()
                        })
                    }

                    composable("my_notes") {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { androidx.compose.material.Text(text = "My Notes") },
                                    contentColor = androidx.compose.material.MaterialTheme.colors.onSurface,
                                    backgroundColor = Color.Transparent,
                                    elevation = 0.dp,
                                    navigationIcon = {
                                        IconButton(
                                            onClick = {
                                                navController.popBackStack()
                                            },
                                            content = {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowBack,
                                                    contentDescription = null,
                                                )
                                            },
                                        )
                                    },
                                    modifier = Modifier.statusBarsPadding(),
                                )
                            },
                        ) { innerPadding ->
                            innerPadding
                            val authViewModel : AuthViewModel = hiltViewModel()
                            val jwtToken = sharedPreferences.getString(Utils.JWT_TOKEN, "USER_NOT_SIGN_IN")
                            Log.i("JWT_TOKEN", jwtToken.toString())
                            if (jwtToken == null || jwtToken == "USER_NOT_SIGN_IN") {
                                UserNotFound(navController)
                            } else {
                                authViewModel.getMyNotes(jwtToken)
                                val myNotes: State<List<MyNoteItem>> = authViewModel.myNotes.collectAsState()
                                if (myNotes.value.isEmpty()) {
                                    LoadingScreen()
                                }
                                LazyColumn(content = {
                                    items(myNotes.value) { myNote ->
                                        MyNotesItem(myNote)
                                    }
                                })
                            }
                        }
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
                }
            }
        }
    }
    private fun getIcon(contentName: String): Int {
        when (contentName) {
            "Notes" -> {
                return R.drawable.notes
            }
            "Books" -> {
                return R.drawable.book
            }
            "Papers" -> {
                return R.drawable.exam
            }
            "Playlists" -> {
                return R.drawable.playlisticon
            }
            "Syllabus" -> {
                return R.drawable.syllabusicon
            }
            else -> return R.drawable.error
        }

    }
    @Composable
    fun MyNotesItem(myNote: MyNoteItem) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
//                Todo(download and view file)
//                  download(contentItem.downloadURL)
                    },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = getIcon(myNote.itemType.toString())),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = myNote.notesName.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxSize()
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp, // Use the thumbs-up icon from Icons.Default
                            contentDescription = "Thumbs Up",
                            modifier = Modifier.padding(8.dp) // Adjust padding as needed
                        )
                    }
                    Text(
                        text = myNote.like?.size.toString(),
                    )
                    IconButton(
                        modifier = Modifier,
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown, // Use the thumbs-up icon from Icons.Default
                            contentDescription = "Thumbs Up",
                            modifier = Modifier.padding(8.dp) // Adjust padding as needed
                        )
                    }
                    Text(
                        text = myNote.dislike?.size.toString()
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                val collegeList = remember {
                    mutableStateOf(
                        myNote.college?.map { myNote ->
                            myNote.collegeName
                        }
                    )
                }
                val courseList = remember {
                    mutableStateOf(
                        myNote.course?.map { myNote ->
                            myNote.courseName
                        }
                    )
                }
                val branchList = remember {
                    mutableStateOf(
                        myNote.branch?.map { myNote ->
                            myNote.branchName
                        }
                    )
                }
                val yearList = remember {
                    mutableStateOf(
                        myNote.year?.map { myNote ->
                            myNote.numofYear
                        }
                    )
                }
                val subjectList = remember {
                    mutableStateOf(
                        myNote.subject?.map { myNote ->
                            myNote.subjectName
                        }
                    )
                }
                Text(
                    text = "College: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "course",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "branch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "year",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "subject",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    @Composable
    private fun UserNotFound(
        navController: NavHostController
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(animationID = R.raw.error)
            Text(
                text = "Account Not Signined"
            )
            Spacer(
                modifier = Modifier
                    .size(20.dp)
            )
            Button(
                onClick = {
                    navController.navigate("sign_in")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = "SIGN IN",
                )
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
                tint = androidx.compose.material.MaterialTheme.colors.primary,
            )
        }
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
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(animationID = R.raw.loading_cats)
        Text (
            text = "Loading..."
        )
    }
}

@Composable
fun ErrorPage(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(animationID = R.raw.error_cat)
        Text (
            text = "Something Went Wrong..."
        )
        Spacer(modifier = Modifier
            .size(20.dp))
        Button(
            onClick = {
                onClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            Text(
                text = "RETRY",
            )
        }
    }
}

fun saveUserDataToSharedPreferences(context: Context, userData: UserData, sharedPreferences: SharedPreferences) {
    val editor = sharedPreferences.edit()
    val gson = Gson()
    editor.putString("user_data_key", gson.toJson(userData))
    editor.apply()
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
                label = { androidx.compose.material.Text("Mobile Number") },
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
                    tint = androidx.compose.material.MaterialTheme.colors.primary,
                )
            }
        }

}

@Composable
fun Community() {
    HeadingSummarizedPage()
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _ContentScreenFigma() {
    ContentScreenFigma(
        listOf(Content("")),
        NavHostController(LocalContext.current),
        "",
        ""
    ) {}
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContentScreenFigma(
    content: List<Content>,
    navController: NavHostController,
    subjectName: String?,
    pageNumber: String?,
    recompose: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val list = listOf("Notes", "Books", "Papers", "Playlists", "Syllabus")
    val pageState = rememberPagerState(pageNumber?.toInt() ?: 0)

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val currentType = remember {
        mutableStateOf("Type")
    }
    val currentSubject = remember {
        mutableStateOf(subjectName)
    }
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheetContent(modalSheetState, currentType, currentSubject, content, recompose, pageState.currentPage)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp)
        ) {
            ContentScreenTitle()
            HorizontalPager(
                pageCount = list.size,
                state = pageState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                pageContent = { pageNumber ->
                    val specificContent = content.filter {
                        it.contentType == list[pageNumber]
                    }
                    LaunchedEffect(key1 = pageNumber) {
                        currentType.value = list[pageNumber]
                        Log.i("catcatcatwty2", pageNumber.toString())
                    }
                    NotesList(specificContent, navController, getIcon(list[pageNumber], true), modalSheetState)
                }
            )

            TabRow(
                selectedTabIndex = pageState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                list.forEachIndexed { index, _ ->
                    Tab(
                        modifier = Modifier.fillMaxWidth(),
                        selectedContentColor = colorResource(R.color.icon_blue),
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = getIcon(list[index], pageState.currentPage == index)),
                                    contentDescription = "Icon",
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                                if (pageState.currentPage == index) {
                                    androidx.compose.material.Text(
                                        list[index],
                                        fontSize = 13.sp,
                                        // on below line we are specifying the text color
                                        // for the text in that tab
                                        color = if (pageState.currentPage == index) colorResource(R.color.icon_blue) else Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                                if (pageState.currentPage == index) {

                                }
                            }

                        },
                        // on below line we are specifying
                        // the tab which is selected.
                        selected = pageState.currentPage == index,
                        // on below line we are specifying the
                        // on click for the tab which is selected.
                        onClick = {
                            // on below line we are specifying the scope.
                            Log.i("happppy", pageState.currentPage.toString())
                            Log.i("happppy2", index.toString())
                            scope.launch {
                                pageState.scrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesList(
    content: List<Content>,
    navController: NavHostController,
    icon: Int,
    modalSheetState: ModalBottomSheetState
) {
    if (content.isEmpty()) {
        ComingSoonScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                val sortedItems = content.sortedByDescending { it.like?.size }
                items(sortedItems) { content ->
                    NotesCard(content, icon, navController)
                }
            })
        }
    }
    AddNotesFAB(modalSheetState)
}

@Composable
private fun NotesCard(
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
    val showMenu = remember {
        mutableStateOf(false)
    }
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
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
            }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
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
                            painter = painterResource(id = R.drawable.dots),
                            contentDescription = "",
                            modifier = Modifier
                                .size(23.dp)
                                .clickable {
//                                TODO (Show menu option)
                                }
                        )
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
                    ExpandedNotesView()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExpandedNotesView() {
    Column(
        modifier = Modifier
            .padding(16.dp),
    ) {
        UploadDate("Uploaded on: 5 September 2024")
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
private fun UploadDate(date: String) {
    Text(
        text = date,
        fontSize = 11.sp,
        fontFamily = FontFamily(Font(R.font.nunito_light_1)),
    )
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

@Composable
private fun ContentScreenTitle() {
    Text(
        text = "Unikit",
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
    Spacer(
        modifier = Modifier
            .size(23.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _RewardHistory() {
    RewardHistory(NavHostController(LocalContext.current))
}
@Composable
fun RewardHistory(navController: NavHostController) {
    MyRewardsUpperComposable(navController)
}


@Composable
private fun MyRewardsUpperComposable(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = "Reward History",
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
                text = "$ 667.89",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Total Rewards",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = "$ 667.89",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "103 Rewards",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            LazyColumn(content = {
                val rewardList = listOf(
                    Reward("1 January 2024", "$8.19"),
                    Reward("2 January 2024", "$8.19"),
                    Reward("3 January 2024", "$8.19"),
                    Reward("4 January 2024", "$8.19"),
                    Reward("5 January 2024", "$8.19"),
                    Reward("6 January 2024", "$8.19"),
                    Reward("7 January 2024", "$8.19")
                )
                items(rewardList) { content ->
                    RewardComposable(date = content.date.toString(), amount = content.amount.toString())
                }
            })
        }
    }
}

@Composable
private fun RewardComposable(date: String, amount: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_icon_sec),
                contentDescription = "menu icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
                Text(
                    text = "Reward",
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
            }
        }
        Text(
            text = amount,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
            color = colorResource(id = R.color.greenny)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _Redeem() {
    Redeem(NavHostController(LocalContext.current))
}
@Composable
fun Redeem(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        MyBalanceComposable(navController)
        Spacer(modifier = Modifier.size(30.dp))
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(48.dp, 48.dp, 0.dp, 0.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "WithDraw Coins",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.padding(6.dp))
                LazyColumn(content = {
                    val data = listOf(
                        WithdrawalCoins(R.drawable.paytm_icon, "Paytm Withdrawal", "$10", "$12"),
                        WithdrawalCoins(R.drawable.paytm_icon, "Paytm Withdrawal", "$20", "$22"),
                        WithdrawalCoins(R.drawable.amazon_icon, "Amazon Withdrawal", "$10", "$12"),
                        WithdrawalCoins(R.drawable.amazon_icon, "Amazon Withdrawal", "$20", "$22")
                    )
                    items(data) { content ->
                        WithdrawCoinsComposable(content.icon ?: 0, content.title.toString(), content.withdrawalAmount.toString(), content.withdrawalCoins.toString())
                    }
                })
            }
        }
    }
}

@Composable
private fun MyBalanceComposable(navController: NavHostController) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(0.dp, 0.dp, 48.dp, 48.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "My Balance",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(
                modifier = Modifier
                    .size(32.dp)
            )
            Text(
                text = "$ 100.0",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Total Balance",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(32.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    backgroundColor = colorResource(id = R.color.light_purple),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("reward_history")
                        },
                    shape = RoundedCornerShape(48.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_history_24),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(
                            text = "Reward History"
                        )
                    }
                }
                Card(
                    backgroundColor = colorResource(R.color.light_green),
                    modifier = Modifier
                        .clickable {
                                   navController.navigate("withdrawal_history")
                        },
                    shape = RoundedCornerShape(48.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.transaction),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(
                            text = "Recent Transactions"
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun WithdrawCoinsComposable(
    icon: Int,
    title: String,
    withdrawalAmount: String,
    withdrawalCoins: String
) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(48.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "Paytm Icon",
                    modifier = Modifier
                        .size(25.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    )
                    Text(
                        text = withdrawalAmount,
                        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    )
                }
            }
            val openAlertDialog = remember { mutableStateOf(false) }
            when {
                openAlertDialog.value -> {
                    AlertDialogExample(
                        onDismissRequest = { openAlertDialog.value = false },
                        onConfirmation = {
                            openAlertDialog.value = false
//                            TODO ("PROCESS THE PURCHASE")
                            println("Confirmation registered") // Add logic here to handle confirmation.
                        },
                        dialogTitle = "Purchase Confirmation",
                        dialogText = "Are you sure to withdraw $title of $withdrawalAmount for $withdrawalCoins coins ?",
                        icon = Icons.Default.Info
                    )
                }
            }
            Card(
                backgroundColor = colorResource(id = R.color.light_blue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .clickable {
                        openAlertDialog.value = true
                    }
            ) {
                Row(
                    Modifier.padding(24.dp)
                ) {
                    Text(text = withdrawalCoins)
                }

            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
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

fun openUrlInBrowser(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}


@Composable
fun FullWebView(url: String) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    // Configure the WebView
    webView.settings.javaScriptEnabled = true
    webView.webViewClient = WebViewClient()
    webView.webChromeClient = WebChromeClient()

    // Load the URL
    webView.loadUrl(url)

    // Create AndroidView to display WebView
    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    ) { webView ->
        // WebView is configured and loaded with the provided URL
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayFileDeepLink(content : Content, navController: NavHostController) {
    val icon = getIcon(content.contentType.toString(), true)
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            androidx.compose.material.Text(
                text = "Content",
                style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
        NotesCard(contentItem = content, icon = icon,  navController = navController)
    }

}

fun encode(url: String): String = URLEncoder.encode(url, "UTF-8")
fun decode(url: String) = URLDecoder.decode(url, "UTF-8")