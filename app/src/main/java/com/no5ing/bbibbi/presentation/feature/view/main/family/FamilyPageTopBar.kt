package com.no5ing.bbibbi.presentation.feature.view.main.family

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun FamilyPageTopBar(
    onDispose: () -> Unit = {},
    onTapSetting: () -> Unit = {},
) {
    DisposableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.family_title),
        rightButton = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onTapSetting() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.setting_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.bbibbiScheme.icon
                )
            }
        }
    )
}