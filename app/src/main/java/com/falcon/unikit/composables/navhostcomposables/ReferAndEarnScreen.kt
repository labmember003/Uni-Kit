package com.falcon.unikit.composables.navhostcomposables

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.falcon.unikit.R
import com.falcon.unikit.TextWithBorderAndCopyIcon

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReferAndEarnScreen() {
    val refferalCode = remember {
        mutableStateOf("ABCDE")
    }
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(24.dp)
    ) {
        Text(
            text = "Refer & Earn",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.refer_and_earn))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(350.dp)
        )
        Text(
            text = "Invite your friends and get $10 each",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_semibold_1)),
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Share the code below or ask them to enter it during they signup. Earn when your friend signs up on your app",
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.nunito_light_1)),
        )
        Spacer(modifier = Modifier.size(20.dp))
        TextWithBorderAndCopyIcon(headingValue = "Referal Code", descriptionValue = refferalCode.value)
        Spacer(modifier = Modifier.size(20.dp))
        Button(onClick = {
            val text = "Hey, I recently stumbled upon this amazing application called Unikit. It serves as a comprehensive hub for all the resources related to my college, accessible with just a single click. What's even more exciting is that you have the opportunity to earn rewards by contributing and uploading your own notes. To explore this all-in-one college resource platform, download the app from the following link: https://play.google.com/store/apps/details?id=com.falcon.unikit. Don't forget to use the referral code ${refferalCode.value} to enhance your experience!"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
        ) {
            Text(
                text = "Invite Friend",
            )
        }
    }

}