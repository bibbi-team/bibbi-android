package com.no5ing.bbibbi.presentation.feature.view.main.post_upload

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.button.CTAButton

@Composable
fun PostUploadPageUploadBar(
    onClickUpload: () -> Unit = {},
    onClickSave: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterHorizontally
        ),
    ) {
        Box(modifier = Modifier.size(48.dp))
        CTAButton(
            text = stringResource(id = R.string.upload_image),
            contentPadding = PaddingValues(horizontal = 60.dp, vertical = 15.dp),
            onClick = onClickUpload
        )
        Image(
            painter = painterResource(R.drawable.save_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    onClickSave()
                }
        )
    }
}