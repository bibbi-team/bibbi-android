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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo

@Composable
fun OnBoardingFirstPage() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.onboarding_first_title),
            style = MaterialTheme.bbibbiTypo.headOne,
            color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(81.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 42.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.landing_one),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}