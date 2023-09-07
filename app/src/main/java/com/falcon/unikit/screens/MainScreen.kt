package com.falcon.unikit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

@Composable
fun ChooseYearScreen(
    yearList: State<List<YearItem>>,
    drawerState: DrawerState,
    navigateToBranchScreen: (yearID: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    Column {
        TopBar(scope, drawerState)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceAround) {
                val reversedList = yearList.value.reversed()
               items(reversedList){
                   YearItemComposable(year = it, navigateToBranchScreen)
               }
            }
    }
}

@Composable
fun YearItemComposable(year: YearItem, navigateToBranchScreen: (yearID: String) -> Unit) {
    var backGroundImage = R.drawable.year4
    if (year.numofYear != null) {
        backGroundImage = getImageFromYear(year.numofYear)
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .padding(4.dp)
            .size(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .paint(
                painterResource(id = backGroundImage),
                contentScale = ContentScale.Crop
            )
            .clickable {
                navigateToBranchScreen(
                    year.yearID?: "Error: yearID is NULL"
                )
            },
    ) {
        Text(
            text =  year.numofYear?: "Error: yearName is NULL",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .padding(8.dp, 20.dp),
            style = MaterialTheme.typography.body1
        )
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
private fun TopBar(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            scope.launch {
                drawerState.open()
            }
        }) {
            Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
        }
        Text(
            text = "UniKit",
            modifier = Modifier,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
fun DrawerContent(navController: NavHostController) {
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
        Spacer(modifier = Modifier.height(2.dp))
        NavDrawerContent("My Notes", R.drawable.baseline_sticky_note_2_24) {
            navController.navigate("my_notes")
        }
        Spacer(modifier = Modifier.height(2.dp))
        NavDrawerContent("Settings", R.drawable.baseline_settings_24) {
            navController.navigate("settings")
        }
    }

}
