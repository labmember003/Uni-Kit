package com.falcon.unikit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.billingclient.api.BillingClient
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.UnikitAPI
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.YearItem
import com.falcon.unikit.models.item.CollegeItem
import com.falcon.unikit.models.item.CourseItem
import com.falcon.unikit.profile.ProfileScreen
import com.falcon.unikit.screens.ContentScreen
import com.falcon.unikit.screens.MainScreen
import com.falcon.unikit.settings.SettingsScreen
import com.falcon.unikit.ui.sign_in.GoogleAuthUiClient
import com.falcon.unikit.ui.walkthrough.WalkThroughScreen
import com.falcon.unikit.viewmodels.BranchViewModel
import com.falcon.unikit.viewmodels.CollegeViewModel
import com.falcon.unikit.viewmodels.ContentViewModel
import com.falcon.unikit.viewmodels.CourseViewModel
import com.falcon.unikit.viewmodels.YearViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var unikitAPI: UnikitAPI

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
//                    composable("test") {
//                        Test()
//                    }
                    composable("walk_through_screen") {
                        BackHandler(
                            onBack = {
                                finish()
                            }
                        )
//                        LaunchedEffect(key1 = Unit) {
//                            if(googleAuthUiClient.getSignedInUser() != null) {
//                                navController.navigate("main_screen")
//                            }
//                        }
                        WalkThroughScreen {
                            navController.navigate("select_college_screen")
                        }
                    }
                    composable("select_college_screen") {
//                        TODO("CHANGE INITIAL_LAUCH TO IS COLLEGE SELECTED OR IS COURSE SELECTED")
                        val collegeViewModel : CollegeViewModel = hiltViewModel()
                        val colleges: State<List<CollegeItem>> = collegeViewModel.colleges.collectAsState()
                        if (colleges.value != emptyList<CollegeItem>()) {
                            SelectCollegeScreen(
                                itemList = colleges.value,
                                title = "Select Your CollegeItem",
                                sharedPrefTitle = "COLLEGE"
                            ) { collegeID ->
                                navController.navigate("select_course_screen/${collegeID}")
                                Log.i("catcatcat", collegeID)
                            }
                        } else {
                           LoadingScreen()
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
                        val courseViewModel : CourseViewModel = hiltViewModel()
                        val courses: State<List<CourseItem>> = courseViewModel.courses.collectAsState()
                        if (courses.value != emptyList<CollegeItem>()) {
                            SelectCourseScreen(
                                itemList = courses.value,
                                title = "Select Your Course",
                                sharedPrefTitle = "COURSE"
                            ) { courseID ->
                                navController.navigate("main_screen/${courseID}")
                            }
                        } else {
                            LoadingScreen()
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
                        val yearViewModel : YearViewModel = hiltViewModel()
                        val years: State<List<YearItem>> = yearViewModel.years.collectAsState()
                        if (years.value != emptyList<YearItem>()) {
                            MainScreen(
                                yearList = years,
                                navController = navController
                            ) { yearID ->
                                navController.navigate("branches_screen/${yearID}")
                            }
                        } else {
                            LoadingScreen()
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
                            ) { subjectID ->
                                navController.navigate("content_screen/${subjectID}")
                            }
                        } else {
                            LoadingScreen()
                        }
                    }
                    composable(
                        "content_screen" + "/{subjectID}",
                        arguments = listOf(
                            navArgument("subjectID") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                        val contentViewModel : ContentViewModel = hiltViewModel()
                        val content: State<List<Content>> = contentViewModel.contents.collectAsState()
                        if (content.value != emptyList<BranchItem>()) {
                            ContentScreen(
                                content.value,
                                navController
                            )
                        } else {
                            LoadingScreen()
                        }
                    }
//                    composable("sign_in") {
//                        BackHandler(
//                            onBack = {
//                                finish()
//                            }
//                        )
//                        LoginScreen { navController.navigate("main_screen") }
//                    }
//                    composable("profile") {
//                        val sharedPreferences = remember {
//                            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
//                        }
//                        ProfileScreen(
//                            userData = googleAuthUiClient.getSignedInUser(),
//                            onSignOut = {
//                                lifecycleScope.launch {
//                                    googleAuthUiClient.signOut()
//                                    navController.navigate("walk_through_screen")
//                                }
//                                val editor = sharedPreferences.edit()
//                                editor.clear()
//                                editor.apply()
//                            }
//                        )
//                    }
                    composable("settings") {
                        SettingsScreen (){
                            navController.popBackStack()
                        }
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
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_cats))
        com.airbnb.lottie.compose.LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxSize()
                .size(400.dp)
        )
        Text (
            text = "Loading..."
        )
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
fun ErrorPage(
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.error_cat))
        com.airbnb.lottie.compose.LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxSize()
                .size(400.dp)
        )
        Text (
            text = "Something Went Wrong..."
        )
        Button(onClick = {
            onClick()
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
        ) {
            Text(
                text = "RETRY",
            )
        }
    }
}