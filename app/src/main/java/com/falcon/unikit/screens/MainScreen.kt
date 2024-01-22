package com.falcon.unikit.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.NavDrawerContent
import com.falcon.unikit.R
import com.falcon.unikit.models.item.YearItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(yearList: State<List<YearItem>>, navController: NavHostController, navigateToBranchScreen: (yearID: String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        ModalDrawerSample(yearList, navController, navigateToBranchScreen)
    }

}

@Composable
fun ModalDrawerSample(
    yearList: State<List<YearItem>>,
    navController: NavHostController,
    navigateToBranchScreen: (yearID: String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController)
        },
        content = {
            ChooseYearScreen(yearList, drawerState, navigateToBranchScreen)
        }
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _ChooseYearScreen() {
    val yearlist = remember {
        mutableStateOf(
            listOf(
                YearItem("123", "First"),
                YearItem("123", "Second"),
                YearItem("123", "Third"),
                YearItem("123", "Fourth")
            )
        )
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ChooseYearScreen(yearlist, drawerState) {

    }
}
@Composable
fun ChooseYearScreen(
    yearList: State<List<YearItem>>,
    drawerState: DrawerState,
    navigateToBranchScreen: (yearID: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MainScreenHeader(scope, drawerState)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Your Curriculam",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.8f)
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = colorResource(id = R.color.view_all_text_blue),
                fontFamily = FontFamily(Font(R.font.nunito_light_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.2f)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceAround) {
            val reversedList = yearList.value
            items(reversedList){
                YearCard(year = it, navigateToBranchScreen)
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Courses For You",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.8f)

            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = colorResource(id = R.color.view_all_text_blue),
                fontFamily = FontFamily(Font(R.font.nunito_light_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.2f)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceAround) {
            val list = listOf<PaidCourseOrEvent>(
                PaidCourseOrEvent(R.drawable.android, "Android Development By Coding Ninjas 4.8 ⭐"),
                PaidCourseOrEvent(R.drawable.fullstack, "Full Stack Web Development By Coding Ninjas 4.8 ⭐"),
                PaidCourseOrEvent(R.drawable.data_analytics, "Data Analytics By Coding Ninjas 4.8 ⭐"),
            )
            items(list){ course ->
                CourseCard(course)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Upcoming Hackathons & Meetups Near You",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.8f)
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = colorResource(id = R.color.view_all_text_blue),
                fontFamily = FontFamily(Font(R.font.nunito_light_1)),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(0.2f)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceAround) {
            val list = listOf<PaidCourseOrEvent>(
                PaidCourseOrEvent(R.drawable.hackathon, "Hackathon\n" +
                        "Venue : DTU\n" +
                        "Date : 11-12th Feb"),
                PaidCourseOrEvent(R.drawable.meeting, "Web3 Meetup\n" +
                        "Venue : Microsoft Noida\n" +
                        "Date : 11-12th Feb"),
            )
            items(list){ course ->
                EventCard(course)
            }
        }
    }
}

@Composable
private fun MainScreenHeader(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Unikit",
            fontSize = 32.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Image(
            painter = painterResource(id = R.drawable.menu_icon),
            contentDescription = "Menu Icon",
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    scope.launch {
                        drawerState.open()
                    }
                }
        )
    }
}

@Composable
fun CourseCard(course: PaidCourseOrEvent) {
    var backGroundImage = R.drawable.year4
    if (course.image != null) {
        backGroundImage = course.image
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(140.dp)
    ) {
        Image(
            painter = painterResource(id = backGroundImage),
            contentDescription = "Redeem Brand Icon",
            modifier = Modifier
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = course.description.toString(),
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun EventCard(event: PaidCourseOrEvent) {
    var backGroundImage = R.drawable.year4
    if (event.image != null) {
        backGroundImage = event.image
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(140.dp)
    ) {
        Image(
            painter = painterResource(id = backGroundImage),
            contentDescription = "Redeem Brand Icon",
            modifier = Modifier
                .size(123.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = event.description.toString(),
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _YearCard() {
    YearCard(
        YearItem("", "First"),
        {}
    )
}
@Composable
fun YearCard(year: YearItem, navigateToBranchScreen: (yearID: String) -> Unit) {
    var backGroundImage = R.drawable.year4
    if (year.numofYear != null) {
        backGroundImage = getImageFromYear(year.numofYear)
    }
    Card(
        modifier = Modifier
            .padding(15.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(48.dp))
            .clickable {
                navigateToBranchScreen(
                    year.yearID ?: "Error: yearID is NULL"
                )
            }
            .size(123.dp),
        backgroundColor = Color.White
    ) {
        Image(
            painter = painterResource(id = backGroundImage),
            contentDescription = "Redeem Brand Icon",
            modifier = Modifier,
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = year.numofYear?: "Error: yearName is NULL",
                fontSize = 10.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            )
        }

    }
}

fun getImageFromYear(yearName: String): Int {
    when (yearName) {
        "First" -> {
            return R.drawable.year1
        }
        "Second" -> {
            return R.drawable.year2
        }
        "Third" -> {
            return R.drawable.year3
        }
        "Fourth" -> {
            return R.drawable.year4
        }
    }
    return R.drawable.graduation
}

@Composable
fun DrawerContent(navController: NavHostController) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxHeight()
        ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.productivity))
        com.airbnb.lottie.compose.LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp)
        )
        NavDrawerContent("Profile", R.drawable.baseline_person_24) {
            navController.navigate("profile")
        }
//        Spacer(modifier = Modifier.height(2.dp))
//        NavDrawerContent("My Notes", R.drawable.baseline_sticky_note_2_24) {
//            navController.navigate("my_notes")
//        }
        Spacer(modifier = Modifier.height(2.dp))
        NavDrawerContent("Settings", R.drawable.baseline_settings_24) {
            navController.navigate("settings")
        }
//        NavDrawerContent("Community", R.drawable.baseline_people_alt_24) {
//            navController.navigate("community")
//        }
        NavDrawerContent("Redeem", R.drawable.baseline_redeem_24) {
            navController.navigate("redeem")
        }
        NavDrawerContent("Refer & Earn", R.drawable.baseline_redeem_24) {
            navController.navigate("refer_and_earn")
        }
//        NavDrawerContent("Book To Story", R.drawable.baseline_menu_book_24) {
//            openUrlInBrowser(context = context, url = "https://mediafiles.botpress.cloud/865dac6b-a4c7-49f9-91e9-4e45c76ee3cc/webchat/bot.html")
//        }
    }

}

fun openUrlInBrowser(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}