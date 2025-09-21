package com.no5ing.bbibbi.presentation.feature.view.main.family_studio_upload_camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.ClosableTopBar

@Composable
fun UploadFamilyStudioTopBar(
    onDispose: () -> Unit = {},
) {
    ClosableTopBar(
        onDispose = onDispose,
        title = "사진 올리기",
    )
}