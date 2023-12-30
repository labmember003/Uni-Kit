package com.falcon.unikit.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.HeadingSummarizedPage
import com.falcon.unikit.LottieAnimation
import com.falcon.unikit.R
import com.falcon.unikit.TextWithBorder
import com.falcon.unikit.Utils
import com.falcon.unikit.Utils.PDF_PASSWORD
import com.falcon.unikit.api.Content
import com.falcon.unikit.encode
import com.falcon.unikit.uploadfile.FileUploadViewModel
import com.falcon.unikit.uploadfile.UploadFileBody
import com.falcon.unikit.viewmodels.AuthViewModel
import com.falcon.unikit.viewmodels.ItemViewModel
import com.itextpdf.text.pdf.PdfWriter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Random
import kotlin.math.sqrt
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import java.io.FileOutputStream
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContentScreen(
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
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeadingSummarizedPage()
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
                val sortedItems = content.sortedByDescending { it.like?.size }
                items(sortedItems) { content ->
                    Log.i("happy sex", content.toString())
                    ContentItemRow(content, icon, navController)
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItemRow(contentItem: Content, icon: Int, navController: NavHostController) {
//    Log.i("happy sex", contentItem.contentID.toString())
    val context = LocalContext.current
    val expanded = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val itemViewModel : ItemViewModel = hiltViewModel()
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val token = sharedPreferences.getString(Utils.JWT_TOKEN, "").toString()
    val liked = remember {
        mutableStateOf(false)
    }
    val disliked = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        if (contentItem.like?.contains(token) == true) {
            liked.value = true
        }
        if (contentItem.dislike?.contains(token) == true) {
            disliked.value = true
        }
    }
    val likeIcon = if (liked.value) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp
    val dislikeIcon = if (disliked.value)  Icons.Default.ThumbDown else Icons.Outlined.ThumbDown

    val numLikes = remember {
        mutableStateOf(contentItem.like?.size ?: 0)
    }
    val numDisLikes = remember {
        mutableStateOf(contentItem.dislike?.size ?: 0)
    }
    val likeFunction = {
        if (liked.value && disliked.value) {
            disliked.value = false
        }
        if (disliked.value) {
            numDisLikes.value = numDisLikes.value - 1
        }
        disliked.value = false
        numLikes.value = numLikes.value.plus(if (liked.value) -1 else 1)
        liked.value = !liked.value
    }
    val dislikeFunction = {
        if (liked.value && disliked.value) {
            liked.value = false
        }
        if (liked.value) {
            numLikes.value = numLikes.value - 1
        }
        liked.value = false
        numDisLikes.value = numDisLikes.value.plus(if (disliked.value) -1 else 1)
        disliked.value = !disliked.value
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(0.5.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded.value = !expanded.value
            }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
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
                        scope.launch {
                            itemViewModel.likeButtonPressed(
                                contentItem.contentID.toString(),
                                token
                            )
                        }
                        likeFunction()
                    }
                ) {
                    Icon(
                        imageVector = likeIcon, // Use the thumbs-up icon from Icons.Default
                        contentDescription = "Thumbs Up",
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
                Text(
                    text = numLikes.value.toString()
                )
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        scope.launch {
                            itemViewModel.dislikeButtonPressed(
                                contentItem.contentID.toString(),
                                token.toString()
                            )
                        }
                        dislikeFunction()
                    }
                ) {
                    Icon(
                        imageVector = dislikeIcon, // Use the thumbs-up icon from Icons.Default
                        contentDescription = "Thumbs Up",
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
                Text(
                    text = numDisLikes.value.toString()
                )
            }
        }
        val activity = LocalContext.current as? androidx.activity.ComponentActivity
        val authViewModel : AuthViewModel = hiltViewModel()
        if (expanded.value) {
            val fileName = contentItem.contentID + ".pdf"
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            val pdfUri = Uri.fromFile(file)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(8.dp) ,
                    onClick = {
                        val fileName = contentItem.contentID + ".pdf"
                        Log.i("asdfvfdfefe", fileName)
                        val file = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            fileName
                        )
                        scope.launch {
                            authViewModel.getDownloadableURL(contentItem.contentID.toString())
                            authViewModel.downloadableURL.collect { downloadableURL ->
                            if (file.exists()) {
                                val uri: String? = Uri.fromFile(file).toString()
                                val encoded = uri?.let { encode(it) }
                                navController.navigate("open_file/${encoded}")
                                Toast.makeText(context, "exosts", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please Wait Downloading is being starting", Toast.LENGTH_SHORT).show()
                                val notificationId = Random().nextInt()
                                downloadPdfNotifination (
                                    context,
                                    downloadableURL.githuburl.toString(),
                                    notificationId,
                                    scope,
                                    activity,
                                    contentItem
                                )
                                // UPAR WAALE CODE SE FILE SAVE HOGI WITH NAME : contentItem.contentID + "ENC" + ".pdf"
                                // NICHE WAALE CODE SE NEW FILE BNEGI WITH NAME : contentItem.contentID + ".pdf"
                                // AUR FIR ENC WAALI FILE KO DELETE KR DENGE

                                Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show()
                            }
                            }
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.download),
                        contentDescription = ""
                    )
                }
                IconButton(
                    modifier = Modifier
                        .padding(8.dp) ,
                    onClick = {
                        val text = "https://uni-kit-api.vercel.app/" + contentItem.contentID
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, text)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                ) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = "Download",
//                        tint = Color.Black, //,
//                        modifier = Modifier.padding(8.dp) // Adjust padding as needed
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = ""
                    )
                }
                val reportDialogueVisibility = remember {
                    mutableStateOf(false)
                }
                IconButton(
                    modifier = Modifier
                        .padding(8.dp) ,
                    onClick = {
                        reportDialogueVisibility.value = !reportDialogueVisibility.value
                    }
                ) {
//                    Icon(
//                        imageVector = Icons.Default.Report,
//                        contentDescription = "Download",
//                        tint = Color.Black, //,
//                        modifier = Modifier.padding(8.dp) // Adjust padding as needed
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.warning),
                        contentDescription = ""
                    )
                }
                if (reportDialogueVisibility.value) {
                    AlertExample { parameter ->
                        scope.launch {
                            itemViewModel.reportContent(token.toString(), contentItem.contentID.toString(), parameter)
                        }
                    }
                }
            }
        }
    }
}
fun openPdfInExternalViewer(context: android.content.Context, fileName: String) {
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val pdfUri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        context.startActivity(intent)
    } else {
        // Handle the case when the file doesn't exist
        // You might want to show an error message to the user
    }
}

