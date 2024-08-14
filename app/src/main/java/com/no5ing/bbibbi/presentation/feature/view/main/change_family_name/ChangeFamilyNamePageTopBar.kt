package com.no5ing.bbibbi.presentation.feature.view.main.change_family_name

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun ChangeFamilyNamePageTopBar(
    onDispose: () -> Unit = {},
    onTapClear: () -> Unit = {}
) {
    DisposableTopBar(
        onDispose = onDispose,
        title = "",
        rightButton = {
            Icon(
                painter = painterResource(id = R.drawable.refresh_icon),
                contentDescription = null,
                tint = MaterialTheme.bbibbiScheme.icon,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(28.dp)
                    .clickable { onTapClear() }
            )
        }
    )
}