package com.falcon.unikit.composables.navhostcomposables

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.AlertDialogExample
import com.falcon.unikit.R
import com.falcon.unikit.WithdrawalCoins

@Composable
fun Redeem(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        MyBalanceComposable(navController)
        Spacer(modifier = Modifier.size(30.dp))
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(48.dp, 48.dp, 0.dp, 0.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "WithDraw Coins",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.padding(6.dp))
                LazyColumn(content = {
                    val data = listOf(
                        WithdrawalCoins(R.drawable.paytm_icon, "Paytm Withdrawal", "$10", "$12"),
                        WithdrawalCoins(R.drawable.paytm_icon, "Paytm Withdrawal", "$20", "$22"),
                        WithdrawalCoins(R.drawable.amazon_icon, "Amazon Withdrawal", "$10", "$12"),
                        WithdrawalCoins(R.drawable.amazon_icon, "Amazon Withdrawal", "$20", "$22")
                    )
                    items(data) { content ->
                        WithdrawCoinsComposable(content.icon ?: 0, content.title.toString(), content.withdrawalAmount.toString(), content.withdrawalCoins.toString())
                    }
                })
            }
        }
    }
}

@Composable
private fun MyBalanceComposable(navController: NavHostController) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(0.dp, 0.dp, 48.dp, 48.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "My Balance",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(
                modifier = Modifier
                    .size(32.dp)
            )
            Text(
                text = "$ 100.0",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Total Balance",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(32.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    backgroundColor = colorResource(id = R.color.light_purple),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("reward_history")
                        },
                    shape = RoundedCornerShape(48.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_history_24),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(
                            text = "Reward History"
                        )
                    }
                }
                Card(
                    backgroundColor = colorResource(R.color.light_green),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("withdrawal_history")
                        },
                    shape = RoundedCornerShape(48.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.transaction),
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(
                            text = "Recent Transactions"
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun WithdrawCoinsComposable(
    icon: Int,
    title: String,
    withdrawalAmount: String,
    withdrawalCoins: String
) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(48.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "Paytm Icon",
                    modifier = Modifier
                        .size(25.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    )
                    Text(
                        text = withdrawalAmount,
                        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    )
                }
            }
            val openAlertDialog = remember { mutableStateOf(false) }
            when {
                openAlertDialog.value -> {
                    AlertDialogExample(
                        onDismissRequest = { openAlertDialog.value = false },
                        onConfirmation = {
                            openAlertDialog.value = false
//                            TODO ("PROCESS THE PURCHASE")
                            println("Confirmation registered") // Add logic here to handle confirmation.
                        },
                        dialogTitle = "Purchase Confirmation",
                        dialogText = "Are you sure to withdraw $title of $withdrawalAmount for $withdrawalCoins coins ?",
                        icon = Icons.Default.Info
                    )
                }
            }
            Card(
                backgroundColor = colorResource(id = R.color.light_blue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .clickable {
                        openAlertDialog.value = true
                    }
            ) {
                Row(
                    Modifier.padding(24.dp)
                ) {
                    Text(text = withdrawalCoins)
                }

            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
}