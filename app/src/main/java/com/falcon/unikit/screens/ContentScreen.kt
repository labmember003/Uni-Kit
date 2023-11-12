package com.falcon.unikit.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.HeadingSummarizedPage
import com.falcon.unikit.LottieAnimation
import com.falcon.unikit.R
import com.falcon.unikit.api.Content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentScreen(content: List<Content>, navController: NavHostController) {
    val list = listOf("Notes", "Books", "Papers", "Playlists", "Syllabus")
    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HeadingSummarizedPage()

        HorizontalPager(
            pageCount = list.size,
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            pageContent = { pageNumber ->
//              0 -> notes = content[0]
//                content[pageNumber], navController
                Log.d("Pager", "Current Page: ${pageState.currentPage}, Requested Page: $pageNumber")
//                val icon = getIcon(content[pageNumber].contentType)
                val specificContent = content.filter {
                    it.contentType == list[pageNumber]
                }
                ContentList(specificContent, navController, getIcon(list[pageNumber], true))
            }
        )
        TabRow(
            selectedTabIndex = pageState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            list.forEachIndexed { index, _ ->
                // on below line we are creating a tab.
                Tab(
                    modifier = Modifier.fillMaxWidth(),
                    selectedContentColor = Color(R.color.teal_200),
                    unselectedContentColor = Color(R.color.teal_200),
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = getIcon(list[index], pageState.currentPage == index)),
                                contentDescription = "Icon",
                                modifier = Modifier
                                    .size(20.dp)
                            )
                            Text(
                                list[index],
                                fontSize = 13.sp,
                                // on below line we are specifying the text color
                                // for the text in that tab
                                color = if (pageState.currentPage == index) Color(R.color.teal_200) else Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
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

fun getIcon(contentName: String, selected: Boolean): Int {
    if (selected) {
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
            else -> return R.drawable.ic_goole
        }
    }
    else {
        when (contentName) {
            "Notes" -> {
                return R.drawable.notes_unseleted
            }
            "Books" -> {
                return R.drawable.book_unseleted
            }
            "Papers" -> {
                return R.drawable.exam_unseleted
            }
            "Playlists" -> {
                return R.drawable.playlisticon_unseleted
            }
            "Syllabus" -> {
                return R.drawable.syllabusicon_unseleted
            }
            else -> return R.drawable.error
        }
    }
}

@Composable
fun ContentList(content: List<Content>, navController: NavHostController, icon: Int) {
    if (content.isEmpty()) {
        ComingSoonScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(content = {
                val sortedItems = content.sortedByDescending { it.likeCount }
                items(sortedItems) { content ->
                    ContentItemRow(content, icon)
                }
            })
        }
    }
    AddNotesFAB()
}

@Composable
fun AddNotesFAB() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {

            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp),
            shape = RoundedCornerShape(percent = 30),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colors.primary,
            )
        }
    }
}

@Composable
fun ComingSoonScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(animationID = R.raw.coming_soon_cat)
        androidx.compose.material3.Text(
            text = "No Notes"
        )
    }
}
suspend fun downloadAndStorePdf(pdfUrl: String, context: Context, name: String) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(pdfUrl)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val pdfFileName = "$name.pdf" // Change this to the desired file name
                val pdfFile = File(getAppStorageDirectory(context), pdfFileName)
                val inputStream = response.body?.byteStream()
                val outputStream = FileOutputStream(pdfFile)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                Log.d("PdfDownload", "PDF downloaded and stored at ${pdfFile.absolutePath}")
            } else {
                Log.e("PdfDownload", "Download failed")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PdfDownload", "Error during download: ${e.message}")
        }
    }
}

private fun getAppStorageDirectory(context: Context): File {
    return File(context.filesDir, "pdfs") // Change "pdfs" to the desired directory name
}

@Composable
fun ContentItemRow(contentItem: Content, icon: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (isPdfFileInStorage(contentItem._id.toString(), context)) {
                    openFile(contentItem._id.toString(), context)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadAndStorePdf(
                            contentItem.downloadURL.toString(),
                            context,
                            contentItem._id.toString()
                        )
                    }
                    openFile(contentItem._id.toString(), context)
                }

//                navController.navigate("content_screen/${subjectItem.subjectID}")
//                Todo(download and view file)
//                  download(contentItem.downloadURL)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = contentItem.contentName.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
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
                text = contentItem.likeCount.toString(),
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
                text = contentItem.dislikeCount.toString()
            )
        }

    }
}

fun openFile(toString: String, context: Context) {
    TODO("Not yet implemented")
}

fun isPdfFileInStorage(fileName: String, context: Context): Boolean {
    val pdfFile = File(getAppStorageDirectory(context), fileName)
    return pdfFile.exists()
}
