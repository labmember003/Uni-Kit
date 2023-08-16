package com.falcon.unikit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun MainScreen(numberOfYears: Int) {
    for (i in 1..numberOfYears) {
        GraduationYear(i)
    }
}

@Composable
fun GraduationYear(yearNumber: Int) {
    Column {
        Image(painter = painterResource(id = R.drawable.graduation), contentDescription = "")
        Text(text = yearNumber.toString())
    }

}
