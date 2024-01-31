package com.no5ing.bbibbi.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun ClosableTopBar(
    onDispose: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    rightButton: @Composable (() -> Unit) = { Spacer(modifier = Modifier.size(52.dp)) }
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.close_button),
            contentDescription = "Exit Button", // 필수 param
            modifier = Modifier
                .size(52.dp)
                .clickable { onDispose() }
        )
        Text(
            text = title,
            color = MaterialTheme.bbibbiScheme.textPrimary,
            style = MaterialTheme.bbibbiTypo.headTwoBold,
        )
        rightButton()
    }
}