private fun openFile(
    context: Context,
    file: File,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    )

    launcher.launch("application/pdf")
}

@Composable
fun EditTextWithBorder(fileName: String, omChangeValue: (String) -> Unit) {
    var mSelectedText by remember(fileName) { mutableStateOf(fileName) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    Column(
        Modifier
            .padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = false,
            value = mSelectedText,
            onValueChange = {
                mSelectedText = it
                omChangeValue(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            label = {
                Text(
                    "Name"
                )
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpComp() {
    val modifier = Modifier
        .size(90.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OtpCell(modifier, " ")
        Spacer(modifier = Modifier.size(10.dp))
        OtpCell(modifier, " ")
        Spacer(modifier = Modifier.size(10.dp))
        OtpCell(modifier, " ")
        Spacer(modifier = Modifier.size(10.dp))
        OtpCell(modifier, " ")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpCell(
    modifier: Modifier,
    value: String,
    isCursorVisible: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
            }
        }
    }

    Card(
        modifier = modifier
            .padding(15.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
            .fillMaxSize()

    ) {
        var textValue2 = remember() { mutableStateOf("") }
//        TextField(
//            value = textValue,
//            onValueChange = { newValue ->
//                textValue = newValue
//            },
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = textValue2.value,
                onValueChange = {
                    textValue2.value = it
                },
                modifier = Modifier.padding(6.dp),
                visualTransformation = VisualTransformation.None,
            )
        }

    }
}

@SuppressLint("Range")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    modalSheetState: ModalBottomSheetState,
    currentType: MutableState<String>,
    currentSubject: MutableState<String?>,
    content: List<Content>,
    recompose: (String) -> Unit,
    currentPage: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val startUpload = remember (modalSheetState.currentValue) {
        mutableStateOf(false)
    }
    val isUploading = remember (modalSheetState.currentValue) {
        mutableStateOf(false)
    }
    val fileName = remember() {
        mutableStateOf("FileName")
    }
    val pdfURI: MutableState<Uri?> = remember {
        mutableStateOf(null)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        Log.i("merimausikhayeande", fileName.value)
        pdfURI.value = uri
        startUpload.value = true
        val contentResolver = context.contentResolver  // Assuming you have access to the context
        val cursor: Cursor? = uri?.let { contentResolver.query(it, null, null, null, null) }
        Log.i("merimausikhayeande", fileName.value)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Toast.makeText(context, displayName, Toast.LENGTH_SHORT).show()
                Log.i("merimausikhayeande", fileName.value)
                if (fileName.value == "FileName") {
                    fileName.value = displayName
                }
                Log.i("merimausikhayeande2", fileName.value)
                // Now displayName contains the name of the selected PDF file
                // You can use it as needed
            }
        }
    }
    Toast.makeText(context,fileName.value.toString() , Toast.LENGTH_SHORT).show()
    
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
        TextWithBorder(headingValue = "Subject", descriptionValue = currentSubject.value.toString())
        TextWithBorder(headingValue = "Type", descriptionValue = currentType.value)
        val finalFileName = remember(fileName.value) {
            mutableStateOf(fileName.value)
        }
        if (startUpload.value) {
            EditTextWithBorder(fileName = fileName.value, omChangeValue = { newName ->
                finalFileName.value = newName
            })
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    launcher.launch("application/pdf")
                }
        ) {
            Column {
                if (!startUpload.value) {
                    UploadIcon(R.raw.upload_pdf)
                    Text(text = "(Upload Your PDF)")
                }
                if (isUploading.value) {
                    UploadIcon(R.raw.loader)
                }
                if (startUpload.value) {
                    SubmitButton(pdfURI, content, currentType.value, {
                        scope.launch { modalSheetState.hide() }
                        Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
                    } ,{
                        scope.launch { modalSheetState.hide() }
                        Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
                    }, {
                        isUploading.value = true
                    }, finalFileName.value, recompose, currentPage)
                }
            }
        }
    }

}

