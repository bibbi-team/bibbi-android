package com.no5ing.bbibbi.presentation.feature.view.landing.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun LoginPageContent(
    isLoggingIn: Boolean,
    onTapGoogle: () -> Unit = {},
    onTapKakao: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .padding(vertical = 36.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Spacer(modifier = Modifier.height(30.dp))
                Icon(
                    painter = painterResource(id = R.drawable.login_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    tint = MaterialTheme.bbibbiScheme.white
                )
            }
            Image(
                painter = painterResource(id = R.drawable.login_backgroup),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            KakaoLoginButton(
                isLoggingIn = isLoggingIn,
                onClick = onTapGoogle,
            )
            GoogleLoginButton(
                isLoggingIn = isLoggingIn,
                onClick = onTapGoogle,
            )
        }

    }
}