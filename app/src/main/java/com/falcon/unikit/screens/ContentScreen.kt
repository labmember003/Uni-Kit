package com.falcon.unikit.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.falcon.unikit.HeadingSummarizedPage
import com.falcon.unikit.R
import com.falcon.unikit.api.Content
import com.falcon.unikit.api.Item
import com.falcon.unikit.viewmodels.ItemViewModel
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
    val list = content.map { content ->
        content.contentName
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
            pageCount = content.size,
            state = pageState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            pageContent = { pageNumber ->
//              0 -> notes = content[0]
//                content[pageNumber], navController
                val icon = getIcon(content[pageNumber].contentName)
                ContentList(content[pageNumber], navController, icon)
            }
        )
    }
}

fun getIcon(contentName: String): Int {
    when (contentName) {
        "notes" -> {
            return R.drawable.ic_goole
        }
        "books" -> {
            return R.drawable.ic_goole
        }
        "papers" -> {
            return R.drawable.ic_goole
        }
        "playlist" -> {
            return R.drawable.ic_goole
        }
        "syllabus" -> {
            return R.drawable.ic_goole
        }
        else -> return R.drawable.ic_goole
    }
}

@Composable
fun ContentList(content: Content, navController: NavHostController, icon: Int) {
    val contentID = content.contentId

    val itemViewModel : ItemViewModel = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        itemViewModel.getItem(contentID)
    }
    val items: State<List<Item>> = itemViewModel.items.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(content = {
            val sortedItems = items.value.sortedByDescending { it.likeCount }
            items(sortedItems) { content ->
                ContentItemRow(content, icon)
            }
        })
    }
}

suspend fun downloadAndStorePdf(pdfUrl: String, context: Context, contentItem: Item) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(pdfUrl)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val pdfFileName = contentItem.itemId + ".pdf" // Change this to the desired file name
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

@Preview(showBackground = true)
@Composable()
fun test() {
    ContentItemRow(Item("itemNAME", "f", "",0, 0), R.drawable.ic_goole)
}
@Composable
fun ContentItemRow(contentItem: Item, icon: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (isPdfFileInStorage(contentItem.itemId.toString(), context)) {
                    openFile(contentItem.itemId.toString(), context)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadAndStorePdf(contentItem.downloadURL, context, contentItem)
                    }
                    openFile(contentItem.itemId.toString(), context)
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
            text = contentItem.itemName,
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
