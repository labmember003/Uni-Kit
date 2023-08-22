package com.falcon.unikit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.viewmodels.BranchViewModel
import com.falcon.unikit.viewmodels.SubjectViewModel
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Test() {
    val navController = rememberNavController()
    val list = listOf(
        BranchItem("cat","cat"),
        BranchItem("dog","dog"),
        BranchItem("rat","rat"),
        BranchItem("mat","mat")
    )
    BranchesScreen(list, navController)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BranchesScreen(branchList: List<BranchItem>, navController: NavHostController) {
    val list = branchList.map { branch ->
        branch.branchName
    }
    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HeadingSummarizedPage()
        TabRow(
            selectedTabIndex = pageState.currentPage,
            modifier = Modifier
        ) {
            list.forEachIndexed { index, _ ->
                // on below line we are creating a tab.
                Tab(
                    text = {
                        Text(
                            list[index],
                            // on below line we are specifying the text color
                            // for the text in that tab
                            color = if (pageState.currentPage == index) Color(R.color.custom_color_tab_bar) else Color.Black
                        )
                    },
                    // on below line we are specifying
                    // the tab which is selected.
                    selected = pageState.currentPage == index,
                    // on below line we are specifying the
                    // on click for the tab which is selected.
                    onClick = {
                        // on below line we are specifying the scope.
                        scope.launch {
                            pageState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            pageCount = branchList.size,
            state = pageState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            pageContent = { pageNumber ->
                SubjectList(branchList[pageNumber])
            }
        )
    }
}

@Composable
fun SubjectList(branch: BranchItem) {
    val subjectViewModel : SubjectViewModel = viewModel()
    val subjects: State<List<SubjectItem>> = subjectViewModel.subjects.collectAsState()
    if (subjects.value != emptyList<SubjectItem>()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = branch.branchName)
            LazyColumn(content = {
                items(subjects.value) { subject ->
                    SubjectItemRow(subject)
                }
            })
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun SubjectItemRow(subjectItem: SubjectItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(data = subjectItem.imageURL),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = subjectItem.subjectName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HeadingSummarizedPage() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Unikit",
            style = MaterialTheme.typography.subtitle1,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {

        }) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
        IconButton(onClick = {

        }) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    }
}