@Composable
fun SubmitButton(
    pdfURI: MutableState<Uri?>,
    content: List<Content>,
    currentType: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
    displayLoader: () -> Unit,
    fileName: String,
    recompose: (String) -> Unit,
    currentPage: Int
) {
    val submitText = remember {
        mutableStateOf("SUBMIT")
    }
    var subjectID : String? = null
    if (content.isNotEmpty()) {
        subjectID = content[0].subjectID
    }
    val scope = rememberCoroutineScope()

    val viewModel: FileUploadViewModel = hiltViewModel()
    val context = LocalContext.current
    val contentResolver = context.contentResolver  // Assuming you have access to the context
    Button(
        onClick = {
            submitText.value = "Uploading Please Wait"
            scope.launch {
                submitPDF(pdfURI, currentType, subjectID, viewModel, contentResolver, context, onSuccess, onFailure, fileName)
                recompose(currentPage.toString())
                displayLoader()
            }

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
    ) {
        androidx.compose.material3.Text(
            text = submitText.value,
        )
    }
}

suspend fun submitPDF(
    pdfURI: MutableState<Uri?>,
    currentType: String,
    subjectID: String?,
    viewModel: FileUploadViewModel,
    contentResolver: ContentResolver,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
    fileName: String
) {
    val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString(Utils.JWT_TOKEN, "")
    pdfURI.value?.let {
        withContext(Dispatchers.IO) {
            viewModel.uploadFile(contentResolver = contentResolver, uri = it,
                UploadFileBody(token?:"", subjectID?:"", currentType, fileName))
        }
    }
    onSuccess()
//    if (viewModel.uploadResult.value == "success") {
//        onSuccess()
//    } else if (viewModel.uploadResult.value == "failure") {
//        onFailure()
//    } else {
////        LoadingScreen()
//    }
}

@Composable
fun UploadIcon(animationId: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animationId))
    com.airbnb.lottie.compose.LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .size(100.dp)
    )
}
object Report {
    const val Sexuality = "Sexuality"
    const val Plagiarism = "Plagiarism"
    const val Irrelevant = "Irrelevant"
}
@Composable
fun AlertExample(onSubmit: (String) -> Unit) {
    var dialogVisibility by remember { mutableStateOf(true) }
    val selectedOption = remember {
        mutableStateOf("Report")
    }
    val context = LocalContext.current
    if (dialogVisibility) {
        AlertDialog(
            onDismissRequest = {
                dialogVisibility = false
            },
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Report",
                        fontSize = 24.sp
                    )
                }
            },
            text = {
                Column {
                    RadioButtonWithText(selectedOption, Report.Irrelevant)
                    RadioButtonWithText(selectedOption, Report.Sexuality)
                    RadioButtonWithText(selectedOption, Report.Plagiarism)
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            dialogVisibility = false
                            Toast.makeText(context, selectedOption.value, Toast.LENGTH_SHORT).show()
                            onSubmit(selectedOption.value)
                        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),

                    ) {
                        androidx.compose.material3.Text("Report")
                    }
                }
            }
        )
    }
}

