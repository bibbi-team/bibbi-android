package com.no5ing.bbibbi.presentation.feature.view.landing.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun KakaoLoginButton(
    isLoggingIn: Boolean,
    onClick: () -> Unit,
) {
    val alphaValue = if (isLoggingIn) 0.5f else 1.0f
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor =
            MaterialTheme.bbibbiScheme.kakaoYellow.copy(alpha = alphaValue)
        ),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.kakao_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(19.dp),
                tint = Color.Black.copy(alpha = alphaValue)
            )
            Text(
                text = stringResource(id = R.string.login_with_kakao),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.bbibbiScheme.backgroundPrimary.copy(alpha = alphaValue),
            )
        }

    }
}
