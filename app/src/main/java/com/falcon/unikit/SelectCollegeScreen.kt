package com.falcon.unikit

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.falcon.unikit.Utils.COLLEGE_ID
import com.falcon.unikit.Utils.COLLEGE_NAME
import com.falcon.unikit.models.item.CollegeItem


@Composable
fun SelectCollegeScreen(
    itemList: List<CollegeItem>,
    title: String,
    sharedPrefTitle: String,
    sharedPreferences: SharedPreferences,
    onClick: (collegeID: String?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier
            .size(100.dp))
        Text(
            text = title,
            fontSize = 18.sp
        )
        Text(
            text = "Can Be Changed Anytime From Settings",
            color = Color.Gray,
            fontSize = 12.sp
        )
        LottieAnimation(animationID = R.raw.university)


        var value by remember { mutableStateOf(sharedPrefTitle) }
        var college by remember { mutableStateOf(CollegeItem(null, null, null)) }
        var mExpanded by remember { mutableStateOf(false) }
        var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
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
                value = value,
                onValueChange = {
                    value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        mTextFieldSize = coordinates.size.toSize()
                    }
                ,
                label = {
                    androidx.compose.material.Text(text = sharedPrefTitle, modifier = Modifier
                        .clickable {
                            mExpanded = !mExpanded
                        })
                },
                trailingIcon = {
                    Icon(icon,"contentDescription",
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
                        value = it.collegeName ?: "ERROR: collegeName is NULL"
                        mExpanded = false
                        college = it

                        val editor = sharedPreferences.edit()
                        editor.putString(COLLEGE_NAME, college.collegeName)
                        editor.putString(COLLEGE_ID, college.collegeID)
                        editor.apply()

                    }) {
                        androidx.compose.material.Text(
                            text = it.collegeName ?: "ERROR: collegeName is NULL",
                            modifier = Modifier
                        )
                    }
                }
            }
        }








        Spacer(modifier = Modifier
            .size(20.dp))
        Button(onClick = {
            onClick(college.collegeID)
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
        ) {
            Text(
                text = "NEXT",
            )
        }
    }
}