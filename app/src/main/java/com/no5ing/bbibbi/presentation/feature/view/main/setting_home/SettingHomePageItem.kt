package com.no5ing.bbibbi.presentation.feature.view.main.setting_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
fun SettingHomePageItem(
    name: String,
    onClick: () -> Unit,
    isCritical: Boolean = false,
    rightButton: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = null,
            tint = MaterialTheme.bbibbiScheme.icon,
        )
    }
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = name,
            color = if (isCritical) MaterialTheme.bbibbiScheme.warningRed else MaterialTheme.bbibbiScheme.textPrimary,
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
        )
        rightButton()
    }
}