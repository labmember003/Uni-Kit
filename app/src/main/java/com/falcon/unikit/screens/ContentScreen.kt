package com.falcon.unikit.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.HeadingSummarizedPage
import com.falcon.unikit.LottieAnimation
import com.falcon.unikit.R
import com.falcon.unikit.TextWithBorder
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
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContentScreen(content: List<Content>, navController: NavHostController) {
    val list = listOf("Notes", "Books", "Papers", "Playlists", "Syllabus")
    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val currentType = remember {
        mutableStateOf("Type")
    }
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheetContent(modalSheetState, currentType)
        }
    ) {
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

                    currentType.value = list[pageNumber]
//                    TODO(TRIGGER RECOMPOSITION OF BOTTOM SHEET)

                    ContentList(specificContent, navController, getIcon(list[pageNumber], true), modalSheetState)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentList(
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
            LazyColumn(content = {
                val sortedItems = content.sortedByDescending { it.likeCount }
                items(sortedItems) { content ->
                    ContentItemRow(content, icon)
                }
            })
        }
    }
    AddNotesFAB(modalSheetState)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNotesFAB(modalSheetState: ModalBottomSheetState) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                scope.launch {
                    modalSheetState.show()
                }
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

private suspend fun downloadPdf(
    downloadLink: String,
    fileName: String,
    statusCallback: (String) -> Unit,
    context: Context
) {
    withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadLink)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream

                // Use a relative path within the private storage
                val relativeFilePath = "pdfs/$fileName"
                val file = File(getAppStorageDirectory(context), relativeFilePath)

                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead)
                }

                fileOutputStream.close()
                inputStream.close()

                // Notify the UI about the download status
                statusCallback("PDF downloaded successfully. Path: $relativeFilePath")

                // Display a Toast indicating successful download
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF downloaded successfully.", Toast.LENGTH_SHORT).show()
                }
            } else {
                statusCallback("Failed to download PDF. Response code: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            statusCallback("Error: ${e.message}")
        }
    }
}

private suspend fun downloadPdf2(
    downloadLink: String,
    fileName: String,
    statusCallback: (String) -> Unit,
    context: Context
) {
    withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadLink)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                val file = File(getAppStorageDirectory(context), fileName)
                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead)
                }

                fileOutputStream.close()
                inputStream.close()

                // Notify the UI about the download status

                Toast.makeText(context, "PDF downloaded successfully.", Toast.LENGTH_SHORT).show()
//                statusCallback("PDF downloaded successfully. Path: ${file.absolutePath}")
//                Toast.makeText(context, file.absolutePath, Toast.LENGTH_SHORT).show()
//                if (file.exists()) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "trueeeeee", Toast.LENGTH_SHORT).show()
//                    }
//                }
            } else {
                statusCallback("Failed to download PDF. Response code: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            statusCallback("Error: ${e.message}")
        }
    }
}
suspend fun downloadAndStorePdf(pdfUrl: String, context: Context, _id: String) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(pdfUrl)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val pdfFileName = "$_id.pdf" // Change this to the desired file name
                val pdfFile = File(getAppStorageDirectory(context), pdfFileName)
                val inputStream = response.body?.byteStream()
                val outputStream = FileOutputStream(pdfFile)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, pdfFile.absolutePath, Toast.LENGTH_SHORT).show()
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
    val file = File(context.filesDir, "pdfs") // Change "pdfs" to the desired directory name
    file.mkdir()
    return File(context.filesDir, "pdfs") // Change "pdfs" to the desired directory name
}

@Composable
fun ContentItemRow(contentItem: Content, icon: Int) {
    val context = LocalContext.current
    var downloadStatus = remember { mutableStateOf<String?>(null) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (isPdfFileInStorage(contentItem._id.toString(), context)) {
//                    openFile(contentItem._id.toString(), context, getAppStorageDirectory(context))
                    Log.i("filefilefile", "openFIle")
                } else {
                    Log.i("filefilefile", "downloadFile")
                    Log.i("filefilefile", contentItem.pdfFile.toString())
                    CoroutineScope(Dispatchers.IO).launch {
//                        downloadAndStorePdf(
//                            contentItem.pdfFile.toString(),
//                            context,
//                            contentItem._id.toString()
//                        )
                        downloadPdf(
                            contentItem.pdfFile.toString(),
                            contentItem._id.toString(),
                            { status ->
                                downloadStatus.value = status
                            },
                            context
                        )
                    }
//                    Toast.makeText(context, isPdfFileInStorage(contentItem._id.toString(), context).toString(), Toast.LENGTH_SHORT).show()
//                    openFile(contentItem._id.toString(), context, getAppStorageDirectory(context))
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

fun openFile(fileName: String, context: Context, appStorageDirectory: File) {
    val file = File(appStorageDirectory, fileName)
    if (file.exists()) {
        val pdfUri = Uri.fromFile(file)
        val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
            data = pdfUri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        ContextCompat.startActivity(context, pdfIntent, null)
    } else {
        Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
    }
}

fun isPdfFileInStorage(fileName: String, context: Context): Boolean {
    val pdfFile = File(getAppStorageDirectory(context), fileName)
    return pdfFile.exists()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(modalSheetState: ModalBottomSheetState, currentType: MutableState<String>) {
    val scope = rememberCoroutineScope()
    val uploaded = remember {
        mutableStateOf(true)
//        TODO(make it false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp, 24.dp, 16.dp, 16.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material3.Text(
                text = "Upload Notes",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 20.sp
            )
            androidx.compose.material.Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .clickable {
                        scope.launch { modalSheetState.hide() }
                    }
            )
        }
        TextWithBorder(headingValue = "Subject", descriptionValue = "College")
        TextWithBorder(headingValue = "Type", descriptionValue = currentType.value)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    uploadFile()
                }
        ) {
            Column {
                UploadIcon()
                Text(text = "(Upload Your PDF)")
                if (uploaded.value) {
                    SubmitButton()
                }
            }
        }
    }

}

@Composable
fun SubmitButton() {
    Button(
        onClick = {
            submitPDF()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
    ) {
        androidx.compose.material3.Text(
            text = "SUBMIT",
        )
    }
}

fun submitPDF() {
    TODO("Not yet implemented")
}

fun uploadFile() {
    TODO("Not yet implemented")
}

@Composable
fun UploadIcon() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.upload_pdf))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(100.dp)
    )
}
