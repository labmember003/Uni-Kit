package com.falcon.unikit.composables.navhostcomposables

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.NotesCard
import com.falcon.unikit.R
import com.falcon.unikit.composables.general.LoadingScreen
import com.falcon.unikit.api.Content
import com.falcon.unikit.screens.AddNotesFAB
import com.falcon.unikit.screens.BottomSheetContent
import com.falcon.unikit.screens.ComingSoonScreen
import com.falcon.unikit.viewmodels.ContentViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppContentScreen(
    entry: NavBackStackEntry,
    navController: NavHostController
) {
    val subjectID = entry.arguments?.getString("subjectID")
    val subjectName = entry.arguments?.getString("subjectName")
    val recompose = { pageNumber: String ->
        navController.popBackStack()
        navController.navigate("content_screen/${subjectID}/${subjectName}/${pageNumber}")
    }
    val contentViewModel: ContentViewModel = hiltViewModel()
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
                .padding(16.dp, 16.dp, 16.dp, 0.dp)
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
                    NotesList(specificContent, navController, com.falcon.unikit.screens.getIcon(list[pageNumber], true), modalSheetState)
                }
            )

            TabRow(
                selectedTabIndex = pageState.currentPage,
                indicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
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
                                    painter = painterResource(id = com.falcon.unikit.screens.getIcon(list[index], pageState.currentPage == index)),
                                    contentDescription = "Icon",
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                                if (pageState.currentPage == index) {
                                    Text(
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
fun ContentScreenTitle() {
    androidx.compose.material3.Text(
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