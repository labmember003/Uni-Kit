package com.falcon.unikit.composables.navhostcomposables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.falcon.unikit.BranchesScreen
import com.falcon.unikit.composables.general.LoadingScreen
import com.falcon.unikit.models.item.BranchItem
import com.falcon.unikit.viewmodels.BranchViewModel

@Composable
fun SelectYourBranchScreen(navController: NavHostController) {
    val branchViewModel: BranchViewModel = hiltViewModel()
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