@Composable
private fun RadioButtonWithText(selectedOption: MutableState<String>, option: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .clickable {
                selectedOption.value = option
            }
    ) {
        RadioButton(
            selected = selectedOption.value == option,
            onClick = {
                selectedOption.value = option
            }
        )
        Spacer(modifier = Modifier.padding(0.dp, 2.dp))
        Text(text = option)
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "Range")
fun downloadPdfNotifination(
    context: Context,
    pdfUrl: String,
    notificationId: Int,
    scope: CoroutineScope,
    activity: ComponentActivity?,
    contentItem: Content
) {
    val downloadManager = context.getSystemService<DownloadManager>()!!
    val uri = Uri.parse(pdfUrl)
    val request = DownloadManager.Request(uri)
        .setTitle("Sample PDF")
        .setDescription("Downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            contentItem.contentID + "UN" + ".pdf"
        )
    val downloadId = downloadManager.enqueue(request)
    val query = DownloadManager.Query().setFilterById(downloadId)


    val notificationManager = NotificationManagerCompat.from(context)

    scope.launch {

        createNotificationChannel(context)
        while (true) {
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        // Download completed
                        val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        Toast.makeText(context, localUri, Toast.LENGTH_SHORT).show()
                        notificationManager.cancel(notificationId)
//                        break


                        val file = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            contentItem.contentID + "UN" + ".pdf"
                        )
                        val file2 = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            contentItem.contentID + ".pdf"
                        )
                        addPasswordToPdf(context, file.absolutePath, file2.absolutePath, PDF_PASSWORD)
                        file.delete()
                    }
                    DownloadManager.STATUS_FAILED -> {
                        // Download failed
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                        notificationManager.cancel(notificationId)
                        break
                    }
                    else -> {
                        // Download in progress
                        val bytesDownloaded =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        showDownloadNotification(
                            context,
                            notificationManager,
                            notificationId,
                            bytesDownloaded,
                            bytesTotal,
                            activity
                        )
                    }
                }
            }
            cursor.close()
            delay(1000) // Update the notification every second
        }
    }
}


