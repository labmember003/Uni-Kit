package com.falcon.unikit

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.falcon.unikit.screens.ContentScreen
import com.falcon.unikit.screens.MainScreen
import com.falcon.unikit.screens.OtpComp
import com.falcon.unikit.settings.SettingsScreen
import com.falcon.unikit.ui.walkthrough.WalkThroughScreen
import com.falcon.unikit.viewmodels.AuthViewModel
import com.falcon.unikit.viewmodels.BranchViewModel
import com.falcon.unikit.viewmodels.CollegeViewModel
import com.falcon.unikit.viewmodels.ContentViewModel
import com.falcon.unikit.viewmodels.CourseViewModel
import com.falcon.unikit.viewmodels.YearViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private var isSigninSuccess by mutableStateOf(false)
    private var isSigninSuccess2 by mutableStateOf(false)
    @Inject
    lateinit var unikitAPI: UnikitAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                    val clipboardManager = ContextCompat.getSystemService(
                                        this@MainActivity,
                                        ClipboardManager::class.java
                                    ) as ClipboardManager?
                                    clipboardManager?.let {
                                        val clipData = ClipData.newPlainText("label", userData.token)
                                        it.setPrimaryClip(clipData)

                                        Toast.makeText(
                                            this@MainActivity,
                                            "token copied to clipboard",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


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
                        OtpSignIn {
                            navController.navigate("get_otp")

                        }
                    }
                    composable("get_otp") {
                        OTPScreen {
                            navController.navigate("select_college_screen")
                        }
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
                                navController.navigate("content_screen/${subjectID}/${subjectName}")
                            }
                        } else {
                            LoadingScreen()
                        }
                    }
                    composable(
                        "content_screen" + "/{subjectID}" + "/{subjectName}",
                        arguments = listOf(
                            navArgument("subjectID") {
                                type = NavType.StringType
                                nullable = false
                            },
                            navArgument("subjectName") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) { entry ->
                        val contentViewModel : ContentViewModel = hiltViewModel()
                        val content: State<List<Content>> = contentViewModel.contents.collectAsState()
                        if (content.value != emptyList<Content>()) {
                            ContentScreen(
                                content.value,
                                navController,
                                entry.arguments?.getString("subjectName")
                            )
                        } else {
                            LoadingScreen()
                        }
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
fun OTPScreen(onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "ENTER OTP",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        LottieAnimation(R.raw.otp)
        Spacer(modifier = Modifier.padding(32.dp))
        OtpComp()
        Spacer(modifier = Modifier
            .size(30.dp))
        FloatingActionButton(
            onClick = {
                onClick()
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
        OTPSignInCard(onClick = onClick)
    }
}





@Composable
fun OTPSignInCard(
    onClick: () -> Unit
) {

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
                    onClick()
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