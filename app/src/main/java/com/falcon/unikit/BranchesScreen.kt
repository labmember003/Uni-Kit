package com.falcon.unikit

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.models.item.SubjectItem
import com.falcon.unikit.viewmodels.SubjectViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BranchesScreen(
    branchList: List<BranchItem>,
    navigateToContentScreen: (String, String) -> Unit
) {
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
                            list[index]?: "ERROR: branchName is NULL",
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
                SubjectList(branchList[pageNumber], navigateToContentScreen)
            }
        )
    }
}

@Composable
fun SubjectList(
    branch: BranchItem,
    navigateToContentScreen: (String, String) -> Unit
) {
    val subjectViewModel : SubjectViewModel = hiltViewModel()
//    val subjectss = emptyList<State<List<SubjectItem>>>()
    val subjects by rememberUpdatedState(subjectViewModel.subjects.collectAsState())
    val isLoading = subjects.value.isEmpty()
    LaunchedEffect(branch.branchID) {
        if (isLoading) {
            Log.i("branchIDDDD", branch.branchID.toString())
            subjectViewModel.getSubjects(branch.branchID.toString())
        }
    }
    if (isLoading) {
        LoadingScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(content = {
                items(subjects.value) { subject ->
                    SubjectItemRow(subject, navigateToContentScreen)
                }
            })
        }
    }
}

@Composable
fun SubjectItemRow(
    subjectItem: SubjectItem,
    navigateToContentScreen: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .clickable {
                navigateToContentScreen(subjectItem.subjectID ?: "ERROR: Subject Id is NULL", subjectItem.subjectName ?: "ERROR: Subject Name is NULL")
                Log.i("subjectID", subjectItem.subjectID.toString())
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.i("diwali", subjectItem.subjectID.toString())
        if(subjectItem.imageURL != null) {
            AsyncImage(
                model = subjectItem.imageURL,
                contentDescription = "Subject picture",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = rememberImagePainter(null),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = subjectItem.subjectName ?: "ERROR: subjectName is NULL",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HeadingSummarizedPage() {
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