private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "download_channel",
            "Download Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showDownloadNotification(
    context: Context,
    notificationManager: NotificationManagerCompat,
    notificationId: Int,
    bytesDownloaded: Int,
    bytesTotal: Int,
    activity: androidx.activity.ComponentActivity?
) {

    val progress = (bytesDownloaded.toFloat() / bytesTotal.toFloat() * 100).toInt()

    val builder = NotificationCompat.Builder(context, "download_channel")
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("Downloading PDF")
        .setContentText("$progress% downloaded")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)
        .setProgress(100, progress, false)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val launcher = activity?.activityResultRegistry?.register(
            "requestPermissionKey",
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, now we can send the notification
                notificationManager.notify(notificationId, builder.build())
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        return
    }
    notificationManager.notify(notificationId, builder.build())
}


fun openPdfInApp(context: Context, file: File) {
    try {
//        val file = File(context.filesDir, pdfFileName)
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)

        // Open the first page of the PDF
        val currentPage = pdfRenderer.openPage(0)

        // Create a bitmap to render the page
        val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)

        // Render the page onto the bitmap
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // Display the bitmap in an ImageView
//        imageView.setImageBitmap(bitmap)

        // Close the page and renderer when done
        currentPage.close()
        pdfRenderer.close()

    } catch (e: Exception) {
        e.printStackTrace()
        Log.i("qwertyuio", e.message.toString())
    }
}



@Composable
fun PdfViewer(
    modifier: Modifier = Modifier,
    uri: Uri,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {
    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val renderer by produceState<PdfRenderer?>(null, uri) {
        rendererScope.launch(Dispatchers.IO) {
            val input = ParcelFileDescriptor.open(uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }
    val context = LocalContext.current
    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
        val height = (width * sqrt(2f)).toInt()
        val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }
        LazyColumn(
            verticalArrangement = verticalArrangement
        ) {
            items(
                count = pageCount,
                key = { index -> "$uri-$index" }
            ) { index ->
                val cacheKey = MemoryCache.Key("$uri-$index")
                val cacheValue : Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

                var bitmap : Bitmap? by remember { mutableStateOf(cacheValue)}
                if (bitmap == null) {
                    DisposableEffect(uri, index) {
                        val job = imageLoadingScope.launch(Dispatchers.IO) {
                            val destinationBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            mutex.withLock {
                                Log.d("PdfGenerator", "Loading PDF $uri - page $index/$pageCount")
                                if (!coroutineContext.isActive) return@launch
                                try {
                                    renderer?.let {
                                        it.openPage(index).use { page ->
                                            page.render(
                                                destinationBitmap,
                                                null,
                                                null,
                                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    //Just catch and return in case the renderer is being closed
                                    return@launch
                                }
                            }
                            bitmap = destinationBitmap
                        }
                        onDispose {
                            job.cancel()
                        }
                    }
                    Box(modifier = Modifier
                        .background(Color.White)
                        .aspectRatio(1f / sqrt(2f))
                        .fillMaxWidth())
                } else { //bitmap != null
                    val request = ImageRequest.Builder(context)
                        .size(width, height)
                        .memoryCacheKey(cacheKey)
                        .data(bitmap)
                        .build()

                    Image(
                        modifier = Modifier
                            .background(Color.White)
                            .aspectRatio(1f / sqrt(2f))
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                        painter = rememberImagePainter(request),
                        contentDescription = "Page ${index + 1} of $pageCount"
                    )
                }
            }
        }
    }
}

fun addPasswordToPdf(context: Context, inputPath: String, outputPath: String, password: String) {
    try {
        val reader = PdfReader(inputPath)
        val fileOutputStream = FileOutputStream(outputPath)
        val stamper = PdfStamper(reader, fileOutputStream)
        stamper.setEncryption(password.toByteArray(), password.toByteArray(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128)

        stamper.close()
        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}