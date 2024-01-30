package com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.ClosableTopBar

@Composable
fun CreateRealEmojiTopBar(
    onDispose: () -> Unit = {},
) {
    ClosableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.real_emoji_upload_title),
    )
}