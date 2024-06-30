package com.falcon.unikit.composables.navhostcomposables

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.LoadingScreen
import com.falcon.unikit.NotesCard
import com.falcon.unikit.R
import com.falcon.unikit.api.Content
import com.falcon.unikit.screens.getIcon
import com.falcon.unikit.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DisplayFileViaDeepLinkScreen(
    entry: NavBackStackEntry,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val contentId = entry.arguments?.getString("contentId")
    val authViewModel: AuthViewModel = hiltViewModel()
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
            Text(
                text = "Content",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
            )
        }
        NotesCard(contentItem = content, icon = icon,  navController = navController)
    }
}