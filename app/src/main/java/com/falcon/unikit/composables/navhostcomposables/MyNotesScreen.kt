package com.falcon.unikit.composables.navhostcomposables

import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.falcon.unikit.composables.general.LoadingScreen
import com.falcon.unikit.composables.general.LottieAnimation
import com.falcon.unikit.MainActivity
import com.falcon.unikit.MyNoteItem
import com.falcon.unikit.R
import com.falcon.unikit.Utils
import com.falcon.unikit.viewmodels.AuthViewModel

@Composable
fun MainActivity.MyNotesScreen(
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Notes") },
                contentColor = MaterialTheme.colors.onSurface,
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
        val authViewModel: AuthViewModel = hiltViewModel()
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

@Composable
fun UserNotFound(
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(animationID = R.raw.error)
        androidx.compose.material3.Text(
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
            androidx.compose.material3.Text(
                text = "SIGN IN",
            )
        }

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
                androidx.compose.material3.Text(
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
                androidx.compose.material3.Text(
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
                androidx.compose.material3.Text(
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
            androidx.compose.material3.Text(
                text = "College: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            androidx.compose.material3.Text(
                text = "course",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            androidx.compose.material3.Text(
                text = "branch",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            androidx.compose.material3.Text(
                text = "year",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            androidx.compose.material3.Text(
                text = "subject",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
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