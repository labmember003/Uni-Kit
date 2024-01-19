package com.falcon.unikit


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.falcon.unikit.screens.RadioButtonWithText
import com.falcon.unikit.screens.Report

@Composable
fun ReportDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    icon: ImageVector,
) {
    val selectedOption = remember {
        mutableStateOf("Report")
    }
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButtonWithText(selectedOption, Report.Irrelevant)
                RadioButtonWithText(selectedOption, Report.Sexuality)
                RadioButtonWithText(selectedOption, Report.Plagiarism)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(selectedOption.value)
                }
            ) {
                Text("Report")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}