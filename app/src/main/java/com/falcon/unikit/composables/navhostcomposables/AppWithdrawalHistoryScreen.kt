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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.unikit.R
import com.falcon.unikit.RedeemData

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun _WithdrawalHistory() {
    WithdrawalHistory(NavHostController(LocalContext.current))
}

@Composable
fun WithdrawalHistory(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = "WithDrawal History",
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
                text = "$ 100",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                color = colorResource(R.color.space_purple)
            )
            Text(
                text = "Current Balance",
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                color = colorResource(R.color.grey)
            )
            Spacer(
                modifier = Modifier
                    .size(24.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        }
        Spacer(
            modifier = Modifier
                .size(24.dp)
        )
        Text(
            text = "History",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        LazyColumn(content = {
            val redeemData = listOf(
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.amazon_icon, "Amazon Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12"),
                RedeemData(R.drawable.paytm_icon, "Paytm Withdrawal $10", "Redeemed on 04 Jan 2024, 12:30 PM", "$12")
            )
            items(redeemData) { content ->
                WithdrawalListItem(content.icon ?: 0, content.title.toString(), content.dateText.toString(), content.cost.toString())
            }
        })
    }
}
@Composable
private fun WithdrawalListItem(icon: Int, title: String, dateText: String, cost: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Redeem Brand Icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                )
                Text(
                    text = dateText,
                    fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
                )
            }
        }
        Text(
            text = "-$cost",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1))
        )
    }
}
