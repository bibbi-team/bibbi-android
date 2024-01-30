package com.no5ing.bbibbi.presentation.feature.view.landing.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.messaging.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun GoogleLoginButton(
    isLoggingIn: Boolean,
    onClick: () -> Unit,
) {
    val alphaValue = if (isLoggingIn) 0.5f else 1.0f
    Button(
        colors = ButtonDefaults
            .buttonColors(
                containerColor = MaterialTheme.bbibbiScheme.white.copy(
                    alpha = alphaValue
                )
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
            Image(
                painter = painterResource(id = R.drawable.googleg_standard_color_18),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp),
                alpha = alphaValue,
            )
            Text(
                text = stringResource(id = com.no5ing.bbibbi.R.string.login_with_google),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.bbibbiScheme.backgroundPrimary.copy(
                    alpha = alphaValue
                ),
            )
        }

    }
}