package com.no5ing.bbibbi.presentation.ui.feature.landing.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R

@Composable
fun OnBoardingFirstPage() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "매일 낮 12시, 알림을 받으면 \n" +
                    "가족에게 사진을 보내세요",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box {
            Image(
                painter = painterResource(R.drawable.landing_one),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .fillMaxWidth(),
//                contentAlignment = Alignment.TopStart
//            ) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .fillMaxWidth()
//                        .height(147.dp)
//                        .background(
//                            brush = Brush.verticalGradient(
//                                colors = listOf(
//                                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
//                                    MaterialTheme.colorScheme.background
//                                ),
//                                startY = 0.0f,
//                                endY = with(LocalDensity.current) { 71.dp.toPx() },
//                            ),
//                        ),
//                )
//            }
        }
    }
}