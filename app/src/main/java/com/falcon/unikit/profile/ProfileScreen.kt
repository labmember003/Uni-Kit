package com.falcon.unikit.profile


import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.falcon.unikit.R
import com.falcon.unikit.TextWithBorder
import com.falcon.unikit.TextWithBorderAndCopyIcon
import com.falcon.unikit.Utils
import com.falcon.unikit.Utils.GENDER
import com.falcon.unikit.api.UserData


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _ProfileScreen() {
    ProfileScreen(
        UserData("Avishisht", "avishishtgupta@gmail.com", "www.google.com", "anfnrfnrnongonlnsdlngoejnofrnonrnv"),
        {

        },
        NavHostController(LocalContext.current)
    )
}
@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        androidx.compose.material3.Text(
            text = "Profile",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(48.dp))
        if(userData?.img != null) {
            AsyncImage(
                model = userData.img,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.material3.Text(
                text = "Content",
                fontSize = 21.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("my_notes")
                }
                .padding(0.dp, 10.dp)
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.material3.Text(
                text = "My Notes",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.nunito)),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.notes_blue),
                contentDescription = "menu icon",
                modifier = Modifier
                    .size(22.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.material3.Text(
                text = "Info",
                fontSize = 21.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            )
        }
        TextWithBorder(headingValue = "Name", descriptionValue = userData?.user?: "INVALID USER")
        TextWithBorder(headingValue = "Email", descriptionValue = userData?.email?: "INVALID USER")
        TextWithBorderAndCopyIcon("User ID", userData?.token ?: "INVALID USER ID")

        val value = remember { mutableStateOf("Gender") }
        var mExpanded by remember { mutableStateOf(false) }
        var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
        val itemList = listOf("Male", "Female", "Mentally ill")
        val sharedPreferences = remember {
            context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
        }
        val icon = if (mExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown
        Column(
            Modifier
                .padding(10.dp, 5.dp)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = sharedPreferences.getString(GENDER, "GENDER").toString(),
                onValueChange = {
                    value.value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        mTextFieldSize = coordinates.size.toSize()
                    }
                ,
                label = {
                    Text(text = value.value, modifier = Modifier
                        .clickable {
                            mExpanded = !mExpanded
                        })
                },
                trailingIcon = {
                    androidx.compose.material3.Icon(icon,"contentDescription",
                        Modifier
                            .size(35.dp)
                            .clickable {
                                mExpanded = !mExpanded
                            }
                    )
                }
            )
            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                    .clickable {
                        mExpanded = true
                    }
            ) {
                itemList.forEach {
                    DropdownMenuItem(onClick = {
                        value.value = it ?: "ERROR: collegeName is NULL"
                        mExpanded = false
                        val editor = sharedPreferences.edit()
                        editor.putString(Utils.GENDER, it)
                        editor.apply()

                    }) {
                        Text(
                            text = it ?: "ERROR: collegeName is NULL",
                            modifier = Modifier
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            androidx.compose.material3.Text(
                text = "Sign Out",
            )
        }
    }
}