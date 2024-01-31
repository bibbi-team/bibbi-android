package com.no5ing.bbibbi.presentation.feature.view.main.post_upload

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.DisposableTopBar

@Composable
fun PostUploadPageTopBar(
    onDispose: () -> Unit = {},
) {
    DisposableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.upload_post),
    )
}