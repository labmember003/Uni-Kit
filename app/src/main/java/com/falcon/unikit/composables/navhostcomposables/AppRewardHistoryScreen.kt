package com.falcon.unikit.composables.navhostcomposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.R
import com.falcon.unikit.Reward

@Composable
fun RewardHistory(navController: NavHostController) {
    MyRewardsUpperComposable(navController)
}

@Composable
private fun MyRewardsUpperComposable(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = "Reward History",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(
            modifier = Modifier
                .size(24.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "$ 667.89",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Total Rewards",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = "$ 667.89",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "103 Rewards",
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            LazyColumn(content = {
                val rewardList = listOf(
                    Reward("1 January 2024", "$8.19"),
                    Reward("2 January 2024", "$8.19"),
                    Reward("3 January 2024", "$8.19"),
                    Reward("4 January 2024", "$8.19"),
                    Reward("5 January 2024", "$8.19"),
                    Reward("6 January 2024", "$8.19"),
                    Reward("7 January 2024", "$8.19")
                )
                items(rewardList) { content ->
                    RewardComposable(date = content.date.toString(), amount = content.amount.toString())
                }
            })
        }
    }
}

@Composable
private fun RewardComposable(date: String, amount: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_icon_sec),
                contentDescription = "menu icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
                Text(
                    text = "Reward",
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
            }
        }
        Text(
            text = amount,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
            color = colorResource(id = R.color.greenny)
        )
    }
}